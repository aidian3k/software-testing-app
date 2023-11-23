package project.eepw.softwaretestingcrud.domain.comment.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
public class CommentCreationDTO {

	@Positive
	private Long userId;

	@Positive
	private Long postId;

	@Size(max = 1024)
	private String content;
}
