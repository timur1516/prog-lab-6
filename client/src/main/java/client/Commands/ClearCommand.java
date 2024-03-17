package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of clear command
 * <p>This command is used to clear collection
 * @see UserCommand
 */
public class ClearCommand extends UserCommand {
    /**
     * ClearCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ClearCommand() {
        super("clear", "delete all element from collection");
    }

    /**
     * Method to complete clear command
     * <p>It clears collection
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), new ArrayList<>())));
            return (ExecuteCommandResponce) UDPClient.getInstance().receiveObject();
        }
        catch (Exception e) {
            return new ExecuteCommandResponce(ResultState.EXCEPTION, e);
        }
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If it is more than zero arguments
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(!arguments.isEmpty()){
            throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
        }
    }
}
