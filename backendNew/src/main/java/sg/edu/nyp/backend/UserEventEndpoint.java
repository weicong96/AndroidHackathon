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
        name = "userEventApi",
        version = "v1",
        resource = "userEvent",
        namespace = @ApiNamespace(
                ownerDomain = "backend.nyp.edu.sg",
                ownerName = "backend.nyp.edu.sg",
                packagePath = ""
        )
)
public class UserEventEndpoint {

    private static final Logger logger = Logger.getLogger(UserEventEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(UserEvent.class);
        ObjectifyService.register(Event.class);
        ObjectifyService.register(User.class);

    }

    /**
     * Returns the {@link UserEvent} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserEvent} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userEvent/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserEvent get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting UserEvent with ID: " + id);
        UserEvent userEvent = ofy().load().type(UserEvent.class).id(id).now();
        if (userEvent == null) {
            throw new NotFoundException("Could not find UserEvent with ID: " + id);
        }
        return userEvent;
    }

    /**
     * Inserts a new {@code UserEvent}.
     */
    @ApiMethod(
            name = "insert",
            path = "userEvent",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserEvent insert(UserEvent userEvent) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userEvent.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(userEvent).now();
        logger.info("Created UserEvent.");

        return ofy().load().entity(userEvent).now();
    }

    @ApiMethod(
            name = "insertEvent",
            path = "insertEvent",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Event insertEvent(Event userEvent) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userEvent.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(userEvent).now();
        logger.info("Created UserEvent.");

        return ofy().load().entity(userEvent).now();
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
            path = "userEvent",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserEvent> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserEvent> query = ofy().load().type(UserEvent.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserEvent> queryIterator = query.iterator();
        List<UserEvent> userEventList = new ArrayList<UserEvent>(limit);
        while (queryIterator.hasNext()) {
            userEventList.add(queryIterator.next());
        }
        return CollectionResponse.<UserEvent>builder().setItems(userEventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name="getUsersAttendingEvent",
            path="getUsersAttendingEvent/{eventID}",
            httpMethod= ApiMethod.HttpMethod.POST
    )
    public List<User> getUsersAttendingEvent(@Named("eventID") Long eventID){
            List<User> usersList = new ArrayList<User>();
            Event event = new Event();
            event.setEventID(eventID);
            event = ofy().load().entity(event).now();
            if(event == null)
                logger.info("Problems with event being null!!!");

            List<UserEvent> userEvents = ofy().load().type(UserEvent.class).filter("eventRef", Key.create(event)).list();

            logger.info(String.valueOf(userEvents.size()));
            for(UserEvent userEvent : userEvents){
                usersList.add(userEvent.getUser());
            }
            return usersList;

    }

    @ApiMethod(
            name="getEvents",
            path="getEvents",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public CollectionResponse<Event> getEvents(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Event> query = ofy().load().type(Event.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<Event>(limit);
        while (queryIterator.hasNext()) {
            eventList.add(queryIterator.next());
        }
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }


    @ApiMethod(
            name="getEvent",
            path="getEvent/{eventID}",
            httpMethod= ApiMethod.HttpMethod.GET
    )
    public Event getEventID(@Named("eventID") Long eventID){
        Event event = new Event();
        event.setEventID(eventID);
        event = ofy().load().entity(event).now();
        return event;
    }
    @ApiMethod(
            name="attendForEvent",
            path="attendEvent/{eventID}",
            httpMethod= ApiMethod.HttpMethod.POST
    )
    public UserEvent attendForEvent(@Named("eventID") Long eventID, @Named("userID") String userID, @Named("attending") Boolean attendingStatus){
        //Check if user exists first
        User user = new User();
        user.setRazerID(userID);
        user = ofy().load().entity(user).now();

        if(user != null){
            Event event = new Event();
            event.setEventID(eventID);
            event = ofy().load().entity(event).now();
            if(event != null){
                //check if event exists for validation purpsoes

                try{
                    List<UserEvent> userEvents = ofy().load().type(UserEvent.class).filter("userRef", Key.create(user)).filter("eventRef", Key.create(event)).list();
                    logger.info(String.valueOf(userEvents.size()));
                    if(userEvents.size() == 0)
                        createAttendanceRecord(event, user, attendingStatus);
                    else {
                        UserEvent userEvent = userEvents.get(0);
                        userEvent.setUserRef(Ref.create(user));
                        userEvent.setEventRef(Ref.create(event));
                        userEvent.setAttending(attendingStatus);

                        //Save event to database after making changes
                        ofy().save().entity(userEvent).now();
                    }
                }catch(IllegalArgumentException ex){
                    logger.info("User event datastore is empty");

                    //Create record here, because event datastore is empty.
                    createAttendanceRecord(event,user,attendingStatus);
                }

            }
        }
        return null;
    }

    private void createAttendanceRecord(Event event, User user,Boolean attending){
        UserEvent userEvent = new UserEvent();
        userEvent.setUserRef(Ref.create(user));
        userEvent.setEventRef(Ref.create(event));
        userEvent.setAttending(attending);

        ofy().save().entity(userEvent).now();
    }
}