package com.brunoandrade.quarkussocial.rest;

import com.brunoandrade.quarkussocial.domain.model.Follower;
import com.brunoandrade.quarkussocial.domain.model.User;
import com.brunoandrade.quarkussocial.domain.repository.FollowerRepository;
import com.brunoandrade.quarkussocial.domain.repository.UserRepository;
import com.brunoandrade.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //Standard test user
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();

        var userFollower = new User();
        userFollower.setName("Ciclano");
        userFollower.setAge(35);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        //create a follower
        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when userId doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {

        var nonExistentUserId = 999;
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonExistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userFollowerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and userId doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {

        var nonExistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonExistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void listFollowersTest() {
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                .when()
                        .get()
                .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }
}