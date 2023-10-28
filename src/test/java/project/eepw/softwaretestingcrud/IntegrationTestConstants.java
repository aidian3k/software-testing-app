package project.eepw.softwaretestingcrud;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestConstants {

	// user endpoints
	public static final String GET_ALL_USERS_URL = "api/user";
	public static final String CREATE_USER_URL =
		GET_ALL_USERS_URL + "/create-user";

	// post endpoints
	public static final String GET_ALL_POSTS_URL = "api/post";
	public static final String CREATE_POST_URL_WITHOUT_USER_ID =
			GET_ALL_POSTS_URL + "/user/";

	// post endpoints
	public static final String GET_ALL_POSTS = "api/post";
}
