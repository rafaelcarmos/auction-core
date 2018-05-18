package command.entity;

import java.util.UUID;

public class Item {
    private final UUID id;
    private final String name;

    public Item(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
