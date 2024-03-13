package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of add command
 * <p>This command is used to add new element to collection
 * @see UserCommand
 */
public class AddCommand extends UserCommand {
    /**
     * Worker reader which is used to read new element from user
     */
    private WorkerReader workerReader;

    private UDPClient client;

    /**
     * AddCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public AddCommand(WorkerReader workerReader, UDPClient client) {
        super("add", "{element}", "add new element to collection");
        this.workerReader = workerReader;
        this.client = client;
    }

    /**
     * Method to complete add command
     * <p>It reads new element and then adds it to collection
     *
     * @throws InvalidDataException If an error occurred while reading
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            Worker worker = this.workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(worker);
            this.client.sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
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
        if(!arguments.isEmpty()) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
    }
}
