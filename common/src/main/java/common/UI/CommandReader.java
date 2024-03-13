package common.UI;

import common.requests.PackedCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandReader {
    private static CommandReader COMMAND_READER = null;
    private CommandReader(){}

    public static CommandReader getInstance(){
        if(COMMAND_READER == null){
            COMMAND_READER = new CommandReader();
        }
        return COMMAND_READER;
    }
    public PackedCommand readCommand(){
        String line = Console.getInstance().readLine();
        String[] input = (line.trim() + " ").split(" ");
        if (input.length == 0) return null;
        String commandName = input[0];
        String[] commandArgs = Arrays.copyOfRange(input, 1, input.length);
        return new PackedCommand(commandName, new ArrayList<>(List.of(commandArgs)));
    }
}
