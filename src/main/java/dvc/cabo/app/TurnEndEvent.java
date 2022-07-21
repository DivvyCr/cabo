package dvc.cabo.app;

import javafx.event.Event;
import javafx.event.EventType;

public class TurnEndEvent extends Event {

    public static final EventType<TurnEndEvent> TEST = new EventType<TurnEndEvent>("TEST");

    public TurnEndEvent() { super(TurnEndEvent.TEST); }

}
