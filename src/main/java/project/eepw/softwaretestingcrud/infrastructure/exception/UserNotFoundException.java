package project.eepw.softwaretestingcrud.infrastructure.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String message) {
		super(message);
	}
}
