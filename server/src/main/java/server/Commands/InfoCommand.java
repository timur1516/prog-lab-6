package server.Commands;

import common.ICommand;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;
import common.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of info command
 * <p>This command is used to print information about collection
 * @see UserCommand
 * @see ICommand
 */
public class InfoCommand extends UserCommand {
    /**
     * Controller of collection which is used get information about collection
     */
    private CollectionController collectionController;

    /**
     * InfoCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public InfoCommand(CollectionController collectionController) {
        super("info", "print information about collection");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete info command
     * <p>It prints info from collection controller
     *
     * @return
     */
    @Override
    public ExecuteCommandResponce execute() {
        return new ExecuteCommandResponce(ResultState.SUCCESS,
                this.collectionController.getInfo());
    }
}
