package com.brunoandrade.quarkussocial.rest;

import com.brunoandrade.quarkussocial.domain.model.Follower;
import com.brunoandrade.quarkussocial.domain.model.Post;
import com.brunoandrade.quarkussocial.domain.model.User;
import com.brunoandrade.quarkussocial.domain.repository.FollowerRepository;
import com.brunoandrade.quarkussocial.domain.repository.PostRepository;
import com.brunoandrade.quarkussocial.domain.repository.UserRepository;
import com.brunoandrade.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp(){

        //Standard test user
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();

        var userNotFollower = new User();
        userNotFollower.setName("Beltrano");
        userNotFollower.setAge(33);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        var userFollower = new User();
        userFollower.setName("Ciclano");
        userFollower.setAge(35);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

        // CREATE POST
        Post post = new Post();
        post.setText("Some Text");
        post.setUser(user);
        postRepository.persist(post);
    }

    @Test
    @DisplayName("Should create a post for user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .post()
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 error when trying to make a post for an non-existent user")
    public void postForAnNonExistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some Text");

        var nonExistentUser = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", nonExistentUser)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest() {
        var invalidUserId = 999;

        given()
                .pathParam("userId", invalidUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId it's not present ")
    public void listPostFollowerHeaderNotSendTest() {

        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist ")
    public void listPostFollowerNotFoundTest() {

        var invalidFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", invalidFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("A non-existent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when user isn't a follower")
    public void listPostNotFollowerTest() {

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostsTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}