package chatmanager.chat;

import utilities.cache.objects.StringCache;

public class ChatMessageCache extends StringCache {

    public ChatMessageCache(String string) {
        super(string);
    }

    public boolean cacheUpdate(String message) {
        String stringCached = string;
        string = message;
        return stringCached.equals(string);
    }
}
