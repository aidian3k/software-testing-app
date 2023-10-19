package project.eepw.softwaretestingcrud.domain.user.data;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	public User getUserById(Long userId) {
		return userRepository
			.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("User has not been found"));
	}

	public Collection<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserByEmail(String email) {
		return userRepository
			.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User has not been found!"));
	}

	public User createUser(User user) {
		return userRepository.save(user);
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}

	public void deleteUserById(Long userId) {
		userRepository.delete(getUserById(userId));
	}
}
