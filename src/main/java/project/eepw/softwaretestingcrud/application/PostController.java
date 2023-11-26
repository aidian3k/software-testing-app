package project.eepw.softwaretestingcrud.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.eepw.softwaretestingcrud.domain.post.data.PostService;
import project.eepw.softwaretestingcrud.domain.post.dto.PostCreationDTO;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Validated
@Slf4j
class PostController {

	private final PostService postService;

	@GetMapping
	public Collection<PostDTO> getAllPosts() {
		return postService.getAllPosts();
	}

	@GetMapping("/{id}")
	public PostDTO getPostById(@PathVariable Long id) {
		return postService.getPostDTOById(id);
	}

	@GetMapping("/user/{userId}")
	public Collection<PostDTO> getAllUserPosts(@PathVariable Long userId) {
		return postService.getAllUserPosts(userId);
	}

	@DeleteMapping("/{postId}")
	public void deletePostById(@PathVariable Long postId) {
		postService.deletePostById(postId);
	}

	@PostMapping("/user/{userId}")
	@ResponseStatus(HttpStatus.CREATED)
	public PostDTO createPost(
		@RequestBody PostCreationDTO postCreationDTO,
		@PathVariable Long userId
	) {
		return postService.createPost(postCreationDTO, userId);
	}

	@PutMapping("/user/{userId}")
	public PostDTO updatePostById(@RequestBody PostDTO modifiedPost, @PathVariable Long userId) {
		return postService.updatePost(modifiedPost, userId);
	}
}
