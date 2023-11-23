package project.eepw.softwaretestingcrud.domain.comment.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentDTO;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;
import project.eepw.softwaretestingcrud.domain.post.helper.PostDTOMapper;
import project.eepw.softwaretestingcrud.domain.user.helper.UserDTOMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentDTOMapper {

	public static CommentDTO toDto(Comment comment) {
		return CommentDTO
			.builder()
			.userDTO(UserDTOMapper.toDto(comment.getAuthor()))
			.postDTO(PostDTOMapper.toDto(comment.getPost()))
			.content(comment.getContent())
			.build();
	}
}
