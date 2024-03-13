package common.requests;

import java.io.Serializable;
import java.util.ArrayList;

public record PackedCommand(String commandName, ArrayList<Serializable> arguments) implements Serializable {}
