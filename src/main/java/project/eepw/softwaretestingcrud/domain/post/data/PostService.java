package project.eepw.softwaretestingcrud.domain.post.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.PostNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final PostRepository postRepository;
	private final UserService userService;

	public PostDTO getPostDTOById(Long postId) {
		return postRepository
			.findById(postId)
			.map(post -> new PostDTO(post.getId(), post.getContent()))
			.orElseThrow(() -> new PostNotFoundException("Post has not been found!"));
	}

	public Collection<PostDTO> getAllUserPosts(Long userId) {
		return userService
			.getUserById(userId)
			.getPosts()
			.stream()
			.map(userPost -> new PostDTO(userPost.getId(), userPost.getContent()))
			.collect(Collectors.toSet());
	}

	public Collection<PostDTO> getAllPosts() {
		return postRepository
			.findAll()
			.stream()
			.map(post -> new PostDTO(post.getId(), post.getContent()))
			.collect(Collectors.toSet());
	}

	public PostDTO createPost(
		PostCreationDTO postCreationDTO,
		Long userId
	) {
		User user = userService.getUserById(userId);
		Post post = Post
			.builder()
			.user(user)
			.content(postCreationDTO.getContent())
			.build();

		Post createdPost = postRepository.save(post);
		user.getPosts().add(createdPost);
		userService.updateUser(user);

		return PostDTO
			.builder()
			.id(createdPost.getId())
			.content(createdPost.getContent())
			.build();
	}

	public PostDTO updatePost(PostDTO modifiedPost, Long userId) {
		User foundUser = userService.getUserById(userId);
		foundUser
			.getPosts()
			.stream()
			.filter(post -> modifiedPost.getId().equals(post.getId()))
			.findAny()
			.ifPresent(post -> foundUser.getPosts().remove(post));
		Post updatedPost = Post
			.builder()
			.id(modifiedPost.getId())
			.content(modifiedPost.getContent())
			.user(foundUser)
			.build();
		foundUser.getPosts().add(updatedPost);

		userService.updateUser(foundUser);

		return modifiedPost;
	}

	public void deletePostById(Long postId) {
		Post postToDelete = getPostById(postId);
		User user = userService.getUserById(postToDelete.getUser().getId());
		user.getPosts().remove(postToDelete);
		userService.updateUser(user);

		postRepository.delete(postToDelete);
	}

	private Post getPostById(Long postId) {
		return postRepository
			.findById(postId)
			.orElseThrow(() -> new PostNotFoundException("Post has not been found!"));
	}
}
