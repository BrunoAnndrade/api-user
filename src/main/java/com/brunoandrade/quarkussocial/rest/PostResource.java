package com.brunoandrade.quarkussocial.rest;

import com.brunoandrade.quarkussocial.domain.model.Post;
import com.brunoandrade.quarkussocial.domain.model.User;
import com.brunoandrade.quarkussocial.domain.repository.FollowerRepository;
import com.brunoandrade.quarkussocial.domain.repository.PostRepository;
import com.brunoandrade.quarkussocial.domain.repository.UserRepository;
import com.brunoandrade.quarkussocial.rest.dto.CreatePostRequest;
import com.brunoandrade.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowerRepository followerRepository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRepository postRepository,
            FollowerRepository followerRepository
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setUser(user);
        post.setText(request.getText());

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId
    ) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("A non-existent followerId").build();

        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }


        PanacheQuery<Post> query = postRepository.find(
                "user", Sort.by("dateTime", Sort.Direction.Descending), user);
        List<Post> postList = query.list();

        List<PostResponse> postResponseList = postList
                .stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
