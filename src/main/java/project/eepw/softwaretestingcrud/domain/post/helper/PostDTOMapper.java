package project.eepw.softwaretestingcrud.domain.post.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostDTOMapper {

	public static PostDTO toDto(Post post) {
		return PostDTO
			.builder()
			.id(post.getId())
			.content(post.getContent())
			.build();
	}
}
