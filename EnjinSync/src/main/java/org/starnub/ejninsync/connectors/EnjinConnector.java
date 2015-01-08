package org.starnub.ejninsync.connectors;

import org.codehome.utilities.simplejson.JSONObject;
import org.codehome.utilities.simplejson.JSONValue;
import org.starnub.StarNub;
import org.starnub.ejninsync.ejinusermanagment.datatypes.Tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnjinConnector {

    String baseURL;
    String secretKey;
    String shopModule;

    @SuppressWarnings("unchecked")
    public EnjinConnector(){
        Map<String, Object> databaseConfig = (Map<String, Object>) StarNub.getPluginManager().getConfiguration("EnjinSync").get("enjin");
        this.baseURL = (String) databaseConfig.get("website_url");
        this.secretKey = (String) databaseConfig.get("secret_key");
        this.shopModule = (String) databaseConfig.get("shop_module");
    }

    /**
     *
     * @return String representing the websites Secret API Key
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     *
     * @param secretKey String representing the websites Secret API Key
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * This will return only the tags mapped in the configuration that are also on the website
     * <p>
     * @param groupsMap Map group mapping of StarNub groups to website Tags/Groups
     * @param awardsMap Map award mapping of StarNub Achievements to Enjin Awards
     * @return ArrayList element 0 will be groups and element 1 is awards, each contained in a concurrent HashMap <String, Tag>
     */
    public ArrayList<ConcurrentHashMap<String, Tag>> getEnjinTagsJSONObjectFiltered(Map<String, Object> groupsMap, Map<String, Object> awardsMap) {
        return getWebsiteTagsByFilter(getEnjinTags(), groupsMap, awardsMap);
    }

    public static ArrayList<ConcurrentHashMap<String, Tag>> getWebsiteTagsByFilter(JSONObject json, Map<String, Object> groupMap, Map<String, Object> awardsMap) {
        ArrayList<ConcurrentHashMap<String, Tag>> arrayList = new ArrayList<>();
        ConcurrentHashMap<String, Tag> groupTags = new ConcurrentHashMap<String, Tag>();
        ConcurrentHashMap<String, Tag> awardTags = new ConcurrentHashMap<String, Tag>();
        for (Object jsonId : json.keySet()) {
            Map jsonTagMap = (Map) json.get(jsonId);
            String jsonTagName = (String) jsonTagMap.get("tagname");
            for (String serverMappedGroupTagString : groupMap.keySet()) {
                groupTags.putAll(tagsConcurrentHashMapBuild(jsonTagName, serverMappedGroupTagString, groupMap, jsonTagMap));
            }
            for (String serverMappedAwardTagStrings : awardsMap.keySet()) {
                awardTags.putAll(tagsConcurrentHashMapBuild(jsonTagName, serverMappedAwardTagStrings, awardsMap, jsonTagMap));
            }
        }
        arrayList.add(groupTags);
        arrayList.add(awardTags);
        return arrayList;
    }

    /**
     *
     * This will return all of the tags from the Enjin site in a JSON object.
     * <p>
     * Enjin Format:
     * <p>
     * "1197112":
     *       {
     *       "tagname":"[Slayer V]",
     *       "numusers":"0","visible":"1",
     *       "tag_id":"1197112"
     *       }
     *
     * @return JSONObject representing the above data format
     */
    public JSONObject getEnjinTags(){
        return getEnjinData(getSafeBaseURL() + "get-tag-types" + "/" + secretKey);
    }

    private static ConcurrentHashMap<String, Tag> tagsConcurrentHashMapBuild(String jsonTagName, String serverMappedTagString, Map<String, Object> groupMap, Map jsonTagMap) {
        ConcurrentHashMap<String, Tag> tagMap = new ConcurrentHashMap<>();
        boolean match = false;
        Object stringOrList = groupMap.get(serverMappedTagString);
        if (stringOrList instanceof String) {
            match = ((String) stringOrList).equalsIgnoreCase(jsonTagName);
        } else if (stringOrList instanceof ArrayList) {
            for (String tagString : ((ArrayList<String>) stringOrList)) {
                match = tagString.equalsIgnoreCase(jsonTagName);
                if (match) {
                    break;
                }
            }
        }
        if (match) {
            Tag newTag = new Tag(
                    Integer.parseInt(String.valueOf(jsonTagMap.get("tag_id"))),
                    jsonTagName,
                    Integer.parseInt(String.valueOf(jsonTagMap.get("numusers"))),
                    (Integer.parseInt(String.valueOf(jsonTagMap.get("visible")))) == 1,
                    serverMappedTagString
            );
            tagMap.putIfAbsent(jsonTagName, newTag);
        }
        return tagMap;
    }

    /**
     *
     * @param urlString URL representing the url you want GET or POST to, should include all parameters
     * @return BufferedReader representing the data returned from the GET or POST
     */
    public JSONObject getEnjinData(String urlString) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (JSONObject) JSONValue.parse(sb.toString());
    }

    /**
     * @return String representing a full URL if the original URL did not include the "http://"
     */
    public String getSafeBaseURL() {
        return (getBaseURL().startsWith("http://") ? getBaseURL() : "http://" + getBaseURL()) + "/api/";
    }

    /**
     * @return String returns the string URL
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     *
     * @param url String representing the URL you want to set
     */
    public void setBaseURL(String url) {
        baseURL = url;
    }


//TODO All users


    //get-users
    //get-users/user_id/#
    //get-users/characters/1
//    user_id: #
//    characters: 1/0


    //TODO - Does not require secret key
    //get-tags
//    user: [user id]

//    {
//        "tags":{
//        "[Tag ID]":{
//            "name":"[Tag Name]",
//                    "visible":"[Tag Visibility]",
//                    "users":["[User ID]","[User ID]"]
//        }
//    },
//        "users":{
//        "[User ID]":{
//            "username":"[Username]",
//                    "forum_post_count":"[Total Forum Posts]",
//                    "forum_votes":"[Total Forum Votes]",
//                    "forum_up_votes":"[Forum Up Votes]",
//                    "forum_down_votes":"[Forum Down Votes]",
//                    "lastseen":"[Unix Timestamp when user last visited website]",
//                    "datejoined":"[Unix Timestamp when user joined website]"
//        }
//    }
//    }





    //NOTE is user
    //get-tags/user/xxx
    //get-tags/character/xxx
    //get-users/tag/xxx
    //api/get-users/characters/1

    //GameID = 69606


    //TODO Requires secret key

//    /api/get-points
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]

//    /api/set-points
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]
//    points: [Total points user will have]



//    /api/tag-user
//    Required POST Parameters
//    key: [secret api key]
//    user: [user id]
//    tag: [tag id]
//Optional POST Parameters
//
//
//    expire_hours: [# of hours until expiration]
//    expire_days: [# of days until expiration]
//    expire_weeks: [# of weeks until expiration]
//    expire_months: [# of months until expiration]
//    expire_years: [# of year until expiration]
//{
//    "success":"true"
//}




//    /api/untag-user
//    Required POST Parameters
//    key: [secret api key]
//    user: [user id]
//    tag: [tag id]
//{
//    "success":"true"
//}



//    /api/get-stats
//    Required POST Parameter
//    key: [secret api key]
//    Optional POST Parameter
//    user: [user id]


//    /api/save-user-stats
//    Required POST Parameters
//    key: [secret api key]
//    stats: [JSON]
//    Example
//    var stats = {
//            "[user id]": {
//        "boings":"3",
//                "marklars":"lots of marklars"
//    },
//            "[user id]": {
//        "boings":"6",
//                "marklars":"more marklars"
//    },
//            ...
//};


//    /api/get-points
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]
//    Optional POST Parameter
//    player: [Minecraft player name]
//    Example
//    $.post("http://example.com/api/get-points", { key:[secret api key], user_id:[user id] }, function(result) {
//        console.log(result);
//    });
//    Returns
//    {
//        "success":"true",
//            "points":10
//    }


//
//    /api/set-points
//
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]
//    points: [Total points user will have]
//    Optional POST Parameter
//    player: [Minecraft player name]
//    Example
//    $.post("http://example.com/api/set-points", { key:[secret api key], user_id:[user id], points:[new point total] }, function(result) {
//        console.log(result);
//    });
//    Returns
//    {
//        "success":"true",
//            "points":10
//    }

//    URL
//http://www.free-universe.com/api/add-points/key/5c881ca2745ed174c94b9464dfae686948f754a090ce7ef0/user_id/1319865/points/1
//    /api/add-points
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]
//    points: [Number of points to add]
//    Optional POST Parameter
//    player: [Minecraft player name]
//    Example
//    $.post("http://example.com/api/add-points", { key:[secret api key], user_id:[user id], points:[points to add] }, function(result) {
//        console.log(result);
//    });
//    Returns
//    {
//        "success":"true",
//            "points":10
//    }

//    URL
//    /api/remove-points
//    Required POST Parameters
//    key: [secret api key]
//    user_id: [user id]
//    points: [Number of points to remove]
//
//    Optional POST Parameter
//    player: [Minecraft player name]
//
//    Example
//    $.post("http://example.com/api/remove-points", { key:[secret api key], user_id:[user id], points:[points to remove] }, function(result) {
//        console.log(result);
//    });
//
//    Returns
//    {
//        "success":"true",
//            "points":10
//    }





//    /api/m-shopping-purchases
//    Required POST Parameter
//    m: [module id]
//
//    Optional POST Parameters
//
//    custom_field: [text]
//    user_id: [user_id]
//    mc_player: [player name]
//
//
//    Returns
//    [
//    {
//        "user":{
//        "user_id":"666",
//                "username":"Illuminati"},
//        "items":[
//        {
//            "item_name":"Weapon",
//                "item_price":"1.00",
//                "item_id":"182018",
//                "variables":{
//            "sword":"276"
//        }
//        }
//        ],
//        "purchase_date":"1368443876",
//            "currency":"USD",
//            "character":"danbrown"
//    }
//    ]

    public void getUser(String userId, boolean getTags, boolean getCharacters) {

    }

    public void getUsers(boolean getTags, boolean getCharacters) {

    }

    /**
     * This can return the data in a few layouts seen below.
     * <p>
     * Enjin Format:
     * <p>
     * /get-users/
     * "1319865":
     *      {
     *      "username":"Underbalanced",
     *      "forum_post_count":"86",
     *      "forum_votes":"18",
     *      "forum_up_votes":"18",
     *      "forum_down_votes":"0",
     *      "lastseen":"1412779604",
     *      "datejoined":"1326822375",
     *      }
     *
     * <p>
     * /get-users/characters/1
     * <p>
     * "1319865":
     *      {
     *      "username":"Underbalanced",
     *      "forum_post_count":"86",
     *      "forum_votes":"18",
     *      "forum_up_votes":"18",
     *      "forum_down_votes":"0",
     *      "lastseen":"1412779604",
     *      "datejoined":"1326822375",
     *      "characters":
     *           {
     *           "69606":
     *                [
     *                     {
     *                      "name":"Test Character",
     *                      "type":"CodeHere"
     *                     }
     *                ],
     *            "4923":
     *                [
     *                     {
     *                     "name":"Underbalanced",
     *                     "type":null
     *                     }
     *                 ]
     *           }
     *      }
     *
     *
     *
     * @param userId
     * @param getTags
     * @param getCharacters
     * @return
     */
    private JSONObject getUsers(String userId, boolean getTags, boolean getCharacters) {
        String tagsOrNo = getTags ? "get-tags" : "get-users";
        String url = getSafeBaseURL() + tagsOrNo;
        if (userId != null) {
            url = url + "/user_id/" + userId;
        }
        if (getCharacters) {
            url = url + "/characters/1";
        }
        return getEnjinData(url);
    }

    public void usersFromJSONObject(JSONObject json) {

    }

    public void tagsFromJSONObject(JSONObject jsonObject){

    }

    public void userFromJSONObject(JSONObject jsonObject){

    }

    public void userTagFromJSONObject(JSONObject jsonObject){

    }

    public void buildServerTags(){

    }
}
