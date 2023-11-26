package project.eepw.softwaretestingcrud.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentCreationDTO;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommentBehaviourTests {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();
	private ResponseEntity<Void> lastDeleteResponse;
	private ResponseEntity<Comment> lastCommentResponse;
	private ResponseEntity<Comment[]> lastCommentsResponse;
	private User backgroundUser;
	private PostDTO backgroundPost;
	private PostDTO secondPost;
	private Comment savedComment;

	@Given(
		"There is added user with with email {string} and name {string} and surname {string} and password {string}"
	)
	public void addUserToSystem(
		String email,
		String name,
		String surname,
		String password
	) {
		User user = User
			.builder()
			.name(name)
			.surname(surname)
			.email(email)
			.password(password)
			.build();

		backgroundUser =
			restTemplate
				.postForEntity(
					createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
					user,
					User.class
				)
				.getBody();
	}

	@And("The user has a post with content {string}")
	public void addPostToExistingUser(String postContent) {
		PostCreationDTO postCreationDTO = PostCreationDTO
			.builder()
			.content(postContent)
			.build();

		backgroundPost =
			restTemplate
				.postForEntity(
					createLocalURIWithGivenPortNumber(
						port,
						GET_ALL_POSTS_URL + "/user/" + backgroundUser.getId()
					),
					postCreationDTO,
					PostDTO.class
				)
				.getBody();
	}

	@Given("The post does not have comments yet")
	public void thePostWithIdDoesNotHaveCommentsYet() {
		Post savedPost = restTemplate
			.getForEntity(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_POSTS_URL + "/" + backgroundPost.getId()
				),
				Post.class
			)
			.getBody();

		assertThat(savedPost.getComments()).isEmpty();
	}

	@When("The user wants to add the comment with content {string} to the post")
	public void addCommentToExistingPost(String commentContent) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.userId(backgroundUser.getId())
			.postId(backgroundPost.getId())
			.build();

		lastCommentResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
				commentCreationDTO,
				Comment.class
			);
	}

	@Then("The system returns the {int} response code to the user")
	public void theSystemReturnsTheResponseCodeToTheUser(int responseCode) {
		assertThat(lastCommentResponse.getStatusCode().value())
			.isEqualTo(responseCode);
	}

	@And("The system returns the comment class with content {string}")
	public void theSystemReturnsTheCommentClassWithIdAndContentSome(
		String commentContent
	) {
		Comment savedComment = lastCommentResponse.getBody();

		assertThat(savedComment).isNotNull();
		assertThat(savedComment.getContent()).isEqualTo(commentContent);
	}

	@Given("The post has a comment with content {string}")
	public void theCommentWithIdAndContentExistsInDatabase(
		String commentContent
	) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.postId(backgroundPost.getId())
			.userId(backgroundUser.getId())
			.build();

		restTemplate.postForEntity(
			createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
			commentCreationDTO,
			Comment.class
		);
	}

	@And("The post has other comment with content {string}")
	public void theOtherCommentWithIdAndContentExistsInDatabase(
		String commentContent
	) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.postId(backgroundPost.getId())
			.userId(backgroundUser.getId())
			.build();

		restTemplate.postForEntity(
			createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
			commentCreationDTO,
			Comment.class
		);
	}

	@When("User wants to search for all comments of the post")
	public void userWantsToSearchForAllCommentsOfThePost() {
		lastCommentsResponse =
			restTemplate.getForEntity(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_COMMENTS_URL + "/" + backgroundPost.getId()
				),
				Comment[].class
			);
	}

	@Then("The system returns a list of size {int}")
	public void theSystemReturnsAListOfSize(int commentsListSize) {
		Comment[] comments = lastCommentsResponse.getBody();
		assertThat(comments).hasSize(commentsListSize);
	}

	@And(
		"The list contains comment with content {string} and comment with content {string}"
	)
	public void theListContainsTwoSpecificComments(
		String commentContent1,
		String commentContent2
	) {
		Comment[] comments = lastCommentsResponse.getBody();
		assertThat(comments)
			.extracting("content")
			.containsExactlyInAnyOrder(commentContent1, commentContent2);
	}

	@And("The system returns {int} response code to the user")
	public void theSystemReturnsResponseCodeToTheUser(int responseCode) {
		assertThat(lastCommentsResponse.getStatusCode().value())
			.isEqualTo(responseCode);
	}

	@Given("The first post has a comment with content {string}")
	public void firstPostHasCommentWithContent(String commentContent) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.postId(backgroundPost.getId())
			.userId(backgroundUser.getId())
			.build();

		restTemplate.postForEntity(
			createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
			commentCreationDTO,
			Comment.class
		);
	}

	@And("There is a second post with content {string}")
	public void thereIsASecondPostWithContent(String postContent) {
		PostCreationDTO postCreationDTO = PostCreationDTO
			.builder()
			.content(postContent)
			.build();

		secondPost =
			restTemplate
				.postForEntity(
					createLocalURIWithGivenPortNumber(
						port,
						GET_ALL_POSTS_URL + "/user/" + backgroundUser.getId()
					),
					postCreationDTO,
					PostDTO.class
				)
				.getBody();
	}

	@And("The second post has a comment with content {string}")
	public void secondPostHasCommentWithContent(String commentContent) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.postId(secondPost.getId())
			.userId(backgroundUser.getId())
			.build();

		restTemplate.postForEntity(
			createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
			commentCreationDTO,
			Comment.class
		);
	}

	@When("User wants to search for all comments in the database")
	public void userWantsToSearchForAllCommentsInTheDatabase() {
		lastCommentsResponse =
			restTemplate.getForEntity(
				createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
				Comment[].class
			);
	}

	@Then("Returned list is of size {int}")
	public void returnedListIsOfSize(int commentsListSize) {
		Comment[] comments = lastCommentsResponse.getBody();
		assertThat(comments).hasSize(commentsListSize);
	}

	@And("The system returns {int} response code")
	public void theSystemReturnsResponseCode(int responseCode) {
		assertThat(lastCommentsResponse.getStatusCode().value())
			.isEqualTo(responseCode);
	}

	@After
	public void cleanUp() {
		List<User> users = Arrays
			.stream(
				Objects.requireNonNull(
					restTemplate.getForObject(
						createLocalURIWithGivenPortNumber(port, GET_ALL_USERS_URL),
						User[].class
					)
				)
			)
			.toList();

		users.forEach(user -> {
			restTemplate.delete(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_USERS_URL + "/" + user.getId()
				)
			);
		});
	}

	@Given("The post has the comment with content {string}")
	public void thePostHasTheCommentWithContent(String commentContent) {
		CommentCreationDTO commentCreationDTO = CommentCreationDTO
			.builder()
			.content(commentContent)
			.postId(backgroundPost.getId())
			.userId(backgroundUser.getId())
			.build();

		savedComment =
			restTemplate
				.postForEntity(
					createLocalURIWithGivenPortNumber(port, GET_ALL_COMMENTS_URL),
					commentCreationDTO,
					Comment.class
				)
				.getBody();
	}

	@When(
		"The user wants to delete the comment with content {string} from the post"
	)
	public void theUserWantsToDeleteTheCommentWithContentFromThePost(
		String commentContent
	) {
		assertThat(savedComment.getContent()).isEqualTo(commentContent);

		lastDeleteResponse =
			restTemplate.exchange(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_COMMENTS_URL + "/" + savedComment.getId()
				),
				HttpMethod.DELETE,
				null,
				Void.class
			);
	}

	@Then("The system returns code {int}")
	public void theSystemReturnsCode(int responseCode) {
		assertThat(lastDeleteResponse.getStatusCode().value())
			.isEqualTo(responseCode);
	}

	@Given("The post does not have any comments")
	public void thePostDoesNotHaveAnyComments() {
		Post savedPost = restTemplate
			.getForEntity(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_POSTS_URL + "/" + backgroundPost.getId()
				),
				Post.class
			)
			.getBody();

		assertThat(savedPost.getComments()).isEmpty();
	}

	@When(
		"User wants to delete not existing comment with random id {int} from the post"
	)
	public void userWantsToDeleteTheCommentWithIdFromThePost(int commentId) {
		try {
			lastDeleteResponse =
				restTemplate.exchange(
					createLocalURIWithGivenPortNumber(
						port,
						GET_ALL_COMMENTS_URL + "/" + commentId
					),
					HttpMethod.DELETE,
					null,
					Void.class
				);
		} catch (HttpClientErrorException exception) {
			lastDeleteResponse = new ResponseEntity<>(exception.getStatusCode());
		}
	}
}
