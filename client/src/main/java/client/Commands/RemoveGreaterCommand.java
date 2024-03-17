package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Constants;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_greater command
 * <p>This command is used to remove all elements which are greater than given
 * @see UserCommand
 */
public class RemoveGreaterCommand extends UserCommand {
    /**
     * Worker reader which is used to read element from user
     */
    private WorkerReader workerReader;

    /**
     * RemoveGreaterCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public RemoveGreaterCommand(WorkerReader workerReader) {
        super("remove_greater", "{element}", "remove all elements which are greater than given");
        this.workerReader = workerReader;
    }

    /**
     * Method to complete remove_greater command
     * <p>It reads element to compare with and then removes elements which are greater that it
     * <p>In the end it prints number of deleted elements
     * <p>If collection is empty element is not read (except script mode)
     *
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.IS_COLLECTION_EMPTY, null));
            if ((boolean)(UDPClient.getInstance().receiveObject())) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readWorker();
                }
                return new ExecuteCommandResponce(ResultState.SUCCESS, "Collection is empty!");
            }
            Worker worker = this.workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(worker);
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
            return (ExecuteCommandResponce) UDPClient.getInstance().receiveObject();
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
