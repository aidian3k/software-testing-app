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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserIntegrationTest {

	@Nested
	@DisplayName("Create users tests")
	@Tag("POST")
	class CreateUserTests {

		@Test
		void shouldReturnCorrectUserWhenUserWasJustCreated() {
			// given
			User createUserDTO = sampleCreateUser();

			// when
			User user = makeUserCreationRequest(createUserDTO);

			// then
			Assertions.assertAll(
				() -> assertThat(user.getEmail()).isEqualTo(createUserDTO.getEmail()),
				() ->
					assertThat(user.getSurname()).isEqualTo(createUserDTO.getSurname()),
				() ->
					assertThat(user.getPassword()).isEqualTo(createUserDTO.getPassword())
			);

			// tear down
			makeUserDeletionRequest(user.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassNullValuesForMandatoryFields() {
			// given
			User createUser = sampleCreateUser()
				.toBuilder()
				.name(null)
				.email(null)
				.build();

			// when
			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(createUser)
				.post(CREATE_USER_URL)
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			// then
			int expectedErrorsSize = 2;

			assertThat(errorResponse.keySet())
				.hasSize(expectedErrorsSize)
				.containsExactlyInAnyOrderElementsOf(Set.of("name", "email"));
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassesWrongRegexForPasswordAndEmail() {
			// given
			User createUser = sampleCreateUser()
				.toBuilder()
				.email("some-email.pl")
				.password("short")
				.build();

			// when
			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(createUser)
				.post(CREATE_USER_URL)
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			// then
			assertThat(errorResponse.keySet())
				.hasSize(2)
				.containsExactlyInAnyOrderElementsOf(Set.of("password", "email"));
		}

		@Test
		void shouldCorrectlyAddMoreThanOneUser() {
			// given
			User firstUser = sampleCreateUser();
			User secondUser = sampleCreateUser()
				.toBuilder()
				.name("Dawidek")
				.surname("Skorup")
				.build();

			// when
			User firstCreatedUser = makeUserCreationRequest(firstUser);
			User secondCreatedUser = makeUserCreationRequest(secondUser);

			// then
			Set<String> expectedUserNames = Set.of("Dawidek", "Adrian");

			assertThat(
				Set.of(firstCreatedUser.getName(), secondCreatedUser.getName())
			)
				.hasSize(2)
				.containsExactlyInAnyOrderElementsOf(expectedUserNames);

			// tear down
			Stream
				.of(firstCreatedUser.getId(), secondCreatedUser.getId())
				.forEach(UserIntegrationTest.this::makeUserDeletionRequest);
		}
	}

	@Nested
	@DisplayName("Get users test")
	@Tag("GET")
	class GetUsersTests {

		@Test
		void shouldCorrectlyGetUserAfterCreatingMultipleUsers() {
			// given
			List<Character> userNames =
				"abcdef".chars().mapToObj(i -> (char) i).toList();

			// when
			List<User> createdUsers = userNames
				.stream()
				.map(userName -> {
					User createdUser = sampleCreateUser()
						.toBuilder()
						.name(String.valueOf(userName))
						.build();

					return makeUserCreationRequest(createdUser);
				})
				.toList();

			List<User> users = Arrays
				.stream(
					given()
						.get(GET_ALL_USERS_URL)
						.then()
						.statusCode(HttpStatus.OK.value())
						.and()
						.extract()
						.as(User[].class)
				)
				.toList();

			// then
			int expectedSize = userNames.size();

			assertThat(users)
				.hasSize(expectedSize)
				.hasAtLeastOneElementOfType(User.class)
				.containsExactlyInAnyOrderElementsOf(createdUsers);

			// tear down
			createdUsers.forEach(createdUser ->
				makeUserDeletionRequest(createdUser.getId())
			);
		}

		@Test
		void shouldReturnEmptyListWhenThereAreNoUsersInDatabase() {
			// when
			List<User> users = Arrays
				.stream(
					given()
						.get(GET_ALL_USERS_URL)
						.then()
						.statusCode(HttpStatus.OK.value())
						.and()
						.extract()
						.as(User[].class)
				)
				.toList();

			// then
			int expectedSize = 0;

			assertThat(users).hasSize(expectedSize);
		}

		@Test
		void shouldCorrectlyFindUsersByIdWhenUserIsCreated() {
			// given
			User createUser = sampleCreateUser();

			// when
			User user = makeUserCreationRequest(createUser);
			User getUser = given()
				.get(GET_ALL_USERS_URL + "/" + user.getId())
				.then()
				.statusCode(HttpStatus.OK.value())
				.and()
				.extract()
				.as(User.class);

			// then
			Assertions.assertAll(
				() -> assertThat(getUser.getEmail()).isEqualTo(user.getEmail()),
				() -> assertThat(getUser.getSurname()).isEqualTo(user.getSurname()),
				() -> assertThat(getUser.getPassword()).isEqualTo(user.getPassword())
			);

			// tear down
			makeUserDeletionRequest(user.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenUserWantsToGetUserWithIdThatDoesNotExist() {
			// given
			int userIdThatNotExist = 1337;

			// when
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.get(GET_ALL_USERS_URL + "/" + userIdThatNotExist)
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
		}
	}

	@Nested
	@DisplayName("Delete users test")
	@Tag("DELETE")
	class DeleteUserTests {

		@Test
		void shouldThrowAnExceptionWhenTryingToDeleteUserWhichDoesNotExist() {
			// given
			int someRandomId = 1024;

			// when

			// then
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.delete(GET_ALL_USERS_URL + "/" + someRandomId)
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
		}

		@Test
		void shouldDeleteUserWhenUserIsCreatedProperly() {
			// given
			User createUser = sampleCreateUser();

			// when
			User user = makeUserCreationRequest(createUser);
			makeUserDeletionRequest(user.getId());

			// then
			assertThatNoException()
				.isThrownBy(() ->
					given()
						.delete(GET_ALL_USERS_URL + "/" + user.getId())
						.then()
						.statusCode(HttpStatus.NOT_FOUND.value())
				);
		}
	}

	@Nested
	@DisplayName("Update user tests")
	@Tag("UPDATE")
	class UpdateUserTests {

		@Test
		void shouldUpdateUserWhenUserIsAlreadyCreated() {
			// given
			User userCreateRequest = sampleCreateUser();
			String changedName = "Kajtek";
			String changedPassword = "some-updated-password";

			// when
			User createdUser = makeUserCreationRequest(userCreateRequest);
			User modifiedUserRequest = createdUser
				.toBuilder()
				.name(changedName)
				.password(changedPassword)
				.build();

			User modifiedUser = given()
				.body(modifiedUserRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.put(GET_ALL_USERS_URL + "/" + createdUser.getId())
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(User.class);

			// then
			Assertions.assertAll(
				() -> assertThat(modifiedUser.getId()).isEqualTo(createdUser.getId()),
				() ->
					assertThat(modifiedUser.getSurname())
						.isEqualTo(createdUser.getSurname()),
				() ->
					assertThat(modifiedUser.getEmail()).isEqualTo(createdUser.getEmail())
			);

			Assertions.assertAll(
				() -> assertThat(modifiedUser.getName()).isEqualTo(changedName),
				() -> assertThat(modifiedUser.getPassword()).isEqualTo(changedPassword)
			);

			// tear down
			makeUserDeletionRequest(modifiedUser.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenWantingToChangeUserWithWrongData() {
			// given
			User userCreateRequest = sampleCreateUser();

			// when
			User createdUser = makeUserCreationRequest(userCreateRequest);
			User modifiedUserRequest = createdUser.toBuilder().name(null).build();

			@SuppressWarnings("unchecked")
			Map<String, String> errorResponse = (Map<String, String>) given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(modifiedUserRequest)
				.put(GET_ALL_USERS_URL + "/" + createdUser.getId())
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(Map.class);

			int expectedErrorsSize = 1;

			assertThat(errorResponse.keySet())
				.hasSize(expectedErrorsSize)
				.containsExactlyInAnyOrderElementsOf(Set.of("name"));

			// tear down
			makeUserDeletionRequest(createdUser.getId());
		}
	}

	private static User sampleCreateUser() {
		return User
			.builder()
			.email("adrian@wp.pl")
			.name("Adrian")
			.surname("Nowosielski")
			.password("some-random-password")
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

	private void makeUserDeletionRequest(Long userId) {
		given()
			.delete(GET_ALL_USERS_URL + "/" + userId)
			.then()
			.statusCode(HttpStatus.OK.value());
	}
}
