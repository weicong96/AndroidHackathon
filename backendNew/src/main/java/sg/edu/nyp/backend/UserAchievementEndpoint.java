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
        name = "userAchievementApi",
        version = "v1",
        resource = "userAchievement",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class UserAchievementEndpoint {

    private static final Logger logger = Logger.getLogger(UserAchievementEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(UserAchievement.class);
    }

    /**
     * Returns the {@link UserAchievement} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserAchievement} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userAchievement/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserAchievement get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting UserAchievement with ID: " + id);
        UserAchievement userAchievement = ofy().load().type(UserAchievement.class).id(id).now();
        if (userAchievement == null) {
            throw new NotFoundException("Could not find UserAchievement with ID: " + id);
        }
        return userAchievement;
    }

    /**
     * Inserts a new {@code UserAchievement}.
     */
    @ApiMethod(
            name = "insert",
            path = "userAchievement",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserAchievement insert(UserAchievement userAchievement) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userAchievement.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(userAchievement).now();
        logger.info("Created UserAchievement with ID: " + userAchievement.getId());

        return ofy().load().entity(userAchievement).now();
    }
    private void checkExists(String razerID) throws NotFoundException {
        try {
            ofy().load().type(User.class).id(razerID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find User with ID: " + razerID);
        }
    }
    @ApiMethod(
            name = "getAchievementsForUser",
            path = "user/achievements/{razerID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public ArrayList<Map<String, Object>> getAchievementsForUser(@Named("razerID") String razerID){
        try {
            checkExists(razerID);

            User user = new User();
            user.setRazerID(razerID);
            user = ofy().load().entity(user).now();


            ArrayList<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
            Map<String, Object> mapMonths = new HashMap<String,Object>();
            for(Ref<UserAchievement> ref : user.getAchievements()){
                UserAchievement ach = ref.get();
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(ach.getTimeRecieved()/1000));
                ach.setAchievements(ach.getAchievementsRef().get());
                if(mapMonths.get("month") != null && mapMonths.get("month").equals(cal.get(Calendar.MONTH))){
                    ArrayList<UserAchievement> list = new ArrayList<UserAchievement>(Arrays.asList((UserAchievement[]) mapMonths.get("items")));
                    list.add(ach);
                }else {
                    //New month, add to list and reitnialize mapMonths
                    if(mapMonths.size() != 0)
                        map.add(mapMonths);
                    mapMonths = new HashMap<String, Object>();
                    mapMonths.put("month", cal.get(Calendar.MONTH));
                    mapMonths.put("items", new UserAchievement[]{ach});
                }
            }
            //handle remaining
            if(mapMonths.size() > 0)
                map.add(mapMonths);
            return map;
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing {@code UserAchievement}.
     *
     * @param id              the ID of the entity to be updated
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserAchievement}

    @ApiMethod(
            name = "update",
            path = "userAchievement/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserAchievement update(@Named("id") long id, UserAchievement userAchievement) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(userAchievement).now();
        logger.info("Updated UserAchievement: " + userAchievement);
        return ofy().load().entity(userAchievement).now();
    }


     * Deletes the specified {@code UserAchievement}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserAchievement}
     */
    @ApiMethod(
            name = "remove",
            path = "userAchievement/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(UserAchievement.class).id(id).now();
        logger.info("Deleted UserAchievement with ID: " + id);
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
            path = "userAchievement",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserAchievement> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserAchievement> query = ofy().load().type(UserAchievement.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserAchievement> queryIterator = query.iterator();
        List<UserAchievement> userAchievementList = new ArrayList<UserAchievement>(limit);
        while (queryIterator.hasNext()) {
            userAchievementList.add(queryIterator.next());
        }
        return CollectionResponse.<UserAchievement>builder().setItems(userAchievementList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(UserAchievement.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserAchievement with ID: " + id);
        }
    }
}