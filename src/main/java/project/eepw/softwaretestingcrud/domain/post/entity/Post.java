package project.eepw.softwaretestingcrud.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
import lombok.ToString;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@Entity(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
	@SequenceGenerator(name = "posts_seq", allocationSize = 1)
	private Long id;

	@NotNull
	@Size(max = 512)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY,
		cascade = CascadeType.ALL
	)
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Builder.Default
	private Set<Comment> comments = new HashSet<>();
}
