package sg.edu.nyp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
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
        name = "achievementsApi",
        version = "v1",
        resource = "achievements",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class AchievementsEndpoint {

    private static final Logger logger = Logger.getLogger(AchievementsEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Achievements.class);
    }

    /**
     * Returns the {@link Achievements} with the corresponding ID.
     *
     * @param achievementID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Achievements} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "achievements/{achievementID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Achievements get(@Named("achievementID") Long achievementID) throws NotFoundException {
        logger.info("Getting Achievements with ID: " + achievementID);
        Achievements achievements = ofy().load().type(Achievements.class).id(achievementID).now();
        if (achievements == null) {
            throw new NotFoundException("Could not find Achievements with ID: " + achievementID);
        }
        return achievements;
    }

    /**
     * Inserts a new {@code Achievements}.
     */
    @ApiMethod(
            name = "insert",
            path = "achievements",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Achievements insert(Achievements achievements) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that achievements.achievementID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(achievements).now();
        logger.info("Created Achievements with ID: " + achievements.getAchievementID());

        return ofy().load().entity(achievements).now();
    }

    /**
     * Updates an existing {@code Achievements}.
     *
     * @param achievementID the ID of the entity to be updated
     * @param achievements  the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code achievementID} does not correspond to an existing
     *                           {@code Achievements}

    @ApiMethod(
            name = "update",
            path = "achievements/{achievementID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Achievements update(@Named("achievementID") Long achievementID, Achievements achievements) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(achievementID);
        ofy().save().entity(achievements).now();
        logger.info("Updated Achievements: " + achievements);
        return ofy().load().entity(achievements).now();
    }


     * Deletes the specified {@code Achievements}.
     *
     * @param achievementID the ID of the entity to delete
     * @throws NotFoundException if the {@code achievementID} does not correspond to an existing
     *                           {@code Achievements}
     */
    @ApiMethod(
            name = "remove",
            path = "achievements/{achievementID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("achievementID") Long achievementID) throws NotFoundException {
        checkExists(achievementID);
        ofy().delete().type(Achievements.class).id(achievementID).now();
        logger.info("Deleted Achievements with ID: " + achievementID);
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
            path = "achievements",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Achievements> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Achievements> query = ofy().load().type(Achievements.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Achievements> queryIterator = query.iterator();
        List<Achievements> achievementsList = new ArrayList<Achievements>(limit);
        while (queryIterator.hasNext()) {
            achievementsList.add(queryIterator.next());
        }
        return CollectionResponse.<Achievements>builder().setItems(achievementsList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long achievementID) throws NotFoundException {
        try {
            ofy().load().type(Achievements.class).id(achievementID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Achievements with ID: " + achievementID);
        }
    }
}