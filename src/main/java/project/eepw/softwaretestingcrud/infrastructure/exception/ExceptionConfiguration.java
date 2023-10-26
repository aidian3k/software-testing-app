package project.eepw.softwaretestingcrud.infrastructure.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ExceptionConfiguration {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodNotValidException(
		MethodArgumentNotValidException methodArgumentNotValidException
	) {
		Map<String, String> errors = methodArgumentNotValidException
			.getAllErrors()
			.stream()
			.collect(
				Collectors.toMap(
					ObjectError::getObjectName,
					Objects.requireNonNull(
						DefaultMessageSourceResolvable::getDefaultMessage
					)
				)
			);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolationException(
		ConstraintViolationException constraintViolationException
	) {
		Map<String, String> errors = constraintViolationException
			.getConstraintViolations()
			.stream()
			.collect(
				Collectors.toMap(
					error -> error.getPropertyPath().toString(),
					ConstraintViolation::getMessage
				)
			);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
