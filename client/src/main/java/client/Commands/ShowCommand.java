package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of show command
 * <p>This command is used to print all elements of collection
 * @see UserCommand
 */
public class ShowCommand extends UserCommand {

    /**
     * ShowCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ShowCommand() {
        super("show", "print all elements of collection");
    }

    /**
     * Method to complete show command
     * <p>It gets collection from collection controller and then prints it
     * <p>If collection is empty user is informed
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), new ArrayList<>())));
            return (ExecuteCommandResponce) UDPClient.getInstance().receiveObject();
        } catch (Exception e) {
            return new ExecuteCommandResponce(ResultState.EXCEPTION, e);
        }
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(!arguments.isEmpty()) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
    }
}
