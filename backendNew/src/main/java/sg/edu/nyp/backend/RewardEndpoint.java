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
        name = "rewardApi",
        version = "v1",
        resource = "reward",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class RewardEndpoint {

    private static final Logger logger = Logger.getLogger(RewardEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Reward.class);
    }

    /**
     * Returns the {@link Reward} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Reward} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "reward/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Reward get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Reward with ID: " + id);
        Reward reward = ofy().load().type(Reward.class).id(id).now();
        if (reward == null) {
            throw new NotFoundException("Could not find Reward with ID: " + id);
        }
        return reward;
    }

    /**
     * Inserts a new {@code Reward}.
     */
    @ApiMethod(
            name = "insert",
            path = "reward",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Reward insert(Reward reward) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that reward.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(reward).now();
        logger.info("Created Reward with ID: " + reward.getId());

        return ofy().load().entity(reward).now();
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
            path = "reward",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Reward> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Reward> query = ofy().load().type(Reward.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Reward> queryIterator = query.iterator();
        List<Reward> rewardList = new ArrayList<Reward>(limit);
        while (queryIterator.hasNext()) {
            rewardList.add(queryIterator.next());
        }
        return CollectionResponse.<Reward>builder().setItems(rewardList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Reward.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Reward with ID: " + id);
        }
    }
}