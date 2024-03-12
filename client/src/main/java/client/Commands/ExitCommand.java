package client.Commands;

import client.UDPClient;
import client.UI.YesNoQuestionAsker;
import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of exit command
 * <p>This command is used to finish program
 * @see UserCommand
 */
public class ExitCommand extends UserCommand {
    private UDPClient client;
    /**
     * ExitCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExitCommand(UDPClient client) {
        super("exit", "stop program without saving collection");
        this.client = client;
    }

    /**
     * Method to complete exit command
     * <p>It checks if collection was changed after last save and tell user if it wasn't
     * <p>After this it asks user if he really wants to exit
     */
    @Override
    public ExecuteCommandResponce execute() {
        YesNoQuestionAsker questionAsker = new YesNoQuestionAsker("Do you want to exit?");
        if(questionAsker.ask()) {
            System.exit(0);
        }
        return new ExecuteCommandResponce(ResultState.SUCCESS, "Exit canceled");
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(!arguments.isEmpty()){
            throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
        }
    }
}
