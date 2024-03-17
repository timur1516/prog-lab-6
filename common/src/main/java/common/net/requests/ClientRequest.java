package common.net.requests;

import java.io.Serializable;

public record ClientRequest(ClientRequestType type, Serializable data) implements Serializable {}
