package server;

import common.Commands.HelpCommand;
import common.FileLoader;
import common.UI.CommandReader;
import common.requests.*;
import common.Collection.*;
import common.UserCommand;
import common.UI.Console;
import server.Commands.*;
import server.Controllers.CollectionController;
import common.Controllers.CommandsController;
import server.Controllers.DataFileController;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class Main {
    public static UDPServer server;

    private static Selector selector;
    /**
     * Controller of collection
     */
    private static CollectionController collectionController;
    /**
     * Controller of commands
     */
    private static CommandsController clientCommandsController;

    private static CommandsController serverCommandsController;
    /**
     * Controller of data file
     */
    private static DataFileController dataFileController;

    private static Reader reader;

    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers and start handling user commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        reader = new InputStreamReader(System.in);
        Console.getInstance().setScanner(new Scanner(reader));

        server = new UDPServer(8081);
        try {
            server.open();
            selector = Selector.open();
            server.registerSelector(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            Console.getInstance().printError("Error while starting server!");
            Console.getInstance().printError(e.getMessage());
            System.exit(0);
        }

        collectionController = new CollectionController(loadData());

        clientCommandsController = new CommandsController();
        clientCommandsController.setCommandsList(
               new ArrayList<>(Arrays.asList(
                        new InfoCommand(collectionController),
                        new ShowCommand(collectionController),
                        new AddCommand(collectionController),
                        new UpdateByIdCommand(collectionController),
                        new RemoveByIdCommand(collectionController),
                        new ClearCommand(collectionController),
                        new RemoveFirstCommand(collectionController),
                        new RemoveGreaterCommand(collectionController),
                        new RemoveLowerCommand(collectionController),
                        new MinBySalaryCommand(collectionController),
                        new FilterLessThanEndDateCommand(collectionController),
                        new PrintFieldDescendingSalaryCommand(collectionController)
                ))
        );
        serverCommandsController = new CommandsController();
        serverCommandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new SaveCommand(collectionController, dataFileController),
                        new ExitCommand(serverCommandsController),
                        new HelpCommand(serverCommandsController)
                ))
        );
        interactiveMode();
    }

    /**
     * Method to handle user input
     *
     * <p>Reads commands from user, gets their name and arguments, launch command and execute it
     * <p>If any error is occurred method prints error message and continues to read data
     */
    public static void interactiveMode() {
        while(true) {
            try {
                askClient();
                askAdmin();
            } catch (IOException e) {
                Console.getInstance().printError("An error occurred while reading reading request!");
                Console.getInstance().printError(e.getMessage());
            } catch (ClassNotFoundException e) {
                Console.getInstance().printError("Unknown class received from client!");
                Console.getInstance().printError(e.getMessage());
            }
        }
    }

    private static void askClient() throws IOException, ClassNotFoundException {
        if(selector.selectNow() == 0) return;
        Set<SelectionKey> keys = selector.selectedKeys();
        for (var iter = keys.iterator(); iter.hasNext();){
            SelectionKey key = iter.next(); iter.remove();
            if(key.isValid()){
                if(key.isReadable()){
                    ClientRequest clientRequest = (ClientRequest) server.receiveObject();
                    handleClientRequest(clientRequest);
                }
            }
        }
    }

    private static void askAdmin() throws IOException {
        if(reader.ready()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            if(packedCommand != null) handleAdminRequest(packedCommand);
        }
    }

    private static void handleAdminRequest(PackedCommand packedCommand) {
        UserCommand command;
        try {
            command = serverCommandsController.launchCommand(packedCommand);
        } catch (Exception e) {
            Console.getInstance().printError(e.getMessage());
            return;
        }
        ExecuteCommandResponce responce = command.execute();
        switch (responce.state()) {
            case SUCCESS:
                Console.getInstance().printLn(responce.data());
                break;
            case EXCEPTION:
                Console.getInstance().printError(((Exception) responce.data()).getMessage());
        }
    }

    private static void handleClientRequest(ClientRequest clientRequest) throws IOException {
        switch (clientRequest.type()) {
            case EXECUTE_COMMAND:
                PackedCommand packedCommand = (PackedCommand) clientRequest.data();
                ExecuteCommandResponce executeCommandResponce = null;
                try {
                    UserCommand command = clientCommandsController.launchCommand(packedCommand);
                    executeCommandResponce = command.execute();
                } catch (Exception e) {
                    executeCommandResponce = new ExecuteCommandResponce(ResultState.EXCEPTION, e);
                } finally {
                    server.sendObject(executeCommandResponce);
                }
                break;
            case CHECK_ID:
                long id = (long) clientRequest.data();
                server.sendObject(collectionController.containsId(id));
                break;
            case IS_COLLECTION_EMPTY:
                server.sendObject(collectionController.getCollection().isEmpty());
                break;
        }
    }


    private static String readFileName(){
        Console.getInstance().print("Enter environmental variable name: ");
        String envName = Console.getInstance().readLine().trim();
        String dataFilePath = System.getenv(envName);
        if(dataFilePath == null){
            Console.getInstance().printError("Environmental variable is not defined!");
            System.exit(0);
        }
        return dataFilePath;
    }

    /**
     * Method to load collection from data file.
     * <p>Method also completes validation of filePath and collection inside dataFile
     * @return Collection of workers
     * @see DataFileController
     * @see CollectionController
     */
    private static PriorityQueue<Worker> loadData(){
        String dataFilePath = readFileName();

        PriorityQueue<Worker> data = null;
        File dataFile = null;

        try {
            dataFile = new FileLoader().loadFile(dataFilePath, "json", "rw", "data file");
        } catch (FileNotFoundException e) {
            Console.getInstance().printError(e.getMessage());
            System.exit(0);
        }

        dataFileController = new DataFileController(dataFile);

        try {
            data = dataFileController.readJSON();
        } catch (Exception e) {
            Console.getInstance().printError("Data file reading error!");
            System.exit(0);
        }
        if(data == null) data = new PriorityQueue<>();
        if(!CollectionController.isValid(data)){
            Console.getInstance().printError("Data file is not valid!");
            System.exit(0);
        }
        Console.getInstance().printLn("Data loaded successfully!");
        return data;
    }
}