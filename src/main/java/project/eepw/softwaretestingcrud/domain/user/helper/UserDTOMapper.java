package project.eepw.softwaretestingcrud.domain.user.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.eepw.softwaretestingcrud.domain.user.dto.UserDTO;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDTOMapper {

	public static UserDTO toDto(User user) {
		return UserDTO
			.builder()
			.id(user.getId())
			.name(user.getName())
			.surname(user.getSurname())
			.email(user.getEmail())
			.build();
	}

	public static User toModel(UserDTO userDTO) {
		return User
			.builder()
			.id(userDTO.id())
			.name(userDTO.name())
			.surname(userDTO.surname())
			.email(userDTO.email())
			.build();
	}
}
