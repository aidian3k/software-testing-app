package project.eepw.softwaretestingcrud.domain.comment.data;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentCreationDTO;
import project.eepw.softwaretestingcrud.domain.comment.dto.CommentDTO;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;
import project.eepw.softwaretestingcrud.domain.comment.helper.CommentDTOMapper;
import project.eepw.softwaretestingcrud.domain.post.data.PostService;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.CommentNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserService userService;
	private final PostService postService;

	@Transactional(readOnly = true)
	public Set<Comment> getAllCommentsDTOs() {
		return new HashSet<>(commentRepository.findAll());
	}

	@Transactional(readOnly = true)
	public Set<CommentDTO> getCommentsAttachedToPost(Long postId) {
		return commentRepository
			.findAll()
			.stream()
			.filter(comment -> comment.getPost().getId().equals(postId))
			.map(CommentDTOMapper::toDto)
			.collect(Collectors.toSet());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Comment saveNewCommentToPost(
		CommentCreationDTO commentCreationDTO
	) {
		User authorOfPost = userService.getUserById(commentCreationDTO.getUserId());
		Post commentedPost = postService.getPostById(commentCreationDTO.getPostId());
		Comment comment = Comment
			.builder()
			.post(commentedPost)
			.content(commentCreationDTO.getContent())
			.author(authorOfPost)
			.build();
		Comment savedComment = commentRepository.save(comment);
		commentedPost.getComments().add(comment);
		postService.savePost(commentedPost);

		return savedComment;
	}

	@Transactional
	public void deleteCommentById(Long commentId) {
		Comment comment = getCommentById(commentId);
		Post post = postService.getPostById(comment.getPost().getId());

		Set<Comment> filteredComments = post
			.getComments()
			.stream()
			.filter(filterComment -> !filterComment.getId().equals(commentId))
			.collect(Collectors.toSet());

		commentRepository.deleteById(commentId);
		post.setComments(filteredComments);
		postService.savePost(post);

		log.debug("Deleted comment with id=[{}]", commentId);
	}

	public Comment getCommentById(Long commentId) {
		return commentRepository
			.findById(commentId)
			.orElseThrow(() ->
				new CommentNotFoundException(
					"Comment with provided id could not be found!"
				)
			);
	}
}
