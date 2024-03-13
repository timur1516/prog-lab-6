package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UserCommand;
import common.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of min_by_salary command
 * <p>This command is used to print any element from collection which salary field is minimal
 * @see UserCommand
 */
public class MinBySalaryCommand extends UserCommand {
    private UDPClient client;
    /**
     * MinBySalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public MinBySalaryCommand(UDPClient client) {
        super("min_by_salary", "print any element from collection which salary field is minimal");
        this.client = client;
    }

    /**
     * Method to complete min_by_salary command
     * <p>It prints element with minimal salary
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
