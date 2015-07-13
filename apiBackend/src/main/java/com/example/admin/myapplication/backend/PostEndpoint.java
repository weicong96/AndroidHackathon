package com.example.admin.myapplication.backend;

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
        name = "postApi",
        version = "v1",
        resource = "post",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.admin.example.com",
                ownerName = "backend.myapplication.admin.example.com",
                packagePath = ""
        )
)
public class PostEndpoint {

    private static final Logger logger = Logger.getLogger(PostEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Post.class);
    }

    /**
     * Returns the {@link Post} with the corresponding ID.
     *
     * @param postID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Post} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "post/{postID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Post get(@Named("postID") String postID) throws NotFoundException {
        logger.info("Getting Post with ID: " + postID);
        Post post = ofy().load().type(Post.class).id(postID).now();
        if (post == null) {
            throw new NotFoundException("Could not find Post with ID: " + postID);
        }
        return post;
    }

    /**
     * Inserts a new {@code Post}.
     */
    @ApiMethod(
            name = "insert",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Post insert(Post post) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that post.postID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(post).now();
        logger.info("Created Post with ID: " + post.getPostID());

        return ofy().load().entity(post).now();
    }

    /**
     * Updates an existing {@code Post}.
     *
     * @param postID the ID of the entity to be updated
     * @param post   the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code postID} does not correspond to an existing
     *                           {@code Post}

    @ApiMethod(
            name = "update",
            path = "post/{postID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Post update(@Named("postID") String postID, Post post) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(postID);
        ofy().save().entity(post).now();
        logger.info("Updated Post: " + post);
        return ofy().load().entity(post).now();
    }
*/
    /**
     * Deletes the specified {@code Post}.
     *
     * @param postID the ID of the entity to delete
     * @throws NotFoundException if the {@code postID} does not correspond to an existing
     *                           {@code Post}
     */
    @ApiMethod(
            name = "remove",
            path = "post/{postID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("postID") String postID) throws NotFoundException {
        checkExists(postID);
        ofy().delete().type(Post.class).id(postID).now();
        logger.info("Deleted Post with ID: " + postID);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    //
    @ApiMethod(
            name = "list",
            path = "post",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Post> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Post> query = ofy().load().type(Post.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Post> queryIterator = query.iterator();
        List<Post> postList = new ArrayList<Post>(limit);
        while (queryIterator.hasNext()) {
            postList.add(queryIterator.next());
        }
        return CollectionResponse.<Post>builder().setItems(postList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String postID) throws NotFoundException {
        try {
            ofy().load().type(Post.class).id(postID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Post with ID: " + postID);
        }
    }
}