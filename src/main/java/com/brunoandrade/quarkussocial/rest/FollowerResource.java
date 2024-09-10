package com.brunoandrade.quarkussocial.rest;

import com.brunoandrade.quarkussocial.domain.model.Follower;
import com.brunoandrade.quarkussocial.domain.model.User;
import com.brunoandrade.quarkussocial.domain.repository.FollowerRepository;
import com.brunoandrade.quarkussocial.domain.repository.UserRepository;
import com.brunoandrade.quarkussocial.rest.dto.FollowerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    public Response followers(
            @PathParam("userId") Long userId,
            FollowerRequest followerRequest
    ){
       User user = userRepository.findById(userId);
       if(user == null) {
           return Response.status(Response.Status.NOT_FOUND).build();
       }

       User follower = userRepository.findById(followerRequest.getFollowerId());

       var entity = new Follower();
       entity.setUser(user);
       entity.setFollower(follower);

       followerRepository.persist(entity);
       return Response.status(Response.Status.NO_CONTENT).build();




    }
}
