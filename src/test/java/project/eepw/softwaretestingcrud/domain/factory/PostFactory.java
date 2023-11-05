package project.eepw.softwaretestingcrud.domain.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostFactory {

	public static Post makePost() {
		return Post
			.builder()
			.id(1L)
			.content("Some random post content")
			.user(UserFactory.makeUser())
			.build();
	}

	public static PostCreationDTO sampleCreatePost() {
		return PostCreationDTO.builder().content("Some content").build();
	}
}
