package RestAssuredTests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.List;

import static io.restassured.RestAssured.*;

class LoginDto {
    String usernameOrEmail;
    String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


public class RestAssuredTestsSkillo {
    // --- global variables ----
    String username;
    String fullAuthToken;
    SecureRandom random;
    String userID;
    String postID = "5355";
    String userIDtoFollow = "2931";
    int followersCount;
    String myPublicPostID;
    String myPrivatePostID;
    String myCommentID;
    String[] commentToArray = null;
    String listOfComments;
    int likesCount;
    int likesCountAfterQuery;

    //---- for delete query ---
    String userIdForDeleteQuery;
    JSONObject loginCredentials = new JSONObject();


    @BeforeTest
    public void setUp() {
        baseURI = "http://training.skillo-bg.com:3100";
        random = new SecureRandom();
    }

    @Test(groups = "signUpPlusLogin")
    public void testSignUp() {
        username = "ivo_" + String.valueOf(random.nextInt(100000));
        JSONObject randomUser = new JSONObject();
        JSONObject signUpCredentials = new JSONObject();
        signUpCredentials.put("username", username);
        String generatedEmail = signUpCredentials.get("username") + "@test.com";
        signUpCredentials.put("email", generatedEmail);
        signUpCredentials.put("birthDate", "01.01.1995");  // <-------???
        signUpCredentials.put("password", "Qwerty1");
        signUpCredentials.put("publicInfo", "BIO info automation.");

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(signUpCredentials)
                .post("/users")
                .then()
                .log()
                .all()
                .extract()
                .response();

        int statusCodeResponse = response.statusCode();
        String responseUserID = response.jsonPath().getString("id");
        userIdForDeleteQuery = responseUserID;
        String usernameResponse = response.jsonPath().getString("username");


        loginCredentials.put("usernameOrEmail", usernameResponse);
        loginCredentials.put("password", signUpCredentials.get("password"));


        Assert.assertEquals(statusCodeResponse, HttpStatus.SC_CREATED);
        Assert.assertFalse(responseUserID.isEmpty());
        Assert.assertEquals(usernameResponse, username);
    }

    @Test
    public void testSignUpUsernameTaken() {

        JSONObject userCredentials = new JSONObject();
        userCredentials.put("username", "Ivo2");
        String generatedEmail = userCredentials.get("username") + "@automation.com";
        userCredentials.put("email", generatedEmail);
        userCredentials.put("birthDate", "01.01.1995");  // <-------???
        userCredentials.put("password", "Qwerty1");
        userCredentials.put("publicInfo", "BIO info automation.");

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(userCredentials)
                .post("/users")
                .then()
                .log().all()
                .extract()
                .response();

        int statusCodeResponse = response.getStatusCode();
        String messageResponse = response.jsonPath().getString("message");

        Assert.assertEquals(statusCodeResponse, HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(messageResponse, "Username taken");
    }

    @Test
    public void testSignUpLongUsername() {
        username = "ivo_" + String.valueOf(random.nextInt(90000));

        JSONObject randomUser = new JSONObject();
        randomUser.put("username", username);
        randomUser.put("email", "21_chars_email@aa.com");
        randomUser.put("password", "Qwerty1");

        Response response = given()
                .log()
                .all()
                .contentType(ContentType.JSON)
                .when()
                .body(randomUser)
                .post("/users")
                .then()
                .log()
                .all()
                .extract()
                .response();

        String maxLengthMessage = response.jsonPath().getString("message.constraints.maxLength");
        int responseStatusCode = response.statusCode();

        System.out.println(maxLengthMessage);

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(maxLengthMessage, "[email must be shorter than or equal to 20 characters]");
    }

    @Test(groups = "signUpPlusLogin", dependsOnMethods = "testSignUp")
    public void testLogin() {

//        LoginDto user1 = new LoginDto();
//        user1.setUsernameOrEmail("Ivaylo123");
//        user1.setPassword("Qwerty1");

        System.out.println(loginCredentials);

        Response response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .body(loginCredentials)
                .post("/users/login")
                .then()
                .log().all()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_CREATED);
        String usernameFromResponse = response.jsonPath().get("user.username");
        String token = response.jsonPath().getString("token");
        Assert.assertFalse(token.isEmpty());
        fullAuthToken = "Bearer " + response.jsonPath().getString("token");
        userID = response.jsonPath().getString("user.id");

        System.out.println(fullAuthToken);
    }

    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testListAllUsers() {
        Response response = given()
                // .header("Content-Type", "application/json")
                .header("Authorization", fullAuthToken)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);

        System.out.println(fullAuthToken);
    }

    @Test
    public void testLoadLastThreeFeedPosts() {

        int numberOfPostsToTake = 3;
        int numberOfPostsToSkip = 0;

        Response response = given()
                .log().all()
                .queryParam("take", numberOfPostsToTake)
                .queryParam("skip", numberOfPostsToSkip)
                .contentType(ContentType.JSON)
                .when()
                .get("/posts")
                .then()
                .log().all()
                .extract()
                .response();

        List<JSONObject> listOfPosts = response.jsonPath().get();
        int responseStatusCode = response.statusCode();

        Assert.assertEquals(listOfPosts.size(), numberOfPostsToTake);
        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);


    }

    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testOpenYourProfile() {

        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", fullAuthToken)
                .when()
                .get("/users/" + userID)
                .then()
                .log()
                .all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        int responseUserID = response.jsonPath().getInt("id");


        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(responseUserID, Integer.parseInt(userID));

    }

    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testCreatePublicPost() {

        String imageURL = "https://i.guim.co.uk/img/media/26392d05302e02f7bf4eb143bb84c8097d09144b/446_167_3683_2210/master/3683.jpg?width=1200&height=1200&quality=85&auto=format&fit=crop&s=49ed3252c0b2ffb49cf8b508892e452d";

        JSONObject body = new JSONObject();
        body.put("caption", "Test caption. Have a nice day!");
        body.put("coverUrl", imageURL);
        body.put("postStatus", "public");


        Response response = given()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("posts/")
                .then()
                .log()
                .all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responsePostId = response.jsonPath().getString("id");
        String responsePostType = response.jsonPath().getString("postStatus");

        myPublicPostID = responsePostId;

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED);
        Assert.assertTrue(responsePostId != null);
        Assert.assertEquals(responsePostType, "public");

    }

    @Test(dependsOnGroups = "signUpPlusLogin", dependsOnMethods = "testCreatePublicPost")
    public void testChangePostStatusToPrivet() {

        JSONObject body = new JSONObject();
        body.put("postStatus", "private");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .put("/posts/" + myPublicPostID)
                .then()
                .log().all()
                .extract()
                .response();

        int responseStatus = response.statusCode();
        String responsePostStatus = response.jsonPath().getString("postStatus");

        Assert.assertEquals(responseStatus, HttpStatus.SC_OK);
        Assert.assertEquals(responsePostStatus, "private");


    }

    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testCreatePrivatePost() {

        String imageURL = "https://www.meme-arsenal.com/memes/f5881567243b109d53c82f9548b05320.jpg";

        JSONObject body = new JSONObject();
        body.put("caption", "Test caption. Have a nice day!");
        body.put("coverUrl", imageURL);
        body.put("postStatus", "private");


        Response response = given()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("posts/")
                .then()
                .log()
                .all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responsePostId = response.jsonPath().getString("id");
        String responsePostType = response.jsonPath().getString("postStatus");
        myPrivatePostID = responsePostId;

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED);
        Assert.assertTrue(responsePostId != null);
        Assert.assertEquals(responsePostType, "private");

    }

    @Test(dependsOnGroups = "signUpPlusLogin", dependsOnMethods = "testCreatePrivatePost")
    public void testChangePostStatusToPublic() {

        JSONObject body = new JSONObject();
        body.put("postStatus", "public");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .put("/posts/" + myPrivatePostID)
                .then()
                .log().all()
                .extract()
                .response();

        int responseStatus = response.statusCode();
        String responsePostStatus = response.jsonPath().getString("postStatus");

        Assert.assertEquals(responseStatus, HttpStatus.SC_OK);
        Assert.assertEquals(responsePostStatus, "public");


    }

    @Test(dependsOnGroups = "signUpPlusLogin", groups = "likes")
    public void testGetPost() {

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .when()
                .get("/posts/" + postID)
                .then()
                .log().all()
                .extract()
                .response();

        likesCount = response.jsonPath().getInt("likesCount");
        followersCount = response.jsonPath().getInt("user.followersCount");

        int responseStatusCode = response.statusCode();
        String responsePostID = response.jsonPath().getString("id");

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(responsePostID, postID);

    }

    @Test(dependsOnGroups = "signUpPlusLogin", groups = "comments")
    public void testWriteComment() {

        String comment = "Automated comment 123";

        JSONObject body = new JSONObject();
        body.put("content", comment);

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/posts/" + postID + "/comment")
                .then()
                .log().all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responseComment = response.jsonPath().getString("content");
        myCommentID = response.jsonPath().getString("id");

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_CREATED);
        Assert.assertEquals(responseComment, comment);

    }

    @Test(dependsOnGroups = "signUpPlusLogin", dependsOnMethods = "testWriteComment", groups = "comments")
    public void testOpenPostComment() {

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .when()
                .get("/posts/" + postID + "/comments")
                .then()
                .log().all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responseCommentID = response.jsonPath().getString("id");
        responseCommentID = responseCommentID.replace("]", "");

        commentToArray = responseCommentID.split(" ");
        String lastComment = commentToArray[commentToArray.length - 1];

        Assert.assertEquals(lastComment, myCommentID);
    }


    @Test(dependsOnGroups = {"signUpPlusLogin", "comments"})
    public void testDeleteComment() {

        Response response = given()
                .header("Authorization", fullAuthToken)
                .when()
                .delete("/posts/" + postID + "/comments/" + myCommentID)
                .then()
                .log().all()
                .extract()
                .response();

        String responseDeletedCommentID = response.jsonPath().getString("id");

        Assert.assertEquals(responseDeletedCommentID, myCommentID);

    }

    @Test(dependsOnGroups = "signUpPlusLogin", dependsOnMethods = "testGetPost", groups = "likes")
    public void testLikePost() {

        JSONObject body = new JSONObject();
        body.put("action", "likePost");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .patch("/posts/" + postID)
                .then()
                .log().all()
                .extract()
                .response();

        likesCountAfterQuery = response.jsonPath().getInt("post.likesCount");
        int responseStatusCode = response.statusCode();


        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(likesCount + 1, likesCountAfterQuery);

    }

    @Test(dependsOnGroups = {"signUpPlusLogin", "likes"})
    public void testRemoveLike() {

        JSONObject body = new JSONObject();
        body.put("action", "likePost");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .patch("/posts/" + postID)
                .then()
                .log().all()
                .extract()
                .response();

        int likesCountAfterDislike = response.jsonPath().getInt("post.likesCount");
        int responseStatusCode = response.statusCode();

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(likesCountAfterDislike, likesCountAfterQuery - 1);


    }

    @Test(dependsOnGroups = {"signUpPlusLogin", "likes"})
    public void testFollowUser() {

        JSONObject body = new JSONObject();
        body.put("action", "followUser");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .patch("/users/" + userIDtoFollow)
                .then()
                .log().all()
                .extract()
                .response();

        int followersCountAfterThisQuery = response.jsonPath().getInt("user.followersCount");
        int responseStatusCode = response.statusCode();


        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(followersCountAfterThisQuery, followersCount + 1);

        followersCount = followersCountAfterThisQuery;  // <---- it will be used in unfollow query


    }

    @Test(dependsOnGroups = {"signUpPlusLogin", "likes"}, dependsOnMethods = "testFollowUser")
    public void testUnfollowUser() {

        JSONObject body = new JSONObject();
        body.put("action", "unfollowUser");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .patch("/users/" + userIDtoFollow)
                .then()
                .log().all()
                .extract()
                .response();

        int followersCountAfterThisQuery = response.jsonPath().getInt("user.followersCount");
        int responseStatusCode = response.statusCode();

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(followersCountAfterThisQuery, followersCount - 1);


    }

    @Test (dependsOnGroups = {"signUpPlusLogin", "likes"})
    public void testTryToFollowYourself(){

        JSONObject body = new JSONObject();
        body.put("action", "followUser");

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .patch("/users/" + userID)
                .then()
                .log().all()
                .extract()
                .response();

        String responseMessage = response.jsonPath().getString("message");
        int responseStatusCode = response.statusCode();


        Assert.assertEquals(responseStatusCode, HttpStatus.SC_BAD_REQUEST);
        Assert.assertEquals(responseMessage, "You cannot follow yourself");



    }



    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testDeleteUser() {
        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .when()
                .delete("users/" + userIdForDeleteQuery)
                .then()
                .log()
                .all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responseIsDeleted = response.jsonPath().getString("isDeleted");
        System.out.println(responseIsDeleted);

        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertTrue(response.jsonPath().get("isDeleted"));

    }

    @Test(dependsOnGroups = "signUpPlusLogin")
    public void testLogout() {

        Response response = given()
                .log().all()
                .header("Authorization", fullAuthToken)
                .when()
                .delete("/users/logout")
                .then()
                .log().all()
                .extract()
                .response();

        int responseStatusCode = response.statusCode();
        String responseMessage = response.jsonPath().getString("msg");


        Assert.assertEquals(responseStatusCode, HttpStatus.SC_OK);
        Assert.assertEquals(responseMessage, "User successfully logged out.");


    }


}



