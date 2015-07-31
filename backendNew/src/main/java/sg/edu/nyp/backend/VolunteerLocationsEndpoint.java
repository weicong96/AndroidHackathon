package sg.edu.nyp.backend;

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
        name = "volunteerLocationsApi",
        version = "v1",
        resource = "volunteerLocations",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class VolunteerLocationsEndpoint {

    private static final Logger logger = Logger.getLogger(VolunteerLocationsEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(VolunteerLocations.class);
    }

    /**
     * Returns the {@link VolunteerLocations} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code VolunteerLocations} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "volunteerLocations/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public VolunteerLocations get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting VolunteerLocations with ID: " + id);
        VolunteerLocations volunteerLocations = ofy().load().type(VolunteerLocations.class).id(id).now();
        if (volunteerLocations == null) {
            throw new NotFoundException("Could not find VolunteerLocations with ID: " + id);
        }
        return volunteerLocations;
    }
    @ApiMethod(
            name = "getLocationsForUser",
            path = "getLocationsForUser/{razerID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<VolunteerLocations> getLocationsForUser(@Named("razerID") String razerID){
        User user = new User();
        user.setRazerID(razerID);
        user = ofy().load().entity(user).now();
        List<VolunteerLocations> list = ofy().load().type(VolunteerLocations.class).filter("userRef", Key.create(user)).list();
        return list;
    }

    /**
     * Inserts a new {@code VolunteerLocations}.
     */
    @ApiMethod(
            name = "insert",
            path = "volunteerLocations/{razerID}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public VolunteerLocations insert(VolunteerLocations volunteerLocations,@Named("razerID") String razerID) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that volunteerLocations.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        User user = new User();
        user.setRazerID(razerID);
        user = ofy().load().entity(user).now();
        volunteerLocations.setUserRef(Ref.create(user));
        ofy().save().entity(volunteerLocations).now();

        logger.info("Created VolunteerLocations with ID: " + volunteerLocations.getId());

        return ofy().load().entity(volunteerLocations).now();
    }

    /**
     * Updates an existing {@code VolunteerLocations}.
     *
     * @param id                 the ID of the entity to be updated
     * @param volunteerLocations the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code VolunteerLocations}
     */
    @ApiMethod(
            name = "update",
            path = "volunteerLocations/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public VolunteerLocations update(@Named("id") Long id, VolunteerLocations volunteerLocations) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(volunteerLocations).now();
        logger.info("Updated VolunteerLocations: " + volunteerLocations);
        return ofy().load().entity(volunteerLocations).now();
    }

    /**
     * Deletes the specified {@code VolunteerLocations}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code VolunteerLocations}
     */
    @ApiMethod(
            name = "remove",
            path = "volunteerLocations/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(VolunteerLocations.class).id(id).now();
        logger.info("Deleted VolunteerLocations with ID: " + id);
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
            path = "volunteerLocations",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<VolunteerLocations> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<VolunteerLocations> query = ofy().load().type(VolunteerLocations.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<VolunteerLocations> queryIterator = query.iterator();
        List<VolunteerLocations> volunteerLocationsList = new ArrayList<VolunteerLocations>(limit);
        while (queryIterator.hasNext()) {
            volunteerLocationsList.add(queryIterator.next());
        }
        return CollectionResponse.<VolunteerLocations>builder().setItems(volunteerLocationsList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(VolunteerLocations.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find VolunteerLocations with ID: " + id);
        }
    }
}