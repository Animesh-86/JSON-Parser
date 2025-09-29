package streaming;

//Interface for handling JSON events during streaming parsing.
public interface JsonEventHandler {

    void handleEvent(JsonEvent event);
}
