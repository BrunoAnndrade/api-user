package com.brunoandrade.quarkussocial.rest;

import com.brunoandrade.quarkussocial.domain.model.User;
import com.brunoandrade.quarkussocial.domain.repository.UserRepository;
import com.brunoandrade.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;

    @BeforeEach
    @Transactional
    public void setUp(){
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();
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

}