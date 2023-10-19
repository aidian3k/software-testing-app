package project.eepw.softwaretestingcrud.domain.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

@Entity(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Comment {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "comments_seq"
	)
	private Long id;

	@NotNull
	@Size(max = 512)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	private User user;
}
