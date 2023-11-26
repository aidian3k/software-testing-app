package project.eepw.softwaretestingcrud.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.ThrowableAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostBehaviourTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<PostDTO> lastPostResponse;
    private ResponseEntity<PostDTO[]> lastPostsResponse;
    private ThrowableAssert.ThrowingCallable lastErrorResponse;

    @Given("In db there is a user with id {int}, name {string} and email {string}")
    public void inDbThereIsUserWithIdNameAndEmail(
            int userId,
            String name,
            String email
    ) {
        String password = "user-password";
        String surname = "user-surname";

        User user = User.builder()
                .id((long) userId)
                .name(name)
                .surname(surname)
                .email(email)
                .password(password)
                .build();
        
        restTemplate.postForEntity(
                createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
                user,
                User.class
        );
    }

    @When("The user with id {int} tries to add new posts with valid content {string}")
    public void theUserWithIdTriesToAddNewPostsWithValidContent(int userId, String content) {
        PostCreationDTO postCreationDTO = PostCreationDTO.builder().content(content).build();

        lastPostResponse = restTemplate.postForEntity(
                createLocalURIWithGivenPortNumber(port, CREATE_POST_URL_WITHOUT_USER_ID + userId),
                postCreationDTO,
                PostDTO.class
        );
    }


    @Then("The system should return post with content {string}")
    public void theSystemShouldReturnPostWithContentContent(String content) {
        PostDTO createdPost = lastPostResponse.getBody();

        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getContent()).isEqualTo(content);
    }

    @And("The system should return the {int} response code to the user")
    public void theSystemShouldReturnTheResponseCodeToTheUser(int statusCode) {
        assertThat(lastPostResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @When("The user with id {int} tries to add new post with content which has length {int}")
    public void theUserWithIdTriesToAddNewPostWithContentWhichHasLength(int userId, int contentLength) {
        String content = IntStream.range(0, contentLength)
                .mapToObj(i -> String.valueOf((char) ('a' + i % 26)))
                .collect(Collectors.joining());
        PostCreationDTO postCreationDTO = PostCreationDTO.builder().content(content).build();

        lastErrorResponse = () -> restTemplate.postForEntity(
                createLocalURIWithGivenPortNumber(port, CREATE_POST_URL_WITHOUT_USER_ID + userId),
                postCreationDTO,
                Map.class
        );
    }

    @Then("The system should return {int} response code to the user")
    public void theSystemShouldReturnResponseCodeToTheUser(int expectedStatusCode) {
        assertThatThrownBy(lastErrorResponse)
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    assertThat(httpClientErrorException.getStatusCode().value()).isEqualTo(expectedStatusCode);
                });
    }

    @Given("The user with id {int} has at least two posts added")
    public void theUserWithIdHasAtLeastTwoPostsAdded(int userId) {
        List<PostCreationDTO> userPosts = List.of(
            PostCreationDTO.builder().content("aaa").build(),
            PostCreationDTO.builder().content("bbb").build()
        );

        for (PostCreationDTO post: userPosts) {
            lastPostResponse = restTemplate.postForEntity(
                    createLocalURIWithGivenPortNumber(port, CREATE_POST_URL_WITHOUT_USER_ID + userId),
                    post,
                    PostDTO.class
            );
        }
    }

    @When("The user with id {int} wants to see all posts data")
    public void theUserWithIdWantsToSeeAllPostsData(int userId) {
        lastPostsResponse = restTemplate.getForEntity(
                createLocalURIWithGivenPortNumber(port, GET_ALL_POSTS_URL + "/user/" + userId),
                PostDTO[].class
        );
    }

    @Then("The system should return the list of posts with size greater than {int}")
    public void theSystemShouldReturnTheListOfPostsWithSizeGreaterThan(int minPostsNumber) {
        assertThat(lastPostsResponse.getBody()).isNotNull();
        assertThat(lastPostsResponse.getBody().length).isGreaterThan(minPostsNumber);
    }

    @And("The system should return {int} response code")
    public void theSystemShouldReturnResponseCode(int statusCode) {
        assertThat(lastPostsResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Given("In DB there is post by user with id {int} and content {string}")
    public void inDBThereIsPostByUserWithIdAndContent(int userId, String content) {
        PostCreationDTO post = PostCreationDTO.builder()
                .content(content)
                .build();

        lastPostResponse = restTemplate.postForEntity(
                createLocalURIWithGivenPortNumber(port, CREATE_POST_URL_WITHOUT_USER_ID + userId),
                post,
                PostDTO.class
        );
    }

    @When("The user wants to find the post by id")
    public void theUserWithIdWantsToFindThePostWithId() {
        Long lastPostId = Objects.requireNonNull(lastPostResponse.getBody()).getId();

        lastPostResponse = restTemplate.getForEntity(
                createLocalURIWithGivenPortNumber(port, GET_POST_BY_ID_URL + lastPostId),
                PostDTO.class
        );
    }

    @Then("The system should return requested post with content {string}")
    public void theSystemShouldReturnRequestedPostWithContent(String expectedContent) {
        assertThat(lastPostResponse.getBody()).isNotNull();
        assertThat(lastPostResponse.getBody().getContent()).isEqualTo(expectedContent);
    }

    @And("The system response should be {int}")
    public void theSystemResponseShouldBe(int statusCode) {
        assertThat(lastPostResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Given("In database there is no post with id {int}")
    public void inDatabaseThereIsNoPostWithId(int postId) {
        try {
            restTemplate.delete(DELETE_POST_BY_ID_URL + postId);
        } catch(Exception ignored) {

        }
    }

    @When("The user wants to get the data of post with id {int}")
    public void theUserWantsToGetTheDataOfPostWithId(int postId) {
        lastErrorResponse = () -> restTemplate.getForEntity(
                createLocalURIWithGivenPortNumber(port, GET_POST_BY_ID_URL + postId),
                PostDTO.class
        );
    }

    @Then("The system should return {int} not found to the user")
    public void theSystemShouldReturnNotFoundToTheUser(int statusCode) {
        assertThatThrownBy(lastErrorResponse)
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(e -> {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    assertThat(httpClientErrorException.getStatusCode().value()).isEqualTo(statusCode);
                });
    }
}
