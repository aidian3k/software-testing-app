package project.eepw.softwaretestingcrud.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import static io.restassured.RestAssured.given;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.CREATE_USER_URL;
import static project.eepw.softwaretestingcrud.IntegrationTestConstants.GET_ALL_USERS_URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFixtures {

	public static User makeUserCreationRequest(User createUser) {
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

	public static void makeUserDeletionRequest(Long userId) {
		given()
			.delete(GET_ALL_USERS_URL + "/" + userId)
			.then()
			.statusCode(HttpStatus.OK.value());
	}
}
