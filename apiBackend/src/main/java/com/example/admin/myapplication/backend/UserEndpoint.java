package com.example.admin.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
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
                ownerDomain = "backend.myapplication.admin.example.com",
                ownerName = "backend.myapplication.admin.example.com",
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
     * Updates an existing {@code User}.
     *
     * @param razerID the ID of the entity to be updated
     * @param user    the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code razerID} does not correspond to an existing
     *                           {@code User}

    @ApiMethod(
            name = "update",
            path = "user/{razerID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(@Named("razerID") String razerID, User user) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(razerID);
        ofy().save().entity(user).now();
        logger.info("Updated User: " + user);
        return ofy().load().entity(user).now();
    }*/

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
            User user = queryIterator.next();
            userList.add(user);
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
            name = "getAchievementForUser",
            path = "getAchievementForUser/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public List<Achievements> getAchievementsForUser(@Named("razerID") String razerID){
        try {
            checkExists(razerID);

            User user = new User();
            user.setRazerID(razerID);
            user = ofy().load().entity(user).now();
            List<Achievements> listAchieve = new ArrayList<Achievements>();
            for(Ref<Achievements> ref : user.getAchievements()){
                listAchieve.add(ref.get());
            }
            return listAchieve;
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @ApiMethod(
            name = "getUserPosts",
            path = "getUserPosts/{razerID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public List<Post> getUserPosts(@Named("razerID") String razerID){
        try {
            checkExists(razerID);

            User user = new User();
            user.setRazerID(razerID);
            user = ofy().load().entity(user).now();
            List<Ref<Post>> list = new ArrayList<Ref<Post>>(user.getPosts());
            List<Post> posts = new ArrayList<Post>();
            for(Ref<Post> ref : list){
                posts.add(ref.get());
            }
            return posts;
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
                    Ref<Achievements> refAch = Ref.create(newAchievement);
                    List<Ref<Achievements>> list = user.getAchievements();
                    if (list == null)
                        list = new ArrayList<Ref<Achievements>>();

                    list.add(refAch);
                    user.setAchievements(list);
                }

                Post post = new Post();
                post.setTitle("I helped somebody!");
                post.setPostID(UUID.randomUUID().toString().replace("-", ""));
                post.setFacebookPostID("434535");
                post.setTimePosted(Calendar.getInstance().getTimeInMillis());
                post.setUser(Ref.create(user));

                List<Ref<Post>> postsList = user.getPosts();
                if (postsList == null)
                    postsList = new ArrayList<Ref<Post>>();
                postsList.add(Ref.create(post));
                user.setPosts(postsList);
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
        for(Ref<Achievements> ref : user.getAchievements()){
               list.add(ref.get());
        }
        for(Achievements achievements : list){
            if(achievements.getAchievementID() == ach.getAchievementID()){
                found = true;
            }
        }
        return found;
    }
}