package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Constants;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Parsers.WorkerParsers;
import common.UserCommand;
import common.Validators.WorkerValidators;
import common.requests.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Class with realization of update command
 * <p>This command is used to update value of collection element which id is equal to given
 * @see UserCommand
 */
public class UpdateByIdCommand extends UserCommand {
    /**
     * Worker reader which is used to read new element from user
     */
    private WorkerReader workerReader;

    UDPClient client;
    /**
     * id of element to update
     */
    private long id;

    /**
     * UpdateByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public UpdateByIdCommand(WorkerReader workerReader, UDPClient client) {
        super("update", "id {element}", "update value of collection element which id is equal to given");
        this.workerReader = workerReader;
        this.client = client;
    }

    /**
     * Method to complete update command
     * <p>It reads new element from user and then updates value of element with given id inside collection
     *
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ExecuteCommandResponce execute() {
        try {
            client.sendObject(new ClientRequest(ClientRequestType.CHECK_ID, id));
            if (!(boolean)(client.receiveObject())) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readWorker();
                }
                return new ExecuteCommandResponce(ResultState.EXCEPTION,
                        new NoSuchElementException("No element with such id!"));
            }
            Worker worker = workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(id);
            arguments.add(worker);
            client.sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new ExecuteCommandRequest(super.getName(), arguments)));
            return (ExecuteCommandResponce) client.receiveObject();
        } catch (Exception e){
            return new ExecuteCommandResponce(ResultState.EXCEPTION, e);
        }
    }

    /**
     * Method checks if amount arguments is correct and validates id
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to one
     * @throws InvalidDataException            If given id is not valid
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException, InvalidDataException {
        if (arguments.size() != 1) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 1, arguments.size());
        this.id = WorkerParsers.longParser.parse((String) arguments.get(0));
        WorkerValidators.idValidator.validate(id);
    }
}
