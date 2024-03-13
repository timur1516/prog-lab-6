package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Constants;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_lower command
 * <p>This command is used to remove all elements which are lower than given
 * @see UserCommand
 */
public class RemoveLowerCommand extends UserCommand {
    /**
     * Worker reader which is used to read element from user
     */
    private WorkerReader workerReader;

    private UDPClient client;

    /**
     * RemoveLowerCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public RemoveLowerCommand(WorkerReader workerReader, UDPClient client) {
        super("remove_lower", "{element}", "remove all elements which are lower than given");
        this.workerReader = workerReader;
        this.client = client;
    }

    /**
     * Method to complete remove_lower command
     * <p>It reads element to compare with and then removes elements which are lower that it
     * <p>In the end it prints number of deleted elements
     * <p>If collection is empty element is not read (except script mode)
     *
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            client.sendObject(new ClientRequest(ClientRequestType.IS_COLLECTION_EMPTY, null));
            if ((boolean)(client.receiveObject())) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readWorker();
                }
                return new ExecuteCommandResponce(ResultState.SUCCESS, "Collection is empty!");
            }
            Worker worker = this.workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(worker);
            client.sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
            return (ExecuteCommandResponce) client.receiveObject();
        } catch (Exception e){
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
