package client;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import client.Commands.*;
import common.Commands.HelpCommand;
import common.Controllers.CommandsController;
import client.Readers.WorkerReader;
import common.UI.CommandReader;
import common.UI.Console;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;
import common.requests.PackedCommand;

/**
 * Main app class
 * <p>Completes initialization of all controllers, sets default input stream for Console
 * <p>In the beginning loads data file (if it is wrong program stops), then calls interactiveMode method
 */
public class Main {
    private static WorkerReader workerReader;
    /**
     * Controller of commands
     */
    private static CommandsController commandsController;

    private static UDPClient client;
    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers and start handling user commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        Console.getInstance().setScanner(new Scanner(System.in));
        workerReader = new WorkerReader();

        try {
            client = new UDPClient(InetAddress.getLocalHost(), 8081);
            client.open();
        } catch (UnknownHostException e) {
            Console.getInstance().printError("Server host was not found!");
            System.exit(0);
        } catch (SocketException e) {
            Console.getInstance().printError("Error while starting client!");
            System.exit(0);
        }

        commandsController = new CommandsController();
        commandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new HelpCommand(commandsController),
                        new InfoCommand(client),
                        new ShowCommand(client),
                        new AddCommand(workerReader, client),
                        new UpdateByIdCommand(workerReader, client),
                        new RemoveByIdCommand(client),
                        new ClearCommand(client),
                        new ExecuteScriptCommand(),
                        new ExitCommand(client),
                        new RemoveFirstCommand(client),
                        new RemoveGreaterCommand(workerReader, client),
                        new RemoveLowerCommand(workerReader, client),
                        new MinBySalaryCommand(client),
                        new FilterLessThanEndDateCommand(workerReader, client),
                        new PrintFieldDescendingSalaryCommand(client)
                ))
        );
        interactiveMode();
    }

    /**
     * method which is used to work with script file
     * @throws Exception if any error occurred in process of executing
     */
    public static void scriptMode() throws Exception {
        while(Console.getInstance().hasNextLine()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            Console.getInstance().printLn(packedCommand.commandName());

            UserCommand command = commandsController.launchCommand(packedCommand);
            ExecuteCommandResponce responce = command.execute();
            switch (responce.state()){
                case SUCCESS:
                    Console.getInstance().printLn(responce.data());
                    break;
                case EXCEPTION:
                    throw (Exception) responce.data();
            }
        }
    }

    /**
     * Method to handle user input
     *
     * <p>Reads commands from user, gets their name and arguments, launch command and execute it
     * <p>If any error is occurred method prints error message and continues to read data
     */
    public static void interactiveMode(){
        while(Console.getInstance().hasNextLine()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            if(packedCommand == null) continue;
            UserCommand command;
            try {
                command = commandsController.launchCommand(packedCommand);
            }
            catch (Exception e){
                Console.getInstance().printError(e.getMessage());
                continue;
            }
            ExecuteCommandResponce responce = command.execute();
            switch (responce.state()){
                case SUCCESS:
                    Console.getInstance().printLn(responce.data());
                    break;
                case EXCEPTION:
                    Console.getInstance().printError(((Exception) responce.data()).getMessage());
            }
        }
    }
}