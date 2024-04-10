package webSocketMessages.serverMessages;

public class ErrorMessage extends  ServerMessage {
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.message = errorMessage;
    }
}
