package project.eepw.softwaretestingcrud.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;
import static project.eepw.softwaretestingcrud.cucumber.UserCreationBehaviourTests.CommonUserMockInformation.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserCreationBehaviourTests {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();
	private ResponseEntity<User> lastUserResponse;
	private ResponseEntity<User[]> lastUserArrayResponse;
	private ResponseEntity<Map<String, String>> lastBadRequestResponse;
	private ResponseEntity<Void> lastVoidResponse;

	@Given(
		"There exists in system the user with id {int}, name {string} and email {string}"
	)
	public void thereExistsInSystemTheUserWithIdNameAndEmail(
		int userId,
		String userName,
		String userEmail
	) {
		String samplePassword = "sample-password-here";
		String sampleSurname = "Nowosielski";

		User creationUser = User
			.builder()
			.name(userName)
			.email(userEmail)
			.id((long) userId)
			.surname(sampleSurname)
			.password(samplePassword)
			.build();

		lastUserResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				creationUser,
				User.class
			);
	}

	@When("User tries to get user with id {int}")
	public void userTriesToGetUserWithId(int userId) {
		lastUserResponse =
			restTemplate.getForEntity(
				createLocalURIWithGivenPortNumber(
					port,
					String.format(GET_ALL_USERS_URL + "/%d", userId)
				),
				User.class
			);
	}

	@Then(
		"The system should return the user with name {string} and email {string}"
	)
	public void theSystemShouldReturnTheUserWithNameAndEmail(
		String userName,
		String userEmail
	) {
		User foundUser = lastUserResponse.getBody();

		Assertions.assertAll(
			() -> assertThat(foundUser).isNotNull(),
			() -> assertThat(foundUser.getName()).isEqualTo(userName),
			() -> assertThat(foundUser.getEmail()).isEqualTo(userEmail)
		);
	}

	@Given(
		"In the system there are two added users with names {string} and {string}"
	)
	public void inTheSystemThereAreTwoAddedUsersWithNamesAnd(
		String firstUserName,
		String secondUserName
	) {
		User firstCreatedUser = User
			.builder()
			.name(firstUserName)
			.email(EMAIL)
			.surname(SURNAME)
			.password(PASSWORD)
			.build();
		User secondCreatedUser = firstCreatedUser
			.toBuilder()
			.name(secondUserName)
			.build();

		Stream
			.of(firstCreatedUser, secondCreatedUser)
			.forEach(user -> {
				lastUserResponse =
					restTemplate.postForEntity(
						createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
						user,
						User.class
					);
			});
	}

	@When("The user tries to find all users data")
	public void theUserTriesToFindAllUsersData() {
		lastUserArrayResponse =
			restTemplate.getForEntity(
				createLocalURIWithGivenPortNumber(port, GET_ALL_USERS_URL),
				User[].class
			);
	}

	@Then(
		"The system should return a collection of saved users with names {string} and {string}"
	)
	public void theSystemShouldReturnACollectionOfSavedUsersWithNamesAnd(
		String firstUserName,
		String secondUserName
	) {
		List<User> users = Arrays
			.stream(Objects.requireNonNull(lastUserArrayResponse.getBody()))
			.toList();
		List<String> userNames = users.stream().map(User::getName).toList();
		Assertions.assertAll(() ->
			assertThat(userNames)
				.containsExactlyInAnyOrderElementsOf(
					List.of(firstUserName, secondUserName)
				)
		);
	}

	@Given("In system exists a user with email {string}")
	public void inSystemExistsAUserWithEmail(String userEmail) {
		User createdUserWithEmail = createMockUser()
			.toBuilder()
			.email(userEmail)
			.build();

		lastUserResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				createdUserWithEmail,
				User.class
			);
	}

	@When("The user tries to find the user with email {string}")
	public void theUserTriesToFindTheUserWithEmail(String userEmail) {
		String emailUriWithParams = UriComponentsBuilder
			.fromUriString(
				createLocalURIWithGivenPortNumber(
					port,
					String.format(GET_ALL_USERS_URL + "/email")
				)
			)
			.queryParam("email", userEmail)
			.toUriString();
		lastUserResponse =
			restTemplate.getForEntity(emailUriWithParams, User.class);
	}

	@Then("The system should return the user with email {string}")
	public void theSystemShouldReturnTheUserWithEmail(String userEmail) {
		assertThat(lastUserResponse.getBody().getEmail()).isEqualTo(userEmail);
	}

	@Given("The user provides valid user data")
	public void theUserProvidesValidUserData() {}

	@When(
		"The user tries to add new user with name {string}, surname {string}, email {string} and password {string}"
	)
	public void theUserTriesToAddNewUserWithNameSurnameEmailAndPassword(
		String userName,
		String surname,
		String email,
		String password
	) {
		User createdUser = User
			.builder()
			.name(userName)
			.surname(surname)
			.email(email)
			.password(password)
			.build();

		lastUserResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				createdUser,
				User.class
			);
	}

	@Then(
		"The system should add new user with assigned name {string}, surname {string}, email {string}, password {string}"
	)
	public void theSystemShouldAddNewUserWithAssignedNameSurnameEmailPasswordAndIdId(
		String userName,
		String userSurname,
		String userEmail,
		String userPassword
	) {
		User userResponse = lastUserResponse.getBody();

		Assertions.assertAll(
			() -> assertThat(userResponse.getName()).isEqualTo(userName),
			() -> assertThat(userResponse.getSurname()).isEqualTo(userSurname),
			() -> assertThat(userResponse.getEmail()).isEqualTo(userEmail),
			() -> assertThat(userResponse.getPassword()).isEqualTo(userPassword)
		);
	}

	@Given("The system is ready to add new user")
	public void theSystemIsReadyToAddNewUser() {}

	@When("The user tries to add new user with wrong {string}")
	public void theUserTriesToAddNewUserWithWrong(String userEmail) {
		User creationUser = createMockUser().toBuilder().email(userEmail).build();

		try {
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				creationUser,
				getErrorObjectType()
			);
		} catch (HttpClientErrorException exception) {
			lastBadRequestResponse =
				new ResponseEntity<>(
					exception.getResponseBodyAs(getErrorObjectType()),
					exception.getStatusCode()
				);
		}
	}

	@Then(
		"The system should throw an exception with map with key {string} and value {string}"
	)
	public void theSystemShouldThrowAnExceptionWithMapWithKeyAndValue(
		String mapErrorKey,
		String mapErrorValue
	) {
		Map<String, String> errorMap = Objects.requireNonNull(
			lastBadRequestResponse.getBody()
		);
		assertThat(errorMap).containsEntry(mapErrorKey, mapErrorValue);
	}

	@When(
		"The user tries to add the user with the username which have length of {string} characters"
	)
	public void theUserTriesToAddTheUserWithTheUsernameWhichHaveLengthOfCharacters(
		String stringifyNumberOfCharacters
	) {
		int numberOfCharacters = Integer.parseInt(stringifyNumberOfCharacters);
		String wrongUserName = IntStream
			.range(0, numberOfCharacters)
			.mapToObj(element -> "a")
			.collect(Collectors.joining());
		User creationUser = createMockUser()
			.toBuilder()
			.name(wrongUserName)
			.build();

		try {
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				creationUser,
				getErrorObjectType()
			);
		} catch (HttpClientErrorException exception) {
			lastBadRequestResponse =
				new ResponseEntity<>(
					exception.getResponseBodyAs(getErrorObjectType()),
					exception.getStatusCode()
				);
		}
	}

	@Then(
		"The system should throw an exception with map with key {string} and value containing string {string}"
	)
	public void theSystemShouldThrowAnExceptionWithMapWithKeyAndValueContainingString(
		String keyError,
		String errorMessage
	) {
		Map<String, String> errorMap = Objects.requireNonNull(
			lastBadRequestResponse.getBody()
		);

		assertThat(errorMap).containsEntry(keyError, errorMessage);
	}

	@Given("There is added user with name {string}")
	public void thereIsAddedUserWithId(String userName) {
		User creationUser = createMockUser().toBuilder().name(userName).build();

		lastUserResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				creationUser,
				User.class
			);
	}

	@When("User tries to update the user with name adrian to name {string}")
	public void userTriesToUpdateTheUserWithIdWithName(String updatedUserName) {
		long updateUserId = Objects
			.requireNonNull(lastUserResponse.getBody())
			.getId();

		User updatedUser = createMockUser()
			.toBuilder()
			.id(updateUserId)
			.name(updatedUserName)
			.build();
		HttpEntity<User> httpEntity = new HttpEntity<>(updatedUser);

		lastUserResponse =
			restTemplate.exchange(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_USERS_URL + "/" + updateUserId
				),
				HttpMethod.PUT,
				httpEntity,
				User.class
			);
	}

	@Then("The system updates user's the username to {string}")
	public void theSystemUpdatesTheUsernameWithIdTo(String updatedUserName) {
		ResponseEntity<User> updatedUserResponse = restTemplate.getForEntity(
			createLocalURIWithGivenPortNumber(
				port,
				GET_ALL_USERS_URL + "/" + lastUserResponse.getBody().getId()
			),
			User.class
		);
		User updatedUserBody = updatedUserResponse.getBody();

		assertThat(updatedUserBody.getName()).isEqualTo(updatedUserName);
		assertThat(updatedUserResponse.getStatusCode().value())
			.isEqualTo(HttpStatus.OK.value());
	}

	@Given("There is added user")
	public void thereIsAddedUserWithId() {
		User creationUser = createMockUser().toBuilder().build();

		lastUserResponse =
			restTemplate.postForEntity(
				createLocalURIWithGivenPortNumber(port, CREATE_USER_URL),
				creationUser,
				User.class
			);
	}

	@When("The user tries to delete existing the user in database")
	public void theUserTriesToDeleteTheUserWithId() {
		lastVoidResponse =
			restTemplate.exchange(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_USERS_URL + "/" + lastUserResponse.getBody().getId()
				),
				HttpMethod.DELETE,
				null,
				Void.class
			);
	}

	@Then("The system should return OK response status code")
	public void theSystemCorrectlyDeletesTheUserWithIdFromDatabase() {
		assertThat(lastVoidResponse.getStatusCode().value())
			.isEqualTo(HttpStatus.OK.value());
	}

	@SuppressWarnings("unchecked")
	private Class<Map<String, String>> getErrorObjectType() {
		return (Class<Map<String, String>>) ((Class) Map.class);
	}

	@Given("The system database does not have user with id {int}")
	public void theSystemDatabaseDoesNotHaveUserWithId(int userId) {}

	@When("The user tries to delete a user with the invalid id {string}")
	public void theUserTriesToDeleteAUserWithTheInvalidId(
		String stringifyUserId
	) {
		try {
			restTemplate.exchange(
				createLocalURIWithGivenPortNumber(
					port,
					GET_ALL_USERS_URL + "/" + stringifyUserId
				),
				HttpMethod.DELETE,
				null,
				Void.class
			);
		} catch (HttpClientErrorException exception) {
			lastVoidResponse = new ResponseEntity<>(exception.getStatusCode());
		}
	}

	@Then("The system should return an error indicating the user was not found")
	public void theSystemShouldReturnAnErrorIndicatingTheUserWasNotFound() {
		assertThat(lastVoidResponse.getStatusCode().value())
			.isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	static class CommonUserMockInformation {

		static User createMockUser() {
			return User
				.builder()
				.name(NAME)
				.password(PASSWORD)
				.email(EMAIL)
				.surname(SURNAME)
				.build();
		}

		static final String PASSWORD = "sample-password";
		static final String SURNAME = "sample-surname";
		static final String NAME = "sample-name";
		static final String EMAIL = "adrian@wp.pl";
	}
}
