package project.eepw.softwaretestingcrud.domain.post.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.data.UserService;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import project.eepw.softwaretestingcrud.infrastructure.exception.PostNotFoundException;
import project.eepw.softwaretestingcrud.infrastructure.exception.UserNotFoundException;


import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Optional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static project.eepw.softwaretestingcrud.domain.DomainUtils.makePost;
import static project.eepw.softwaretestingcrud.domain.DomainUtils.makeUser;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldReturnPostWhenGivenExistingPostId() {
        //given
        Post post = makePost();
        Long id = post.getId();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        //when
        Post fetchedPost = postService.getPostById(id);

        //then
        verify(postRepository, times(1)).findById(id);
        assertThat(fetchedPost)
                .usingRecursiveComparison()
                .isEqualTo(post);
    }

    @Test
    void shouldThrowExceptionWhenGivenNonExistingPostId() {
        //given
        Long nonExistingPostId = 2L;
        when(postRepository.findById(nonExistingPostId)).thenReturn(Optional.empty());

        //when
        ThrowingCallable getPostByIdExecutable = () -> postService.getPostById(nonExistingPostId);

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
        when(postRepository.findById(nullId)).thenThrow(IllegalArgumentException.class);

        //when
        ThrowingCallable getPostByIdExecutable = () -> postService.getPostById(nullId);

        //then
        assertThatThrownBy(getPostByIdExecutable)
                .isInstanceOf(IllegalArgumentException.class);
        verify(postRepository, times(1)).findById(nullId);
    }

    @Test
    void shouldReturnAllPostsWhenGetAllPostsInvoked() {
        //given
        Post firstPost = makePost();
        Post secondPost = makePost().toBuilder()
                .id(2L)
                .content("Another post content")
                .user(makeUser())
                .build();

        List<Post> expectedListOfPosts = List.of(firstPost, secondPost);

        when(postRepository.findAll()).thenReturn(expectedListOfPosts);

        //when
        Collection<Post> allPosts = postService.getAllPosts();

        //then
        verify(postRepository, times(1)).findAll();
        assertThat(allPosts)
                .usingRecursiveComparison()
                .isEqualTo(expectedListOfPosts);
    }

    @Test
    void shouldReturnEmptyCollectionWhenNoPostsInDatabase() {
        //given
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        Collection<Post> allPosts = postService.getAllPosts();

        //then
        verify(postRepository, times(1)).findAll();
        assertThat(allPosts)
                .usingRecursiveComparison()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void shouldReturnSetOfUserPostsWhenGivenExistingUserId() {
        //given
        Post firstPost = makePost();
        Post secondPost = makePost().toBuilder()
                .id(2L)
                .content("Another post content")
                .build();
        User user = makeUser().toBuilder()
                .posts(Set.of(firstPost, secondPost))
                .build();
        Long userId = user.getId();

        Collection<Post> expectedListOfPosts = Set.of(firstPost, secondPost);
        when(userService.getUserById(userId)).thenReturn(user);

        //when
        Collection<Post> fetchedPosts = postService.getAllUserPosts(userId);

        //then
        verify(userService, times(1)).getUserById(userId);
        assertThat(fetchedPosts)
                .usingRecursiveComparison()
                .isEqualTo(expectedListOfPosts);
    }

    @Test
    void shouldReturnEmptySetOfUserPostsWhenGivenIdOfExistingUserWithoutPosts() {
        //given
        User user = makeUser();
        Long userId = user.getId();

        when(userService.getUserById(userId)).thenReturn(user);

        //when
        Collection<Post> fetchedPosts = postService.getAllUserPosts(userId);

        //then
        verify(userService, times(1)).getUserById(userId);
        assertThat(fetchedPosts)
                .usingRecursiveComparison()
                .isEqualTo(Set.of());
    }

    @Test
    void shouldThrowExceptionWhenGivenNonExistingUserId() {
        //given
        Long userId = 2L;

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User has not been found"));

        //when
        ThrowingCallable getAllUserPostsExecutable = () -> postService.getAllUserPosts(userId);

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

        when(userService.getUserById(nullUserId)).thenThrow(IllegalArgumentException.class);

        //when
        ThrowingCallable getAllUserPostsExecutable = () -> postService.getAllUserPosts(nullUserId);

        //then
        assertThatThrownBy(getAllUserPostsExecutable)
                .isInstanceOf(IllegalArgumentException.class);
        verify(userService, times(1)).getUserById(nullUserId);
    }
}
