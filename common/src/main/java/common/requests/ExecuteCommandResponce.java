package common.requests;

import java.io.Serializable;

public record ExecuteCommandResponce (ResultState state, Serializable data) implements Serializable {}
