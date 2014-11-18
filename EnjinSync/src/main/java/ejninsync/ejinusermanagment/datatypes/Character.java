package ejninsync.ejinusermanagment.datatypes;

public class Character {

    private final String GAME_ID;
    private final String NAME;
    private final String TYPE;

    public Character(String GAME_ID, String NAME, String TYPE) {
        this.GAME_ID = GAME_ID;
        this.NAME = NAME;
        this.TYPE = TYPE;
    }

    public String getGAME_ID() {
        return GAME_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getTYPE() {
        return TYPE;
    }

}