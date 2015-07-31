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
        name = "helpRequestApi",
        version = "v1",
        resource = "helpRequest",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class HelpRequestEndpoint {

    private static final Logger logger = Logger.getLogger(HelpRequestEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(HelpRequest.class);
    }

    /**
     * Returns the {@link HelpRequest} with the corresponding ID.
     *
     * @param requestID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code HelpRequest} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "helpRequest/{requestID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public HelpRequest get(@Named("requestID") Long requestID) throws NotFoundException {
        logger.info("Getting HelpRequest with ID: " + requestID);
        HelpRequest helpRequest = ofy().load().type(HelpRequest.class).id(requestID).now();
        if (helpRequest == null) {
            throw new NotFoundException("Could not find HelpRequest with ID: " + requestID);
        }
        return helpRequest;
    }
    /**
     * Inserts a new {@code HelpRequest}.
     */
    @ApiMethod(
            name = "insert",
            path = "helpRequest",
            httpMethod = ApiMethod.HttpMethod.POST)
    public HelpRequest insert(HelpRequest helpRequest) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that helpRequest.requestID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(helpRequest).now();
        logger.info("Created HelpRequest with ID: " + helpRequest.getRequestID());

        return ofy().load().entity(helpRequest).now();
    }
    @ApiMethod(
            name="getNearbyHelpRequests",
            path= "getNearbyHelpRequests",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<HelpRequest> getNearbyHelp(@Named("lat") final double lat , @Named("lng") final double lng){
        List<HelpRequest> helpRequests = ofy().load().type(HelpRequest.class).filter("resolved != ", true).list();

        Collections.sort(helpRequests, new Comparator<HelpRequest>() {
            @Override
            public int compare(HelpRequest o1, HelpRequest o2) {
                double distanceDiffo1 = distance(o1.getLat(), o1.getLng(), lat, lng);
                double distanceDiffo2 = distance(o2.getLat(), o2.getLng(), lat, lng);

                return Double.compare(distanceDiffo1, distanceDiffo2);
            }
        });
        for(int i = 0 ; i < helpRequests.size();i++){
            double distance = distance(helpRequests.get(i).getLat(), helpRequests.get(i).getLng(), lat, lng);
            helpRequests.get(i).setDistanceFromU(distance);
        }

        return helpRequests;
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
            name = "acceptHelpRequest",
            path = "helpRequest/{requestID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public void acceptHelpRequest(@Named("requestID") Long requestID, @Named("razerID") String razerID){
        HelpRequest request = new HelpRequest();
        request.setRequestID(requestID);
        request = ofy().load().entity(request).now();

        User user = new User();
        user.setRazerID(razerID);
        user = ofy().load().entity(user).now();

        request.setUserRef(Ref.create(user));
        request.setResolved(true);
        ofy().save().entity(request).now();

        //When my help request is accepted, sned needy notification


    }
    /**
     * Updates an existing {@code HelpRequest}.
     *
     * @param requestID   the ID of the entity to be updated
     * @param helpRequest the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code requestID} does not correspond to an existing
     *                           {@code HelpRequest}

    @ApiMethod(
            name = "update",
            path = "helpRequest/{requestID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public HelpRequest update(@Named("requestID") Long requestID, HelpRequest helpRequest) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(requestID);
        ofy().save().entity(helpRequest).now();
        logger.info("Updated HelpRequest: " + helpRequest);
        return ofy().load().entity(helpRequest).now();
    }
    */
    /**
     * Deletes the specified {@code HelpRequest}.
     *
     * @param requestID the ID of the entity to delete
     * @throws NotFoundException if the {@code requestID} does not correspond to an existing
     *                           {@code HelpRequest}
     */
    @ApiMethod(
            name = "remove",
            path = "helpRequest/{requestID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("requestID") Long requestID) throws NotFoundException {
        checkExists(requestID);
        ofy().delete().type(HelpRequest.class).id(requestID).now();
        logger.info("Deleted HelpRequest with ID: " + requestID);
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
            path = "helpRequest",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<HelpRequest> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<HelpRequest> query = ofy().load().type(HelpRequest.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<HelpRequest> queryIterator = query.iterator();
        List<HelpRequest> helpRequestList = new ArrayList<HelpRequest>(limit);
        while (queryIterator.hasNext()) {
            helpRequestList.add(queryIterator.next());
        }
        return CollectionResponse.<HelpRequest>builder().setItems(helpRequestList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long requestID) throws NotFoundException {
        try {
            ofy().load().type(HelpRequest.class).id(requestID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find HelpRequest with ID: " + requestID);
        }
    }
}