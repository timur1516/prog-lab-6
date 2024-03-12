package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import client.Controllers.CommandsController;
import client.Readers.WorkerReader;
import common.Console;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commandsController = new CommandsController(workerReader, client);
        interactiveMode();
    }

    /**
     * method which is used to work with script file
     * @throws Exception if any error occurred in process of executing
     */
    public static void scriptMode() throws Exception {
        while(Console.getInstance().hasNextLine()) {
            String s = Console.getInstance().readLine();
            String[] input = (s.trim() + " ").split(" ");
            if(input.length == 0) continue;
            String commandName = input[0];
            Console.getInstance().printLn(commandName);
            String[] commandArgs = Arrays.copyOfRange(input, 1, input.length);

            UserCommand command = commandsController.launchCommand(commandName, commandArgs);
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
            String s = Console.getInstance().readLine();
            String[] input = (s.trim() + " ").split(" ");
            if(input.length == 0) continue;
            String commandName = input[0];
            String[] commandArgs = Arrays.copyOfRange(input, 1, input.length);

            UserCommand command;
            try {
                command = commandsController.launchCommand(commandName, commandArgs);
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