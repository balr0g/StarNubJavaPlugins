package ejninsync.ejinusermanagment.datatypes;

public class Purchase {

    private final String USER_ID;
    private final String USERNAME;
    private final Item ITEM;
    private final String PURCHASE_DATE;
    private final String CURRENCY;
    private final String CHARACTER;

    public Purchase(String USER_ID, String USERNAME, Item ITEM, String PURCHASE_DATE, String CURRENCY, String CHARACTER) {
        this.USER_ID = USER_ID;
        this.USERNAME = USERNAME;
        this.ITEM = ITEM;
        this.PURCHASE_DATE = PURCHASE_DATE;
        this.CURRENCY = CURRENCY;
        this.CHARACTER = CHARACTER;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public Item getITEM() {
        return ITEM;
    }

    public String getPURCHASE_DATE() {
        return PURCHASE_DATE;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public String getCHARACTER() {
        return CHARACTER;
    }
}

