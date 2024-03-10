package server.Commands;

import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.ICommand;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;
import common.requests.ResultState;
import server.Controllers.CollectionController;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Class with realization of filter_less_than_end_date command
 * <p>This command is used to print all elements whose endDate is less than given
 * @see UserCommand
 * @see ICommand
 */
public class FilterLessThanEndDateCommand extends UserCommand {
    /**
     * Controller of collection which is used to get list of filtered elements
     */
    private CollectionController collectionController;

    private LocalDateTime endDate;
    /**
     * FilterLessThanEndDateCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public FilterLessThanEndDateCommand(CollectionController collectionController) {
        super("filter_less_than_end_date", "endDate", "print all elements whose endDate is less than given");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete filter_less_than_end_date command
     * <p>It reads endDate from user and then print all elements whose endDate is less than given
     * <p>If collection is empty endDate is not read (except script mode)
     *
     * @return
     */
    @Override
    public ExecuteCommandResponce execute() {
        return new ExecuteCommandResponce(ResultState.SUCCESS,
                this.collectionController.getLessThanEndDate(endDate).toString());
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException, WrongAmountOfArgumentsException {
        this.endDate = (LocalDateTime) arguments.get(0);
    }
}
