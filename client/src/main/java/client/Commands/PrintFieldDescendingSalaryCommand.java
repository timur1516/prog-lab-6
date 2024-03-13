package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of print_field_descending_salary command
 * <p>This command is used to print values of all salary fields in collection in descending order
 * @see UserCommand
 */
public class PrintFieldDescendingSalaryCommand extends UserCommand {
    UDPClient client;
    /**
     * PrintFieldDescendingSalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public PrintFieldDescendingSalaryCommand(UDPClient client) {
        super("print_field_descending_salary", "print values of all salary fields in collection in descending order");
        this.client = client;
    }

    /**
     * Method to complete print_field_descending_salary command
     * <p>It prints list of all salaries in descending order
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
        if(!arguments.isEmpty()) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
    }
}
