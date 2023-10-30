package project.eepw.softwaretestingcrud.application;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import project.eepw.softwaretestingcrud.SoftwareTestingCrudApplication;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
	classes = { SoftwareTestingCrudApplication.class }
)
class PostIntegrationTest {

	private User user;

	@BeforeEach
	public void setUp() {
		this.user = makeUserCreationRequest(sampleCreateUser());
	}

	@AfterEach
	public void tearDown() {
		makeUserDeletionRequest(user.getId());
	}

	@Nested
	@DisplayName("Create posts tests")
	@Tag("POST")
	class CreatePostTests {

		@Test
		void shouldReturnCorrectPostWhenPostWasJustCreated() {
			// given
			PostDTO createPostDTO = sampleCreatePost();

			// when
			PostDTO post = makePostCreationRequest(createPostDTO, user.getId());

			// then
			assertThat(post.getContent()).isEqualTo(createPostDTO.getContent());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassNullValuesForMandatoryPostField() {
			// given
			PostDTO createPost = sampleCreatePost().toBuilder().content(null).build();

			// when
			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(createPost)
				.post(CREATE_POST_URL_WITHOUT_USER_ID + user.getId())
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			// then
			int expectedErrorsSize = 1;

			assertThat(errorResponse.keySet())
				.hasSize(expectedErrorsSize)
				.containsExactlyInAnyOrderElementsOf(Set.of("content"));
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassTooLongValueForPostContentField() {
			// given
			int lengthOfWrongContent = 513;

			String wrongLengthContent = IntStream
				.range(0, lengthOfWrongContent)
				.mapToObj(element -> "a")
				.collect(Collectors.joining());

			PostDTO createPost = sampleCreatePost()
				.toBuilder()
				.content(wrongLengthContent)
				.build();

			// when
			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(createPost)
				.post(CREATE_POST_URL_WITHOUT_USER_ID + user.getId())
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			// then
			int expectedErrorsSize = 1;

			assertThat(errorResponse.keySet())
				.hasSize(expectedErrorsSize)
				.containsExactlyInAnyOrderElementsOf(Set.of("content"));
		}

		@Test
		void shouldCorrectlyAddMoreThanOnePostToOneUser() {
			// given
			PostDTO firstPost = sampleCreatePost()
				.toBuilder()
				.content("Daaawideek")
				.build();

			PostDTO secondPost = sampleCreatePost()
				.toBuilder()
				.content("James 1.25 zgłoś się")
				.build();

			// when
			PostDTO firstCreatedPost = makePostCreationRequest(
				firstPost,
				user.getId()
			);
			PostDTO secondCreatedPost = makePostCreationRequest(
				secondPost,
				user.getId()
			);

			// then
			Set<String> expectedPostContents = Set.of(
				"Daaawideek",
				"James 1.25 zgłoś się"
			);

			assertThat(
				Set.of(firstCreatedPost.getContent(), secondCreatedPost.getContent())
			)
				.hasSize(2)
				.containsExactlyInAnyOrderElementsOf(expectedPostContents);
		}
	}

	@Nested
	@DisplayName("Get posts test")
	@Tag("GET")
	class GetPostsTests {

		@Test
		void shouldCorrectlyGetPostAfterCreatingMultiplePosts() {
			// given
			List<String> postContents = List.of(
				"Lorem dawidsum",
				"Lorem jamesum",
				"Lorem adriansum",
				"Lorem cezarsum"
			);

			// when
			List<PostDTO> createdPosts = postContents
				.stream()
				.map(postContent -> {
					PostDTO createdPost = sampleCreatePost()
						.toBuilder()
						.content(String.valueOf(postContent))
						.build();

					return makePostCreationRequest(createdPost, user.getId());
				})
				.toList();

			List<PostDTO> posts = Arrays
				.stream(
					given()
						.get(GET_ALL_POSTS_URL)
						.then()
						.statusCode(HttpStatus.OK.value())
						.and()
						.extract()
						.as(PostDTO[].class)
				)
				.toList();

			// then
			int expectedSize = postContents.size();

			assertThat(posts)
				.hasSize(expectedSize)
				.hasAtLeastOneElementOfType(PostDTO.class)
				.extracting(PostDTO::getContent)
				.containsExactlyInAnyOrderElementsOf(postContents);
		}

		@Test
		void shouldReturnEmptyListWhenThereAreNoPostsInDatabase() {
			// when
			List<Post> posts = Arrays
				.stream(
					given()
						.get(GET_ALL_POSTS_URL)
						.then()
						.statusCode(HttpStatus.OK.value())
						.and()
						.extract()
						.as(Post[].class)
				)
				.toList();

			// then
			int expectedSize = 0;

			assertThat(posts).hasSize(expectedSize);
		}

		@Test
		void shouldCorrectlyFindPostsByIdWhenPostIsCreated() {
			// given
			PostDTO createPost = sampleCreatePost();

			// when
			PostDTO post = makePostCreationRequest(createPost, user.getId());
			Post getPost = given()
				.get(GET_ALL_POSTS_URL + "/" + post.getId())
				.then()
				.statusCode(HttpStatus.OK.value())
				.and()
				.extract()
				.as(Post.class);

			// then
			Assertions.assertAll(
				() -> assertThat(getPost.getId()).isEqualTo(post.getId()),
				() -> assertThat(getPost.getContent()).isEqualTo(post.getContent())
			);
		}
	}

	@Nested
	@DisplayName("Delete posts test")
	@Tag("DELETE")
	class DeletePostTests {

		@Test
		void shouldThrowAnExceptionWhenTryingToDeletePostWhichDoesNotExist() {
			// given
			int someRandomId = 1024;

			// when

			// then
			given()
				.delete(GET_ALL_POSTS_URL + "/" + someRandomId)
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
		}
	}

	@Nested
	@DisplayName("Update posts tests")
	@Tag("UPDATE")
	class UpdatePostTests {

		@Test
		void shouldUpdatePostWhenPostIsAlreadyCreated() {
			// given
			PostDTO postCreateRequest = sampleCreatePost();
			String changedContent = "James 1.25 zgłoś się";

			// when
			PostDTO createdPost = makePostCreationRequest(
				postCreateRequest,
				user.getId()
			);
			PostDTO modifiedPostRequest = createdPost
				.toBuilder()
				.content(changedContent)
				.build();

			PostDTO modifiedPost = makePostUpdateRequest(
				modifiedPostRequest,
				user.getId()
			);

			// then
			Assertions.assertAll(
				() -> assertThat(modifiedPost.getId()).isEqualTo(createdPost.getId()),
				() -> assertThat(modifiedPost.getContent()).isEqualTo(changedContent)
			);
		}
	}

	private static User sampleCreateUser() {
		return User
			.builder()
			.email("czarek@wp.pl")
			.name("Cezary")
			.surname("Skorupski")
			.password("some-random-password")
			.build();
	}

	private static PostDTO sampleCreatePost() {
		return PostDTO.builder().content("Some content").build();
	}

	private User makeUserCreationRequest(User createUser) {
		return given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(createUser)
			.post(CREATE_USER_URL)
			.then()
			.statusCode(HttpStatus.OK.value())
			.log()
			.body()
			.extract()
			.as(User.class);
	}

	private PostDTO makePostCreationRequest(PostDTO createPost, Long userId) {
		return given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(createPost)
			.post(CREATE_POST_URL_WITHOUT_USER_ID + userId)
			.then()
			.statusCode(HttpStatus.OK.value())
			.log()
			.body()
			.extract()
			.as(PostDTO.class);
	}

	private void makeUserDeletionRequest(Long userId) {
		given()
			.delete(GET_ALL_USERS_URL + "/" + userId)
			.then()
			.statusCode(HttpStatus.OK.value());
	}

	private void makePostDeletionRequest(Long postId) {
		given()
			.delete(GET_ALL_POSTS_URL + "/" + postId)
			.then()
			.statusCode(HttpStatus.OK.value());
	}

	private PostDTO makePostUpdateRequest(PostDTO updatedPost, Long userId) {
		return given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(updatedPost)
			.put(GET_ALL_POSTS_URL + "/user/" + userId)
			.then()
			.statusCode(HttpStatus.OK.value())
			.log()
			.body()
			.extract()
			.as(PostDTO.class);
	}
}
