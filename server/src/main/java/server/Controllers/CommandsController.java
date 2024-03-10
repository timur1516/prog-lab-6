package server.Controllers;


import common.UserCommand;
import server.Commands.*;

import common.Exceptions.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
     * @param collectionController
     * @param dataFileController
     */
    public CommandsController(CollectionController collectionController, DataFileController dataFileController){
        this.commandsList  = new ArrayList<>(Arrays.asList(
                new InfoCommand(collectionController),
                new ShowCommand(collectionController),
                new AddCommand(collectionController),
                new UpdateByIdCommand(collectionController),
                new RemoveByIdCommand(collectionController),
                new ClearCommand(collectionController),
                new RemoveFirstCommand(collectionController),
                new SaveCommand(collectionController, dataFileController),
                new RemoveGreaterCommand(collectionController),
                new RemoveLowerCommand(collectionController),
                new MinBySalaryCommand(collectionController),
                new FilterLessThanEndDateCommand(collectionController),
                new PrintFieldDescendingSalaryCommand(collectionController)
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
     * @return UserCommand object
     * @throws WrongAmountOfArgumentsException If number of arguments is wrong for given command
     * @throws NoSuchElementException If command not found
     * @throws InvalidDataException if command argument are not valid
     */
    public UserCommand launchCommand(String commandName, ArrayList<Serializable> arguments) throws InvalidDataException, WrongAmountOfArgumentsException, NoSuchElementException {
        if(this.commandsList.stream().noneMatch(userCommand -> userCommand.getName().equals(commandName))){
            throw new NoSuchElementException("Command '" + commandName + "' not found!");
        }

        UserCommand command;

        command = this.commandsList
                .stream()
                .filter(userCommand -> userCommand.getName().equals(commandName))
                .findFirst().get();
        command.initCommandArgs(arguments);

        return command;
    }
}
