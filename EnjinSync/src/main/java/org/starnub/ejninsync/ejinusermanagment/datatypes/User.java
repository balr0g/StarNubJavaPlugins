package org.starnub.ejninsync.ejinusermanagment.datatypes;

import java.lang.Character;
import java.util.HashSet;

public class User {

    private final String USER_ID;
    private final String USERNAME;
    private final ForumStats FORUM_STATS;
    private final HashSet<Tag> TAGS;
    private final HashSet<Character> CHARACTERS; // NOT NEEDED CURRENTLY
//    private final HashSet<Stat> STATS; // NOT NEEDED CURRENTLY
    private final HashSet<Purchase> PURCHASES;
    private long lastSeen;
    private long dateJoined;

    public User(String USER_ID, String USERNAME, long lastSeen, long dateJoined, ForumStats FORUM_STATS) {
        this.USER_ID = USER_ID;
        this.USERNAME = USERNAME;
        this.lastSeen = lastSeen;
        this.dateJoined = dateJoined;
        this.FORUM_STATS = FORUM_STATS;
        TAGS = new HashSet<Tag>();
        CHARACTERS = new HashSet<Character>();
//        STATS = new HashSet<Stat>();
        PURCHASES = new HashSet<Purchase>();

    }

    public HashSet<Tag> getTAGS() {
        return TAGS;
    }

    public HashSet<Character> getCHARACTERS() {
        return CHARACTERS;
    }

    public HashSet<Purchase> getPURCHASES() {
        return PURCHASES;
    }

    public String toString() {
        String ret = "";
        ret += "ID: " + getUSER_ID() + "\n";
        ret += "Name: " + getUSERNAME() + "\n";
        ret += "Post Count: " + getFORUM_STATS().getForum_post_count() + "\n";
        ret += "Votes: " + getFORUM_STATS().getForum_votes() + "\n";
        ret += "Votes Up: " + getFORUM_STATS().getForum_up_votes() + "\n";
        ret += "Votes Down: " + getFORUM_STATS().getForum_down_votes() + "\n";
        ret += "Last Seen: " + getLastSeen() + "\n";
        ret += "Date Joined: " + getDateJoined() + "\n";
        return ret;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public ForumStats getFORUM_STATS() {
        return FORUM_STATS;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(long dateJoined) {
        this.dateJoined = dateJoined;
    }
}
