package sg.edu.nyp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonPrimitive;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "userApi",
        version = "v1",
        resource = "user",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class UserEndpoint {

    private static final Logger logger = Logger.getLogger(UserEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(User.class);
        ObjectifyService.register(UserLocation.class);
        ObjectifyService.register(UserReward.class);
        ObjectifyService.register(Reward.class);
        ObjectifyService.register(HelpRequest.class);

    }

    /**
     * Returns the {@link User} with the corresponding ID.
     *
     * @param razerID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code User} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "user/{razerID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public User get(@Named("razerID") String razerID) throws NotFoundException {
        logger.info("Getting User with ID: " + razerID);
        User user = ofy().load().type(User.class).id(razerID).now();
        if (user == null) {
            throw new NotFoundException("Could not find User with ID: " + razerID);
        }

        return user;
    }

    /**
     * Inserts a new {@code User}.
     */
    @ApiMethod(
            name = "insert",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.POST)
    public User insert(User user) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that user.razerID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        user.setName("Wei Cong");
        ofy().save().entity(user).now();
        logger.info("Created User with ID: " + user.getRazerID());

        return ofy().load().entity(user).now();
    }



    /**
     * Deletes the specified {@code User}.
     *
     * @param razerID the ID of the entity to delete
     * @throws NotFoundException if the {@code razerID} does not correspond to an existing
     *                           {@code User}
     */
    @ApiMethod(
            name = "remove",
            path = "user/{razerID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("razerID") String razerID) throws NotFoundException {
        checkExists(razerID);
        ofy().delete().type(User.class).id(razerID).now();
        logger.info("Deleted User with ID: " + razerID);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<User> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<User> query = ofy().load().type(User.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<User> queryIterator = query.iterator();
        List<User> userList = new ArrayList<User>(limit);
        while (queryIterator.hasNext()) {
            userList.add(queryIterator.next());
        }
        return CollectionResponse.<User>builder().setItems(userList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String razerID) throws NotFoundException {
        try {
            ofy().load().type(User.class).id(razerID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find User with ID: " + razerID);
        }
    }
    @ApiMethod(
            name="getHelpees",
            path="helpee",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<User> getHelpees(@Named("razerID") String razerID){
        try {
            User user = this.get(razerID);
            return user.getHelpeed();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @ApiMethod(
            name="registerNotification",
            path="regNotification",
            httpMethod= ApiMethod.HttpMethod.POST
    )
    public User registerNotification(User user){
        String razerID = user.getRazerID();
        String regId = user.getRegID();

        //Update user here with regID
        try {
            user = this.get(razerID);
            user.setRegID(regId);
            Key<User> userKey = ofy().save().entity(user).now();
            return Ref.create(userKey).get();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @ApiMethod(
            name = "requestHelp",
            path = "requestHelp/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    //Got to make it such that this also updates the needy's location.
    public void requestHelp(@Named("razerID") String razerID, @Named("lat") final double lat, @Named("lng") final double lng){
        List<User> users = ofy().load().type(User.class).filter("needy", false).list();
        //Go to database, find user that is near given lat lng and notify user.
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                double distanceDiffo1 = distance(o1.getLat(), o1.getLng(), lat, lng);
                double distanceDiffo2 = distance(o2.getLat(), o2.getLng(), lat, lng);

                return Double.compare(distanceDiffo1, distanceDiffo2);
            }
        });
        URL url = null;
        try {
            url = new URL("https://pushy.me/push?api_key=677903558b207ae13205462e1c7b20609bb6f87e6c5869f38945e6ac6ed8566b");
            JsonObject object = new JsonObject();
            JsonArray array = new JsonArray();
            for(int i = 0 ; i < users.size(); i++){
                array.add(new JsonPrimitive(users.get(i).getRegID()));
            }

                HelpRequest request = new HelpRequest();

                User user = this.get(razerID);
                request.setNeedyUserRef(Ref.create(user));
                request.setLat(user.getLat());
                request.setLng(user.getLng());
                //Yet to set resolved and helper ref yet, do it in update helprequest endpoint

                Key<HelpRequest> key = ofy().save().entity(request).now();

                JsonObject dataObject = new JsonObject();
                //Need to send information about user here?
                dataObject.addProperty("TYPE" , "NEARBY");
                dataObject.addProperty("MESSAGE","Somebody nearby needs help! Help now for your chance to earn points!");
                dataObject.addProperty("TITLE","Help Request");
                dataObject.addProperty("LAT", String.valueOf(lat));
                dataObject.addProperty("LNG", String.valueOf(lng));
                dataObject.addProperty("REQUESTID", String.valueOf(Ref.create(key).get().getRequestID()));

                object.add("registration_ids", array);
                object.add("data", dataObject);
                String content = new Gson().toJson(object);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", String.valueOf(content.length()));

                logger.info(String.valueOf(lat));
                OutputStream stream = con.getOutputStream();
                stream.write(content.getBytes());
                stream.flush();

                String allLines = "";
                String line;
                InputStream response = con.getInputStream();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    allLines += line+"/n";
                }
                stream.close();
                reader.close();






        } catch (MalformedURLException e) {

            logger.severe(e.getMessage());
        } catch (IOException e) {

            logger.severe(e.getMessage()+" "+e.getCause());
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }
    //returns distance in meters
    public static double distance(double lat1, double lng1,
                                  double lat2, double lng2){
        double a = (lat1-lat2)*distPerLat(lat1);
        double b = (lng1-lng2)*distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private static double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
    @ApiMethod(
            name = "updateLocation",
            path = "updateLocation/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public void updateLocation(@Named("razerID") String razerID, @Named("lat") double lat, @Named("lng") double lng){
        try {
            User user = this.get(razerID);
            user.setLat(lat);
            user.setLng(lng);
            ofy().save().entity(user).now();

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
    @ApiMethod(
            name="getRewardsForUser",
            path="getRewards/{razerID}",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<UserReward> getRewardsForUser(@Named("razerID") String razerID){
        try {
            User user = this.get(razerID);
            return user.getRewards();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @ApiMethod(
            name = "addPoint",
            path = "addPoints/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )

    public User addPointsForUser(@Named("razerID") String razerID, @Named("targetUser") String otherUserID){
        try {
            checkExists(razerID);
            User user = this.get(razerID);

            //Check if user has been helped?
            List<Ref<User>> helped = user.getHelpedRef();
            if(helped == null)
                helped = new ArrayList<Ref<User>>();
            User targetUser = this.get(otherUserID);

            //Check if user is registered in system AND needy
            if(targetUser != null && targetUser.isNeedy()) {

                URL url = null;
                try {
                    url = new URL("https://pushy.me/push?api_key=677903558b207ae13205462e1c7b20609bb6f87e6c5869f38945e6ac6ed8566b");
                    JsonObject object = new JsonObject();
                    JsonArray array = new JsonArray();
                    array.add(new JsonPrimitive(user.getRegID()));

                    JsonObject dataObject = new JsonObject();

                    //Need to send information about user here?
                    dataObject.addProperty("TYPE" , "HELPED");
                    dataObject.addProperty("MESSAGE","You helped somebody!Good job! ");
                    dataObject.addProperty("TITLE","Help Request");

                    object.add("registration_ids", array);
                    object.add("data", dataObject);
                    String content = new Gson().toJson(object);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Content-Length", String.valueOf(content.length()));

                    OutputStream stream = con.getOutputStream();
                    stream.write(content.getBytes());
                    stream.flush();

                    String allLines = "";
                    String line;
                    InputStream response = con.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(con.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        allLines += line+"/n";
                    }
                    stream.close();
                    reader.close();

                } catch (MalformedURLException e) {

                    logger.severe(e.getMessage());
                } catch (IOException e) {

                    logger.severe(e.getMessage()+" "+e.getCause());
                    e.printStackTrace();
                }

                helped.add(Ref.create(targetUser));
                user.setHelpedRef(helped);

                int addPoints = 0;
                //Add achievement
                Achievements newAchievement = null;
                if (helped.size() >= 5) {
                    //Check if got achievements already
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_THREE);
                    addPoints = 3;
                }
                if (helped.size() >= 10) {
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_SEVEN);
                    addPoints = 5;
                }
                if (helped.size() >= 15) {
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_TEN);
                    addPoints = 8;
                }
                if (gotAchievements(newAchievement, user)) {
                    newAchievement = null;

                }else{
                    logger.info("GOt no achievement!");
                }
                if (newAchievement != null) {
                    UserAchievement userach = new UserAchievement();

                    userach.setTimeRecieved(Calendar.getInstance().getTimeInMillis());
                    userach.setAchievementsRef(Ref.create(newAchievement));
                    userach.setUserRef(Ref.create(user));

                    ofy().save().entity(userach).now();
                    userach = ofy().load().entity(userach).now();

                    Ref<UserAchievement> refAch = Ref.create(userach);
                    List<Ref<UserAchievement>> list = user.getAchievements();
                    if (list == null)
                        list = new ArrayList<Ref<UserAchievement>>();

                    list.add(refAch);

                    user.setPoints(user.getPoints() + addPoints);
                    user.setAchievements(list);
                }
                ofy().save().entity(user).now();
            }else{
                logger.severe("Handshake with both non needy"+otherUserID+" id is for target user");
            }
            return ofy().load().entity(user).now();

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    private User giveReward(User user, String razerID){
        //Create UserReward
        UserReward userReward = new UserReward();
        try {
            long rewardID = 0;
            long pointsRequired = 0;

            if(user.getPoints() >= 10){
                rewardID = 1;
                pointsRequired = 10;
            }
            if(user.getPoints() >= 11){
                rewardID = 2;
                pointsRequired = 20;
            }
            if(user.getPoints() >= 12){
                rewardID = 3;
                pointsRequired = 30;
            }
            //Can put all reward logic here
            userReward.setUserRef(Ref.create(this.get(razerID)));
            if(rewardID != 0) {
                Reward rewardEntity = new Reward();
                rewardEntity.setId(rewardID);
                rewardEntity.setPointsRequired(pointsRequired);
                ofy().save().entity(rewardEntity).now();
                rewardEntity = ofy().load().entity(rewardEntity).now();
                userReward.setRewardRef(Ref.create(rewardEntity));


                //Create user reward and save in db
                Key<UserReward> key = ofy().save().entity(userReward).now();

                //Append to userreward
                List<Ref<UserReward>> rewards = user.getRewardsRef();
                if (rewards == null)
                    rewards = new ArrayList<Ref<UserReward>>();
                rewards.add(Ref.create(key));
                user.setRewardsRef(rewards);
            }
            return user;
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/
    private boolean gotAchievements(Achievements ach, User user){
        boolean found = false;
        ArrayList<Achievements> list = new ArrayList<Achievements>();
        if(user.getAchievements() != null)
            for(Ref<UserAchievement> ref : user.getAchievements()){
                UserAchievement userAchievement = ref.get();
                Achievements achievements = userAchievement.getAchievementsRef().get();
                list.add(achievements);
            }
            for(Achievements achievements : list){
                if(achievements != null && ach != null){
                    if(achievements.getAchievementID().equals(ach.getAchievementID())){
                        found = true;
                    }else{
                        logger.info("Not equals");
                    }
                }else{
                    logger.info("Compare with either one null");
                }
            }
        return found;
    }
}