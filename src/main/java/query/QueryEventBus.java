package query;

import command.events.Event;

public interface QueryEventBus {

    void broadcast(Event e);

}
