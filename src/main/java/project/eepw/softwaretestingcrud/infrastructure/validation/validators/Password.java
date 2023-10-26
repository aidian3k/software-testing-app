package project.eepw.softwaretestingcrud.infrastructure.validation.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Password.PasswordValidator.class)
public @interface Password {
	String message() default "Password does not meet our requirements";

	Class<?>[] groups() default {};

	Class<?>[] payload() default {};

	class PasswordValidator implements ConstraintValidator<Password, String> {

		private String message;

		@Override
		public void initialize(Password constraintAnnotation) {
			ConstraintValidator.super.initialize(constraintAnnotation);
			this.message = constraintAnnotation.message();
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			if (value == null || !value.matches(".{8,}")) {
				context
					.buildConstraintViolationWithTemplate(message)
					.addPropertyNode("password");
				return false;
			}

			return true;
		}
	}
}
