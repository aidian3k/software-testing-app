package project.eepw.softwaretestingcrud.application;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Validated
@Slf4j
class UserController {

	private final UserService userService;

	@GetMapping("")
	public Collection<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public User getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@GetMapping("/{email}")
	public User getUserByEmail(@PathVariable String email) {
		return userService.getUserByEmail(email);
	}

	@PutMapping("/{id}")
	public User updateUserById(User user) {
		return userService.updateUser(user);
	}

	@PostMapping("/create-user")
	public User createUser(@RequestBody User user) {
		return userService.createUser(user);
	}

	@DeleteMapping("/{id}")
	public void deleteUserById(@PathVariable Long id) {
		userService.deleteUserById(id);
	}
}
