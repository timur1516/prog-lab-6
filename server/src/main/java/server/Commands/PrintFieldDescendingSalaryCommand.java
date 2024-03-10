package server.Commands;

import common.ICommand;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;
import common.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of print_field_descending_salary command
 * <p>This command is used to print values of all salary fields in collection in descending order
 * @see UserCommand
 * @see ICommand
 */
public class PrintFieldDescendingSalaryCommand extends UserCommand {
    /**
     * Controller of collection which is used to get sorted list of all salaries
     */
    private CollectionController collectionController;

    /**
     * PrintFieldDescendingSalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public PrintFieldDescendingSalaryCommand(CollectionController collectionController) {
        super("print_field_descending_salary", "print values of all salary fields in collection in descending order");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete print_field_descending_salary command
     * <p>It prints list of all salaries in descending order
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ExecuteCommandResponce execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ExecuteCommandResponce(ResultState.SUCCESS,"Collection is empty!");
        }
        return new ExecuteCommandResponce(ResultState.SUCCESS,
                this.collectionController.getDescendingSalaries().toString());
    }
}
