package client.Controllers;

import client.Commands.*;
import client.Readers.WorkerReader;
import client.UDPClient;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class which is used to work with UserCommand objects
 */
public class CommandsController {
    /**
     * List with all available commands
     */
    private final ArrayList<UserCommand> commandsList;

    /**
     * CommandsController constructor
     * <p>Gets all required controllers and initialize all commands
     * @param workerReader
     */
    public CommandsController(WorkerReader workerReader, UDPClient client){
        this.commandsList  = new ArrayList<>(Arrays.asList(
                new HelpCommand(this),
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
        ));
    }

    /**
     * Method to get list of commands
     * @return ArrayList of UserCommand
     */
    public ArrayList<UserCommand> getCommandsList() {
        return commandsList;
    }

    /**
     * Method to find command by its name and init its argument
     * @param commandName Name of command to find
     * @param commandArgs Arguments of command
     * @return UserCommand object
     * @throws WrongAmountOfArgumentsException If number of arguments is wrong for given command
     * @throws NoSuchElementException If command not found
     * @throws InvalidDataException if command argument are not valid
     */
    public UserCommand launchCommand(String commandName, String[] commandArgs) throws InvalidDataException, WrongAmountOfArgumentsException, NoSuchElementException {
        if(this.commandsList.stream().noneMatch(userCommand -> userCommand.getName().equals(commandName))){
            throw new NoSuchElementException("Command '" + commandName + "' not found!");
        }

        UserCommand command;

        command = this.commandsList
                .stream()
                .filter(userCommand -> userCommand.getName().equals(commandName))
                .findFirst().get();

        command.initCommandArgs(new ArrayList<>(List.of(commandArgs)));

        return command;
    }
}
