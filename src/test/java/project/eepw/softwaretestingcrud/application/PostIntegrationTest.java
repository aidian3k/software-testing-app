package project.eepw.softwaretestingcrud.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import project.eepw.softwaretestingcrud.SoftwareTestingCrudApplication;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.helpers.UserFixtures;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.CREATE_POST_URL_WITHOUT_USER_ID;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.GET_ALL_POSTS_URL;
import static project.eepw.softwaretestingcrud.domain.factory.PostFactory.sampleCreatePost;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
	classes = { SoftwareTestingCrudApplication.class }
)
class PostIntegrationTest {

	private User user;

	@BeforeEach
	void setUp() {
		this.user = UserFixtures.makeUserCreationRequest(sampleCreateUser());
	}

	@AfterEach
	void tearDown() {
		UserFixtures.makeUserDeletionRequest(user.getId());
	}

	@Nested
	@DisplayName("Create posts tests")
	@Tag("POST")
	class CreatePostTests {

		@Test
		void shouldReturnCorrectPostWhenPostWasJustCreated() {
			// given
			PostCreationDTO createPostDTO = sampleCreatePost();

			// when
			PostDTO post = makePostCreationRequest(createPostDTO, user.getId());

			// then
			assertThat(post.getContent()).isEqualTo(createPostDTO.getContent());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassNullValuesForMandatoryPostField() {
			// given
			PostCreationDTO createPost = sampleCreatePost()
				.toBuilder()
				.content(null)
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
		void shouldThrowAnExceptionWhenUserPassTooLongValueForPostContentField() {
			// given
			int lengthOfWrongContent = 513;

			String wrongLengthContent = IntStream
				.range(0, lengthOfWrongContent)
				.mapToObj(element -> "a")
				.collect(Collectors.joining());

			PostCreationDTO createPost = sampleCreatePost()
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
			PostCreationDTO firstPost = sampleCreatePost()
				.toBuilder()
				.content("Daaawideek")
				.build();

			PostCreationDTO secondPost = sampleCreatePost()
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

		@ParameterizedTest
		@MethodSource(value = "makeValidCreationPosts")
		void shouldCorrectlyCreateNewPostWhenCreationPostIsValid(
			PostCreationDTO creationDTO
		) {
			// when
			PostDTO createdPost = makePostCreationRequest(creationDTO, user.getId());

			// then
			Assertions.assertAll(
				() ->
					assertThat(createdPost.getContent())
						.isEqualTo(creationDTO.getContent()),
				() -> assertThat(createdPost.getId()).isNotNull()
			);
		}

		private static Stream<Arguments> makeValidCreationPosts() {
			String borderContentValue = IntStream
				.range(0, 512)
				.mapToObj(element -> "a")
				.collect(Collectors.joining());
			String longerContent = IntStream
				.range(0, 256)
				.mapToObj(element -> "b")
				.collect(Collectors.joining());

			return Stream
				.<Arguments>builder()
				.add(Arguments.of(PostCreationDTO.builder().content("").build()))
				.add(
					Arguments.of(
						PostCreationDTO.builder().content("some-new-content").build()
					)
				)
				.add(
					Arguments.of(
						PostCreationDTO.builder().content(borderContentValue).build()
					)
				)
				.add(
					Arguments.of(PostCreationDTO.builder().content(longerContent).build())
				)
				.build();
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
					PostCreationDTO createdPost = sampleCreatePost()
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
				.containsExactlyInAnyOrderElementsOf(createdPosts);
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
			PostCreationDTO createPost = sampleCreatePost();

			// when
			PostDTO post = makePostCreationRequest(createPost, user.getId());
			PostDTO getPost = given()
				.get(GET_ALL_POSTS_URL + "/" + post.getId())
				.then()
				.statusCode(HttpStatus.OK.value())
				.and()
				.extract()
				.as(PostDTO.class);

			// then
			Assertions.assertAll(
				() -> assertThat(getPost.getId()).isEqualTo(post.getId()),
				() -> assertThat(getPost.getContent()).isEqualTo(post.getContent())
			);
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassesPostIdThatDoesNotExist() {
			// given
			long wrongPostId = 1337L;

			// when
			given()
				.get(GET_ALL_POSTS_URL + "/" + wrongPostId)
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
		}
	}

	@Nested
	@DisplayName("Delete posts test")
	@Tag("DELETE")
	class DeletePostTests {

		@Test
		void shouldDeletePostWhenPostIsCreatedProperly() {
			// given
			PostCreationDTO createPostDTO = sampleCreatePost();

			// when
			PostDTO post = makePostCreationRequest(createPostDTO, user.getId());
			makePostDeletionRequest(post.getId());

			// then
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.delete(GET_ALL_POSTS_URL + "/" + post.getId())
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
		}

		@Test
		void shouldDeleteMoreThanOnePostWhenPostsAreCreatedProperly() {
			// given
			PostCreationDTO firstPost = sampleCreatePost()
				.toBuilder()
				.content("Daaawideek")
				.build();

			PostCreationDTO secondPost = sampleCreatePost()
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
			makePostDeletionRequest(firstCreatedPost.getId());
			makePostDeletionRequest(secondCreatedPost.getId());

			// then
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.delete(GET_ALL_POSTS_URL + "/" + firstCreatedPost.getId())
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.delete(GET_ALL_POSTS_URL + "/" + secondCreatedPost.getId())
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
		}

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
		void shouldThrowAnExceptionWhenAttemptingToUpdatePostThatDoesNotExist() {
			// given
			Long wrongUpdateId = 1024L;

			// when
			PostDTO postDTO = PostDTO
				.builder()
				.id(wrongUpdateId)
				.content("Some random content")
				.build();

			given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(postDTO)
				.put(GET_ALL_POSTS_URL + "/user/" + user.getId())
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
		}

		@Test
		void shouldThrowAnExceptionWhenTryingToUpdateWithWrongData() {
			// given
			int minimumNumberOfCharacters = 0;
			int maximumNumberOfCharacters = 513;
			String wrongSizeContent = IntStream
				.range(minimumNumberOfCharacters, maximumNumberOfCharacters)
				.mapToObj(String::valueOf)
				.collect(Collectors.joining(""));
			PostCreationDTO creationDTO = PostCreationDTO
				.builder()
				.content("Good content")
				.build();

			// when
			PostDTO createdPost = makePostCreationRequest(creationDTO, user.getId());
			PostDTO modifiedPost = createdPost
				.toBuilder()
				.content(wrongSizeContent)
				.build();

			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(modifiedPost)
				.post(CREATE_POST_URL_WITHOUT_USER_ID + user.getId())
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			// then
			int expectedErrorsSize = 1;
			Set<String> expectedErrorKeys = Set.of("content");

			assertThat(errorResponse.keySet())
				.hasSize(expectedErrorsSize)
				.containsExactlyInAnyOrderElementsOf(expectedErrorKeys);
		}

		@Test
		void shouldUpdatePostWhenPostIsAlreadyCreated() {
			// given
			PostCreationDTO postCreateRequest = sampleCreatePost();
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

	private PostDTO makePostCreationRequest(
		PostCreationDTO createPost,
		Long userId
	) {
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
