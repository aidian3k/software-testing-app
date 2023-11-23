package project.eepw.softwaretestingcrud.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserCreationBehaviourTests {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();
	private ResponseEntity<User> lastUserResponse;

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

	@And("The system should return {int} response")
	public void theSystemShouldReturnResponse(int responseStatus) {
		assertThat(lastUserResponse.getStatusCode().value())
			.isEqualTo(responseStatus);
	}
}
