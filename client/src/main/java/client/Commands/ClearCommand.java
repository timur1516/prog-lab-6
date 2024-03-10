package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of clear command
 * <p>This command is used to clear collection
 * @see UserCommand
 */
public class ClearCommand extends UserCommand {

    private UDPClient client;
    /**
     * ClearCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ClearCommand(UDPClient client) {
        super("clear", "delete all element from collection");
        this.client = client;
    }

    /**
     * Method to complete clear command
     * <p>It clears collection
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            this.client.sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new ExecuteCommandRequest(super.getName(), new ArrayList<>())));
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
     * @throws WrongAmountOfArgumentsException If it is more than zero arguments
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(!arguments.isEmpty()){
            throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
        }
    }
}
