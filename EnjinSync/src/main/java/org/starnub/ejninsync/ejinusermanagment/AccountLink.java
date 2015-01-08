package org.starnub.ejninsync.ejinusermanagment;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.starnub.cache.objects.AbstractCache;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.ejninsync.ejinusermanagment.datatypes.User;

@DatabaseTable(tableName = "AccountLinks")
public class AccountLink extends AbstractCache{

    /**
     * Represents a an ID for a starnubId & enjinId
     */
    @DatabaseField(generatedId = true, columnName = "ID")
    private int characterIDIPPairid;

    /**
     * Represents this Account Links StarnubId
     */
    @DatabaseField(dataType = DataType.INTEGER, canBeNull = false, uniqueCombo = true, columnName = "STARNUB_ID")
    private int starnubdId;

    /**
     * Represents this Account Links enjinId
     */
    @DatabaseField(dataType = DataType.STRING, canBeNull = false,  uniqueCombo = true, columnName = "ENJIN_ID")
    private String enjinId;

    /**
     * Represent the current player session for this account link
     */
    private Player playerSession;

    /**
     * Represents the enjin user file
     */
    private User enjinUser;

    public AccountLink(int characterIDIPPairid, int starnubdId, String enjinId) {
        this.characterIDIPPairid = characterIDIPPairid;
        this.starnubdId = starnubdId;
        this.enjinId = enjinId;
    }

    public int getStarnubdId() {
        return starnubdId;
    }

    public void setStarnubdId(int starnubdId) {
        this.starnubdId = starnubdId;
    }

    public String getEnjinId() {
        return enjinId;
    }

    public void setEnjinId(String enjinId) {
        this.enjinId = enjinId;
    }

    public int getCharacterIDIPPairid() {
        return characterIDIPPairid;
    }

    public void setCharacterIDIPPairid(int characterIDIPPairid) {
        this.characterIDIPPairid = characterIDIPPairid;
    }

    public Player getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(Player playerSession) {
        this.playerSession = playerSession;
    }

    public User getEnjinUser() {
        return enjinUser;
    }

    public void setEnjinUser(User enjinUser) {
        this.enjinUser = enjinUser;
    }
}

