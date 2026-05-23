package org.ies.fenix.client.gui.model.script;

import java.util.UUID;

public abstract class BaseBlockModel {

    private final String id;
    private final String type;

    public BaseBlockModel(String type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}


