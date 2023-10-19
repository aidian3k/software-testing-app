package project.eepw.softwaretestingcrud.domain.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
	private Long id;

	@NotNull
	@Size(max = 255)
	private String name;

	@Email
	@NotNull
	@Size(min = 2, max = 6)
	private String email;

	@NotNull
	@Size(max = 255)
	private String surname;

	@NotNull
	@Size(max = 255)
	private String password;

	@OneToMany(
		mappedBy = "user",
		fetch = FetchType.LAZY,
		cascade = CascadeType.ALL
	)
	@EqualsAndHashCode.Exclude
	@Builder.Default
	private Set<Post> posts = new HashSet<>();
}
