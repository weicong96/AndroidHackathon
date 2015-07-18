package sg.edu.nyp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            name = "addPoint",
            path = "addPoints/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public User addPointsForUser(@Named("razerID") String razerID, @Named("targetUser") String otherUserID){
        try {
            checkExists(razerID);
            User user = this.get(razerID);
            user.setPoints(user.getPoints() + 1);

            //Check if user has been helped?
            List<Ref<User>> helped = user.getHelped();
            if(helped == null)
                helped = new ArrayList<Ref<User>>();
            User targetUser = this.get(otherUserID);
            if(targetUser.isNeedy()) {
                helped.add(Ref.create(targetUser));
                user.setHelped(helped);


                //Add achievement
                Achievements newAchievement = null;
                if (helped.size() >= 3) {
                    //Check if got achievements already
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_THREE);
                }
                if (helped.size() >= 7) {
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_SEVEN);
                }
                if (helped.size() >= 10) {
                    newAchievement = AchievementsENUM.getAchivements(AchievementsENUM.UNIQUE_TEN);
                }
                if (gotAchievements(newAchievement, user)) {
                    newAchievement = null;
                }
                if (newAchievement != null) {
                    UserAchievement userach = new UserAchievement();
                    NumberFormat formatter = new DecimalFormat("0000000");
                    userach.setId(razerID+formatter.format(user.getAchievements().size()));
                    userach.setTimeRecieved(Calendar.getInstance().getTimeInMillis());
                    userach.setAchievementsRef(Ref.create(newAchievement));

                    ofy().save().entity(userach).now();
                    userach = ofy().load().entity(userach).now();

                    Ref<UserAchievement> refAch = Ref.create(userach);
                    List<Ref<UserAchievement>> list = user.getAchievements();
                    if (list == null)
                        list = new ArrayList<Ref<UserAchievement>>();

                    list.add(refAch);
                    user.setAchievements(list);
                }
                ofy().save().entity(user).now();
            }
            return ofy().load().entity(user).now();

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
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
                if(achievements.getAchievementID() == ach.getAchievementID()){
                    found = true;
                }
            }
        return found;
    }
}