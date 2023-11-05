package project.eepw.softwaretestingcrud.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import project.eepw.softwaretestingcrud.domain.factory.UserFactory;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.helpers.UserFixtures;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.CREATE_USER_URL;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.GET_ALL_USERS_URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserIntegrationTest {

	@Nested
	@DisplayName("Create users tests")
	@Tag("POST")
	class CreateUserTests {

		@Test
		void shouldReturnCorrectUserWhenUserWasJustCreated() {
			// given
			User createUserDTO = UserFactory.makeUser();

			// when
			User user = UserFixtures.makeUserCreationRequest(createUserDTO);

			// then
			Assertions.assertAll(
				() -> assertThat(user.getEmail()).isEqualTo(createUserDTO.getEmail()),
				() ->
					assertThat(user.getSurname()).isEqualTo(createUserDTO.getSurname()),
				() ->
					assertThat(user.getPassword()).isEqualTo(createUserDTO.getPassword())
			);

			// tear down
			UserFixtures.makeUserDeletionRequest(user.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenUserPassNullValuesForMandatoryFields() {
			// given
			User createUser = UserFactory
				.makeUser()
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
			User createUser = UserFactory
				.makeUser()
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
			User firstUser = UserFactory.makeUser();
			User secondUser = UserFactory
				.makeUser()
				.toBuilder()
				.name("Dawidek")
				.surname("Skorup")
				.build();

			// when
			User firstCreatedUser = UserFixtures.makeUserCreationRequest(firstUser);
			User secondCreatedUser = UserFixtures.makeUserCreationRequest(secondUser);

			// then
			Set<String> expectedUserNames = Set.of("Dawidek", "John");

			assertThat(
				Set.of(firstCreatedUser.getName(), secondCreatedUser.getName())
			)
				.hasSize(2)
				.containsExactlyInAnyOrderElementsOf(expectedUserNames);

			// tear down
			Stream
				.of(firstCreatedUser.getId(), secondCreatedUser.getId())
				.forEach(UserFixtures::makeUserDeletionRequest);
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
					User createdUser = UserFactory
						.makeUser()
						.toBuilder()
						.name(String.valueOf(userName))
						.build();

					return UserFixtures.makeUserCreationRequest(createdUser);
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
				UserFixtures.makeUserDeletionRequest(createdUser.getId())
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
			User createUser = UserFactory.makeUser();

			// when
			User user = UserFixtures.makeUserCreationRequest(createUser);
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
			UserFixtures.makeUserDeletionRequest(user.getId());
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
			User createUser = UserFactory.makeUser();

			// when
			User user = UserFixtures.makeUserCreationRequest(createUser);
			UserFixtures.makeUserDeletionRequest(user.getId());

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
			User userCreateRequest = UserFactory.makeUser();
			String changedName = "Kajtek";
			String changedPassword = "some-updated-password";

			// when
			User createdUser = UserFixtures.makeUserCreationRequest(
				userCreateRequest
			);
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
			UserFixtures.makeUserDeletionRequest(modifiedUser.getId());
		}

		@Test
		void shouldThrowAnExceptionWhenWantingToChangeUserWithWrongData() {
			// given
			User userCreateRequest = UserFactory.makeUser();

			// when
			User createdUser = UserFixtures.makeUserCreationRequest(
				userCreateRequest
			);
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
			UserFixtures.makeUserDeletionRequest(createdUser.getId());
		}
	}
}
