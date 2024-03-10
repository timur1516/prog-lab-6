package server;

import common.FileLoader;
import common.requests.*;
import common.Collection.*;
import common.UserCommand;
import common.Console;
import server.Controllers.CollectionController;
import server.Controllers.CommandsController;
import server.Controllers.DataFileController;

import java.io.*;
import java.util.*;

public class Main {
    public static UDPServer server;
    /**
     * Controller of collection
     */
    private static CollectionController collectionController;
    /**
     * Controller of commands
     */
    private static CommandsController commandsController;
    /**
     * Controller of data file
     */
    private static DataFileController dataFileController;

    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers and start handling user commands
     * @param args (not used)
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Console.getInstance().setScanner(new Scanner(System.in));
        server = new UDPServer(8081);
        collectionController = new CollectionController(loadData());
        commandsController = new CommandsController(collectionController, dataFileController);
        interactiveMode();
    }

    /**
     * Method to handle user input
     *
     * <p>Reads commands from user, gets their name and arguments, launch command and execute it
     * <p>If any error is occurred method prints error message and continues to read data
     */
    public static void interactiveMode() throws IOException, ClassNotFoundException {
        while(true) {
            ClientRequest clientRequest = (ClientRequest) server.receiveObject();
            switch (clientRequest.type()) {
                case EXECUTE_COMMAND:
                    ExecuteCommandRequest executeCommandRequest = (ExecuteCommandRequest) clientRequest.data();
                    ExecuteCommandResponce executeCommandResponce = null;
                    try {
                        UserCommand command = commandsController.launchCommand(executeCommandRequest.commandName(), executeCommandRequest.arguments());
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