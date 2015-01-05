package chatmanager.chat.settings;

import java.util.UUID;

public class ChatIgnores {

    private int chatIgnoreId;
    private UUID ignoreeUuid;
    private UUID ignored;


    public int getChatIgnoreId() {
        return chatIgnoreId;
    }

    public void setChatIgnoreId(int chatIgnoreId) {
        this.chatIgnoreId = chatIgnoreId;
    }

    public UUID getIgnoreeUuid() {
        return ignoreeUuid;
    }

    public void setIgnoreeUuid(UUID ignoreeUuid) {
        this.ignoreeUuid = ignoreeUuid;
    }

    public UUID getIgnored() {
        return ignored;
    }

    public void setIgnored(UUID ignored) {
        this.ignored = ignored;
    }
}
