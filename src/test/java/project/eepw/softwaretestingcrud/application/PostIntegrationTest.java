package project.eepw.softwaretestingcrud.application;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PostIntegrationTest {

	@Nested
	@DisplayName("Create posts tests")
	@Tag("POST")
	class CreatePostTests {

		@Test
		void shouldReturnCorrectPostWhenPostWasJustCreated() {
			// given
			User createUserDTO = sampleCreateUser();
			User user = makeUserCreationRequest(createUserDTO);
			Post createPostDTO = sampleCreatePost(user);

			// when
			Post post = makePostCreationRequest(createPostDTO, user.getId());

			// then
			Assertions.assertAll(
				() -> assertThat(post.getContent()).isEqualTo(createPostDTO.getContent()),
				() -> assertThat(post.getUser()).isEqualTo(createPostDTO.getUser())
			);

			// tear down
			makePostDeletionRequest(post.getId());
			makeUserDeletionRequest(user.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassNullValuesForMandatoryPostField() {
			// given
			User createUserDTO = sampleCreateUser();
			User user = makeUserCreationRequest(createUserDTO);
			Post createPost = sampleCreatePost(user)
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

			// tear down
			makeUserDeletionRequest(user.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassTooLongValueForPostContentField() {
			// given
			User createUserDTO = sampleCreateUser();
			User user = makeUserCreationRequest(createUserDTO);
			Post createPost = sampleCreatePost(user)
					.toBuilder()
					.content("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum ultricies placerat quam ut luctus. Etiam fringilla, enim vitae cursus laoreet, diam nisl consectetur nulla, eget tincidunt nunc turpis id orci. Sed feugiat rutrum purus quis luctus. Nulla malesuada posuere sapien, nec aliquam diam varius vel. Maecenas facilisis vel est consectetur tincidunt. In laoreet magna mauris, sit amet tempor nibh tempor eget. Nulla porttitor sodales lectus, a ultricies ipsum euismod blandit. Mauris ullamcorper turpis.")
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

			// tear down
			makeUserDeletionRequest(user.getId());
		}
		@Test
		void shouldCorrectlyAddMoreThanOnePostToOneUser() {
			// given
			User createUserDTO = sampleCreateUser();
			User user = makeUserCreationRequest(createUserDTO);
			Post firstPost = sampleCreatePost(user)
					.toBuilder()
					.content("Daaawideek")
					.build();

			Post secondPost = sampleCreatePost(user)
					.toBuilder()
					.content("James 1.25 zgłoś się")
					.build();

			// when
			Post firstCreatedPost = makePostCreationRequest(firstPost, user.getId());
			Post secondCreatedPost = makePostCreationRequest(secondPost, user.getId());

			// then
			Set<String> expectedPostContents = Set.of("Daaawideek", "James 1.25 zgłoś się");

			assertThat(
					Set.of(firstCreatedPost.getContent(), secondCreatedPost.getContent())
			)
					.hasSize(2)
					.containsExactlyInAnyOrderElementsOf(expectedPostContents);

			// tear down
			Stream
					.of(firstCreatedPost.getId(), secondCreatedPost.getId())
					.forEach(PostIntegrationTest.this::makePostDeletionRequest);

			makeUserDeletionRequest(user.getId());
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
	private static Post sampleCreatePost(User User) {
		return Post
			.builder()
			.content("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis et ornare sapien.")
			.user(User)
			.build();
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

	private Post makePostCreationRequest(Post createPost, Long userId) {
		return given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(createPost)
				.post(CREATE_POST_URL_WITHOUT_USER_ID + userId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.log()
				.body()
				.extract()
				.as(Post.class);
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
}
