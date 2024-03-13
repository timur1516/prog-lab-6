package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_first command
 * <p>This command is used to remove first element from collection
 * @see UserCommand
 */
public class RemoveFirstCommand extends UserCommand {
    UDPClient client;
    /**
     * RemoveFirstCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveFirstCommand(UDPClient client) {
        super("remove_first", "remove first element from collection");
        this.client = client;
    }

    /**
     * Method to complete remove_first command
     * <p>It removes first element from collection
     * <p>If collection is empty user is informed
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            this.client.sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), new ArrayList<>())));
            return (ExecuteCommandResponce) this.client.receiveObject();
        }
        catch (Exception e) {
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
        if (!arguments.isEmpty()) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
    }
}
