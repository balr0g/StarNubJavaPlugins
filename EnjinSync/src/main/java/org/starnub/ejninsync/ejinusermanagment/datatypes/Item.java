package org.starnub.ejninsync.ejinusermanagment.datatypes;

import java.util.HashMap;

public class Item {

    private final String NAME;
    private final String ID;
    private final String PRICE;
    private final HashMap<String, Object> variables;

    public Item(String NAME, String ID, String PRICE, HashMap<String, Object> variables) {
        this.NAME = NAME;
        this.ID = ID;
        this.PRICE = PRICE;
        this.variables = variables;
    }

    public String getNAME() {
        return NAME;
    }

    public String getID() {
        return ID;
    }

    public String getPRICE() {
        return PRICE;
    }

    public HashMap<String, Object> getVariables() {
        return variables;
    }
}
