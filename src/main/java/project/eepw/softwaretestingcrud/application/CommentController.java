package project.eepw.softwaretestingcrud.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.eepw.softwaretestingcrud.domain.comment.data.CommentService;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentCreationDTO;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentDTO;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Slf4j
class CommentController {

	private final CommentService commentService;

	@GetMapping("/{postId}")
	public ResponseEntity<Collection<CommentDTO>> handleSearchCommentsByPostId(
		@PathVariable Long postId
	) {
		return new ResponseEntity<>(
			commentService.getCommentsAttachedToPost(postId),
			HttpStatus.OK
		);
	}

	@GetMapping
	public ResponseEntity<Collection<Comment>> getAllComments() {
		return new ResponseEntity<>(
			commentService.getAllCommentsDTOs(),
			HttpStatus.OK
		);
	}

	@PostMapping
	public ResponseEntity<Comment> handleCreationOfComment(
		@RequestBody CommentCreationDTO commentCreationDTO
	) {
		return new ResponseEntity<>(
			commentService.saveNewCommentToPost(commentCreationDTO),
			HttpStatus.CREATED
		);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> handleDeletionOfComment(
		@PathVariable Long commentId
	) {
		commentService.deleteCommentById(commentId);

		return ResponseEntity.ok().build();
	}
}
