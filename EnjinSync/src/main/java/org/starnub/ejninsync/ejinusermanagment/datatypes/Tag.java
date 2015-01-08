package org.starnub.ejninsync.ejinusermanagment.datatypes;

public class Tag {

    private final int TAG_ID;
    private String tagName;
    private int numberOfUsers;
    private boolean visible;
    private String serverGroup;

    public Tag(int TAG_ID, String tagName, int numberOfUsers, boolean visible, String serverGroup) {
        this.TAG_ID = TAG_ID;
        this.tagName = tagName;
        this.numberOfUsers = numberOfUsers;
        this.visible = visible;
        this.serverGroup = serverGroup;
    }

    public int getTAG_ID() {
        return TAG_ID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }
}