package server.Commands;

import common.Exceptions.ExitingException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.UI.YesNoQuestionAsker;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponce;
import common.net.requests.PackedCommand;
import common.net.requests.ResultState;
import common.Controllers.CommandsController;
import server.Main;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of exit command
 * <p>This command is used to finish program
 * @see UserCommand
 */
public class ExitCommand extends UserCommand {
    private CommandsController commandsController;
    /**
     * ExitCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExitCommand(CommandsController commandsController) {
        super("exit", "stop program without saving collection");
        this.commandsController = commandsController;
    }

    /**
     * Method to complete exit command
     * <p>It checks if collection was changed after last save and tell user if it wasn't
     * <p>After this it asks user if he really wants to exit
     */
    @Override
    public ExecuteCommandResponce execute() {
        YesNoQuestionAsker questionAsker = new YesNoQuestionAsker("Do you want to exit?");
        if(questionAsker.ask()) {
            try {
                UserCommand saveCommad = this.commandsController
                        .launchCommand(new PackedCommand("save", new ArrayList<>()));
                ExecuteCommandResponce responce = saveCommad.execute();
                if(responce.state() == ResultState.EXCEPTION) throw (Exception) responce.data();
            } catch (Exception e) {
                String message = "Collection wasn't saved!\n" +
                        e.getMessage() + "\n Exit canceled!";
                return new ExecuteCommandResponce(ResultState.EXCEPTION, new ExitingException(message));
            }
            try {
                Main.server.stop();
            } catch (IOException e) {
                return new ExecuteCommandResponce(ResultState.EXCEPTION, new ExitingException("Could not stop server!"));
            }
            System.exit(0);
        }
        return new ExecuteCommandResponce(ResultState.SUCCESS, "Exit canceled");
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(!arguments.isEmpty()){
            throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 0, arguments.size());
        }
    }
}
