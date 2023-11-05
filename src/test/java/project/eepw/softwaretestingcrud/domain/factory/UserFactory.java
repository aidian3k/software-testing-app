package project.eepw.softwaretestingcrud.domain.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserFactory {

	public static User makeUser() {
		return User
			.builder()
			.id(1L)
			.name("John")
			.email("john@example.com")
			.surname("Doe")
			.password("password")
			.posts(Collections.emptySet())
			.build();
	}
}
