package streaming;

public class PrintEventHandler implements JsonEventHandler{

    @Override
    public void handleEvent(JsonEvent event) {
        System.out.println(event);
    }
}
