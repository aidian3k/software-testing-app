package project.eepw.softwaretestingcrud.domain.post.data;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.PostNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final PostRepository postRepository;
	private final UserService userService;

	public Post getPostById(Long postId) {
		return postRepository
			.findById(postId)
			.orElseThrow(() -> new PostNotFoundException("Post has not been found!"));
	}

	public Collection<Post> getAllUserPosts(Long userId) {
		return userService.getUserById(userId).getPosts();
	}

	public Collection<Post> getAllPosts() {
		return postRepository.findAll();
	}

	public Post createPost(Post post, Long userId) {
		User user = userService.getUserById(userId);
		user.getPosts().add(post);
		userService.updateUser(user);

		return post;
	}

	public Post updatePostById(Long postId) {
		Post postToUpdate = getPostById(postId);
        return createPost(postToUpdate, postToUpdate.getUser().getId());
	}

	public void deletePostById(Long postId) {
		Post postToDelete = getPostById(postId);
		User user = userService.getUserById(postToDelete.getUser().getId());
		user.getPosts().remove(postToDelete);
		userService.updateUser(user);

		postRepository.delete(postToDelete);
	}
}
