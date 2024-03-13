package client.Commands;

import client.Main;
import common.Constants;
import common.Exceptions.RecursiveScriptException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.FileLoader;
import common.UserCommand;
import common.requests.ExecuteCommandResponce;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import common.UI.Console;
import common.requests.ResultState;

/**
 * Class with realization of execute_script command
 * <p>This command is used to execute script file with commands
 * @see UserCommand
 */
public class ExecuteScriptCommand extends UserCommand {
    /**
     * Path to script file
     */
    private String scriptFilePath;

    /**
     *  ExecuteScriptCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExecuteScriptCommand() {
        super("execute_script", "file_name", "read and execute script from given file");
    }

    /**
     * Method to complete execute_script command
     * <p>Firstly it completes validation of path to script file
     * <p>Than file is checked to recursive script (stack of script files is used
     * <p>Eventually it sets script mode, changes Console inputStream to scriptFile and calls scriptMode
     * <p>Regardless of the result of the script execution Script mode is removed and Console inputString is returned to previous value
     *
     * @throws Exception If any error occurred while executing script
     */
    @Override
    public ExecuteCommandResponce execute() {

        File scriptFile;
        try {
            scriptFile = new FileLoader().loadFile(scriptFilePath, "txt", "r", "Script file");
        } catch (FileNotFoundException e) {
            return new ExecuteCommandResponce(ResultState.EXCEPTION, e);
        }

        if(!Constants.scriptStack.isEmpty() && Constants.scriptStack.contains(scriptFilePath)){
            return new ExecuteCommandResponce(ResultState.EXCEPTION, new RecursiveScriptException("Script is recursive!"));
        }

        Scanner prevScanner = Console.getInstance().getScanner();
        try {
            Console.getInstance().setScanner(new Scanner(new FileInputStream(scriptFile)));
        } catch (FileNotFoundException e) {
            return new ExecuteCommandResponce(ResultState.EXCEPTION, "Script file reading error!");
        }

        Constants.scriptStack.push(scriptFilePath);

        Constants.SCRIPT_MODE = true;

        ExecuteCommandResponce responce;

        try {
            Main.scriptMode();
            responce = new ExecuteCommandResponce(ResultState.SUCCESS,"Script executed successfully!");
        }
        catch (Exception e){
            responce = new ExecuteCommandResponce(ResultState.EXCEPTION, e);
        }
        finally {
            Constants.scriptStack.pop();
            Constants.SCRIPT_MODE = false;
            Console.getInstance().setScanner(prevScanner);
        }
        return responce;
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException {
        if(arguments.size() != 1) throw new WrongAmountOfArgumentsException("Wrong amount of arguments!", 1, arguments.size());
        this.scriptFilePath = (String) arguments.get(0);
    }
}
