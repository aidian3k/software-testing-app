package project.eepw.softwaretestingcrud.domain.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@Entity(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
	private Long id;

	@NotNull
	@Size(max = 512)
	private String content;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	private Set<Comment> comments = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
}
