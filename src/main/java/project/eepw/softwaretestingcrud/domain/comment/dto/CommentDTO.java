package project.eepw.softwaretestingcrud.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.user.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDTO {

	private UserDTO userDTO;
	private String content;
	private PostDTO postDTO;
}
