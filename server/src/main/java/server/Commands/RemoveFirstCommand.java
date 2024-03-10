package server.Commands;

import common.ICommand;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;
import common.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of remove_first command
 * <p>This command is used to remove first element from collection
 * @see UserCommand
 * @see ICommand
 */
public class RemoveFirstCommand extends UserCommand {
    /**
     * Controller of collection which is used to remove element
     */
    private CollectionController collectionController;

    /**
     * RemoveFirstCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public RemoveFirstCommand(CollectionController collectionController) {
        super("remove_first", "remove first element from collection");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete remove_first command
     * <p>It removes first element from collection
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ExecuteCommandResponce execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ExecuteCommandResponce(ResultState.SUCCESS, "Collection is empty!");
        }
        this.collectionController.removeFirst();
        return new ExecuteCommandResponce(ResultState.SUCCESS,
                "Element removed successfully!");
    }
}
