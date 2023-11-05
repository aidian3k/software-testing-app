package project.eepw.softwaretestingcrud.domain.post.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static project.eepw.softwaretestingcrud.domain.DomainUtils.makePost;
import static project.eepw.softwaretestingcrud.domain.DomainUtils.makeUser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.eepw.softwaretestingcrud.domain.post.dto.PostDTO;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.PostNotFoundException;
import project.eepw.softwaretestingcrud.infrastructure.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private PostService postService;

	@Nested
	@DisplayName("Get post test")
	class GetPostTests {

		@Test
		void shouldReturnPostWhenGivenExistingPostId() {
			//given
			Post post = makePost();
			Long id = post.getId();

			when(postRepository.findById(id)).thenReturn(Optional.of(post));

			//when
			PostDTO fetchedPost = postService.getPostDTOById(id);

			//then
			verify(postRepository, times(1)).findById(id);

			Assertions.assertAll(
				() -> assertThat(fetchedPost.getId()).isEqualTo(post.getId()),
				() -> assertThat(fetchedPost.getContent()).isEqualTo(post.getContent())
			);
		}

		@Test
		void shouldThrowExceptionWhenGivenNonExistingPostId() {
			//given
			Long nonExistingPostId = 2L;
			when(postRepository.findById(nonExistingPostId))
				.thenReturn(Optional.empty());

			//when
			ThrowingCallable getPostByIdExecutable = () ->
				postService.getPostDTOById(nonExistingPostId);

			//then
			assertThatThrownBy(getPostByIdExecutable)
				.isInstanceOf(PostNotFoundException.class)
				.hasMessageContaining("Post has not been found!");
			verify(postRepository, times(1)).findById(nonExistingPostId);
		}

		@Test
		void shouldThrowExceptionWhenGivenNullId() {
			//given
			Long nullId = null;
			when(postRepository.findById(nullId))
				.thenThrow(IllegalArgumentException.class);

			//when
			ThrowingCallable getPostByIdExecutable = () ->
				postService.getPostDTOById(nullId);

			//then
			assertThatThrownBy(getPostByIdExecutable)
				.isInstanceOf(IllegalArgumentException.class);
			verify(postRepository, times(1)).findById(nullId);
		}

		@Test
		void shouldReturnAllPostsWhenGetAllPostsInvoked() {
			//given
			Post firstPost = makePost();
			Post secondPost = makePost()
				.toBuilder()
				.id(2L)
				.content("Another post content")
				.user(makeUser())
				.build();

			List<Post> expectedListOfPosts = List.of(firstPost, secondPost);
			List<PostDTO> expectedFetchedListOfPosts = expectedListOfPosts
				.stream()
				.map(post ->
					PostDTO.builder().id(post.getId()).content(post.getContent()).build()
				)
				.toList();

			when(postRepository.findAll()).thenReturn(expectedListOfPosts);

			//when
			Collection<PostDTO> allPosts = postService.getAllPosts();

			//then
			verify(postRepository, times(1)).findAll();
			assertThat(allPosts)
				.containsExactlyInAnyOrderElementsOf(expectedFetchedListOfPosts);
		}

		@Test
		void shouldReturnEmptyCollectionWhenNoPostsInDatabase() {
			//given
			when(postRepository.findAll()).thenReturn(Collections.emptyList());

			//when
			Collection<PostDTO> allPosts = postService.getAllPosts();

			//then
			verify(postRepository, times(1)).findAll();
			assertThat(allPosts).isEmpty();
		}

		@Test
		void shouldReturnSetOfUserPostsWhenGivenExistingUserId() {
			//given
			Post firstPost = makePost();
			Post secondPost = makePost()
				.toBuilder()
				.id(2L)
				.content("Another post content")
				.build();
			User user = makeUser()
				.toBuilder()
				.posts(Set.of(firstPost, secondPost))
				.build();
			Long userId = user.getId();

			Collection<Post> expectedListOfPosts = Set.of(firstPost, secondPost);
			Collection<PostDTO> expectedListOfPostDTOs = expectedListOfPosts
				.stream()
				.map(post ->
					PostDTO.builder().id(post.getId()).content(post.getContent()).build()
				)
				.collect(Collectors.toSet());
			when(userService.getUserById(userId)).thenReturn(user);
			//when
			Collection<PostDTO> fetchedPosts = postService.getAllUserPosts(userId);

			//then
			verify(userService, times(1)).getUserById(userId);

			assertThat(fetchedPosts)
				.usingRecursiveComparison()
				.isEqualTo(expectedListOfPostDTOs);
		}

		@Test
		void shouldReturnEmptySetOfUserPostsWhenGivenIdOfExistingUserWithoutPosts() {
			//given
			User user = makeUser();
			Long userId = user.getId();

			when(userService.getUserById(userId)).thenReturn(user);

			//when
			Collection<PostDTO> fetchedPosts = postService.getAllUserPosts(userId);

			//then
			verify(userService, times(1)).getUserById(userId);
			assertThat(fetchedPosts).usingRecursiveComparison().isEqualTo(Set.of());
		}

		@Test
		void shouldThrowExceptionWhenGivenNonExistingUserId() {
			//given
			Long userId = 2L;

			when(userService.getUserById(userId))
				.thenThrow(new UserNotFoundException("User has not been found"));

			//when
			ThrowingCallable getAllUserPostsExecutable = () ->
				postService.getAllUserPosts(userId);

			//then
			assertThatThrownBy(getAllUserPostsExecutable)
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("User has not been found");
			verify(userService, times(1)).getUserById(userId);
		}

		@Test
		void shouldThrowExceptionWhenGivenNullUserId() {
			//given
			Long nullUserId = null;

			when(userService.getUserById(nullUserId))
				.thenThrow(IllegalArgumentException.class);

			//when
			ThrowingCallable getAllUserPostsExecutable = () ->
				postService.getAllUserPosts(nullUserId);

			//then
			assertThatThrownBy(getAllUserPostsExecutable)
				.isInstanceOf(IllegalArgumentException.class);
			verify(userService, times(1)).getUserById(nullUserId);
		}
	}

	@Nested
	@DisplayName("Update post test")
	class UpdatePostTest {

		@Test
		void shouldReturnUpdatedPostWhenGivenValidPostDTO() {
			//given
			Post post = makePost();
			PostDTO postDTO = makePostDTO();
			Long userId = 1L;
			User expectedUser = makeUser()
				.toBuilder()
				.posts(new HashSet<>(Set.of(post)))
				.build();

			when(userService.getUserById(userId)).thenReturn(expectedUser);
			when(userService.updateUser(any()))
				.thenAnswer(invocation -> invocation.getArgument(0));

			//when
			PostDTO updatedPost = postService.updatePost(postDTO, userId);

			//then
			verify(userService, times(1)).getUserById(userId);
			verify(userService, times(1)).updateUser(expectedUser);
			assertThat(updatedPost).usingRecursiveComparison().isEqualTo(postDTO);
		}

		@Test
		void shouldThrowExceptionWhenGivenPostDTOWithInvalidId() {
			//given
			Post post = makePost();
			PostDTO postDTO = PostDTO
				.builder()
				.id(3L)
				.content("Wrong id post content")
				.build();
			Long userId = 1L;
			User expectedUser = makeUser()
				.toBuilder()
				.posts(new HashSet<>(Set.of(post)))
				.build();

			when(userService.getUserById(userId)).thenReturn(expectedUser);

			//when
			ThrowingCallable postUpdateExecutable = () ->
				postService.updatePost(postDTO, userId);

			//then
			assertThatThrownBy(postUpdateExecutable)
				.hasMessage("Post with id=[3] has not been found")
				.isInstanceOf(PostNotFoundException.class);
			verify(userService, times(1)).getUserById(userId);
			verify(userService, times(0)).updateUser(expectedUser);
		}

		@Test
		void shouldThrowExceptionWhenGivenNonExistingUserId() {
			//given
			Long userId = 2L;
			PostDTO postDTO = makePostDTO();

			when(userService.getUserById(userId))
				.thenThrow(new UserNotFoundException("User has not been found"));

			//when
			ThrowingCallable getAllUserPostsExecutable = () ->
				postService.updatePost(postDTO, userId);

			//then
			assertThatThrownBy(getAllUserPostsExecutable)
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("User has not been found");
			verify(userService, times(1)).getUserById(userId);
			verify(userService, times(0)).updateUser(any());
		}

		@Test
		void shouldThrowExceptionWhenGivenNullUserId() {
			//given
			Long nullUserId = null;
			PostDTO postDTO = makePostDTO();

			when(userService.getUserById(nullUserId))
				.thenThrow(IllegalArgumentException.class);

			//when
			ThrowingCallable getAllUserPostsExecutable = () ->
				postService.updatePost(postDTO, nullUserId);

			//then
			assertThatThrownBy(getAllUserPostsExecutable)
				.isInstanceOf(IllegalArgumentException.class);
			verify(userService, times(1)).getUserById(nullUserId);
			verify(userService, times(0)).updateUser(any());
		}
	}

	@Nested
	@DisplayName("Delete post test")
	class DeletePostTest {

		@Test
		void shouldThrowExceptionWhenTryingToDeleteNonExistingPost() {
			//given
			Long notExistingPostId = 1L;
			when(postRepository.findById(notExistingPostId))
				.thenReturn(Optional.empty());

			//when
			ThrowingCallable deletePostByIdExecutable = () ->
				postService.deletePostById(notExistingPostId);

			//then
			assertThatThrownBy(deletePostByIdExecutable)
				.isInstanceOf(PostNotFoundException.class)
				.hasMessageContaining("Post has not been found");
			verify(postRepository, times(1)).findById(notExistingPostId);
		}

		@Test
		void shouldDeletePostWhenPostWithGivenIdExists() {
			//given
			Post post = makePost();
			Long postId = post.getId();
			User expectedUser = makeUser()
				.toBuilder()
				.posts(new HashSet<>(Set.of(post)))
				.build();

			when(postRepository.findById(postId)).thenReturn(Optional.of(post));
			when(userService.getUserById(post.getUser().getId()))
				.thenReturn(expectedUser);

			//when
			postService.deletePostById(postId);

			//then
			verify(postRepository, times(1)).findById(postId);
			verify(postRepository, times(1)).delete(post);
		}

		@Test
		void shouldThrowExceptionWhenGivenIdIsNull() {
			//given
			Long postId = null;
			when(postRepository.findById(postId))
				.thenThrow(IllegalArgumentException.class);

			//when
			ThrowingCallable deletePostExecutable = () ->
				postService.deletePostById(postId);

			//then
			assertThatThrownBy(deletePostExecutable)
				.isInstanceOf(IllegalArgumentException.class);
		}
	}

	private PostDTO makePostDTO() {
		return PostDTO.builder().id(1L).content("Sample post content").build();
	}
}
