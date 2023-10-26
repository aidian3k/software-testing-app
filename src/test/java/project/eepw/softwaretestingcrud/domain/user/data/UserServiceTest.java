package project.eepw.softwaretestingcrud.domain.user.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static project.eepw.softwaretestingcrud.domain.DomainUtils.makeUser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.eepw.softwaretestingcrud.domain.user.entity.User;
import project.eepw.softwaretestingcrud.infrastructure.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserService userService;

  @Test
  void shouldReturnTheUserWhenUserWithThatIdIsInDB() {
    //given
    User user = makeUser();
    Long id = user.getId();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    //when
    User fetchedUser = userService.getUserById(id);

    //then
    verify(userRepository, times(1)).findById(id);
    assertThat(fetchedUser)
        .usingRecursiveComparison()
        .isEqualTo(user);
  }

  @Test
  void shouldThrowExceptionWhenUserWithThatIdIsNotInDB() {
    //given
    Long notExistingUserId = 1L;
    when(userRepository.findById(notExistingUserId)).thenReturn(Optional.empty());

    //when
    ThrowingCallable getUserByIdExecutable = () -> userService.getUserById(notExistingUserId);

    //then
    assertThatThrownBy(getUserByIdExecutable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User has not been found");
    verify(userRepository, times(1)).findById(notExistingUserId);
  }

  @Test
  void shouldThrowExceptionWhenProvidedIdIsNull() {
    //given
    Long nullId = null;
    when(userRepository.findById(nullId)).thenThrow(IllegalArgumentException.class);

    //when
    ThrowingCallable getUserByIdExecutable = () -> userService.getUserById(nullId);

    //then
    assertThatThrownBy(getUserByIdExecutable)
        .isInstanceOf(IllegalArgumentException.class);
    verify(userRepository, times(1)).findById(nullId);
  }

  @Test
  void shouldReturnAllUsersWhenGetAllUsersInvoked() {
    //given
    User user1 = makeUser();
    User user2 = makeUser().toBuilder()
        .id(2L)
        .name("James")
        .email("u2@example.com")
        .surname("Jackson")
        .password("password")
        .build();

    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    //when
    Collection<User> allUsers = userService.getAllUsers();

    //then
    verify(userRepository, times(1)).findAll();
    assertThat(allUsers)
        .usingRecursiveComparison()
        .isEqualTo(List.of(user1, user2));
  }

  @Test
  void shouldReturnEmptyCollectionWhenNoDataIsPresentInDB() {
    //given
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    //when
    Collection<User> allUsers = userService.getAllUsers();

    //then
    verify(userRepository, times(1)).findAll();
    assertThat(allUsers)
        .usingRecursiveComparison()
        .isEqualTo(Collections.emptyList());
  }

  @Test
  void shouldReturnUserWhenUserWithProvidedEmailIsInDB() {
    //given
    User user = makeUser();
    String email = user.getEmail();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    //when
    User fetchedUser = userService.getUserByEmail(email);

    //then
    verify(userRepository, times(1)).findByEmail(email);
    assertThat(fetchedUser)
        .usingRecursiveComparison()
        .isEqualTo(user);
  }

  @Test
  void shouldThrowExceptionWhenProvidedEmailIsNotInDB() {
    //given
    String notExistingEmail = "notexisting@example.com";
    when(userRepository.findByEmail(notExistingEmail)).thenReturn(Optional.empty());

    //when
    ThrowingCallable getUserByEmailExecutable = () -> userService.getUserByEmail(notExistingEmail);

    //then
    assertThatThrownBy(getUserByEmailExecutable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User has not been found!");
    verify(userRepository, times(1)).findByEmail(notExistingEmail);
  }

  @Test
  void shouldThrowExceptionWhenProvidedEmailIsNull() {
    //given
    String nullEmail = null;
    when(userRepository.findByEmail(nullEmail)).thenThrow(IllegalArgumentException.class);

    //when
    ThrowingCallable getUserByEmailExecutable = () -> userService.getUserByEmail(nullEmail);

    //then
    assertThatThrownBy(getUserByEmailExecutable)
        .isInstanceOf(IllegalArgumentException.class);
    verify(userRepository, times(1)).findByEmail(nullEmail);
  }

  @Test
  void shouldCreateNewUserWhenGivenUserHasAllRequiredData() {
    //given
    User user = makeUser().toBuilder()
        .id(null).build();
    User dbUser = user.toBuilder()
        .id(5L).build();
    when(userRepository.save(user)).thenReturn(dbUser);

    //when
    User createdUser = userService.createUser(user);

    //then
    verify(userRepository, times(1)).save(user);
    assertThat(createdUser)
        .usingRecursiveComparison()
        .isEqualTo(dbUser);
  }

  @Test
  void shouldThrowExceptionWhenGivenUserIsNull() {
    //given
    User nullUser = null;
    when(userRepository.save(nullUser)).thenThrow(IllegalArgumentException.class);

    //when
    ThrowingCallable createUserExecutable = () -> userService.createUser(nullUser);

    //then
    assertThatThrownBy(createUserExecutable)
        .isInstanceOf(IllegalArgumentException.class);
    verify(userRepository, times(1)).save(nullUser);
  }

  @Test
  void shouldUpdateUserDataWhenGivenUserAlreadyExists() {
    //given
    User user = makeUser();
    Long userId = user.getId();
    User toUpdate = user.toBuilder()
        .name("James")
        .email("james@example.com")
        .surname("Jackson")
        .password("123")
        .build();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(toUpdate)).thenReturn(toUpdate);

    //when
    User updatedUser = userService.updateUser(toUpdate);

    //then
    verify(userRepository, times(1)).findById(toUpdate.getId());
    verify(userRepository, times(1)).save(toUpdate);
    assertThat(updatedUser)
        .usingRecursiveComparison()
        .isEqualTo(toUpdate);
  }

  @Test
  void shouldThrowExceptionOnUpdateWhenGivenUserDoesNotExist() {
    //given
    User notExistingUser = makeUser();

    when(userRepository.findById(notExistingUser.getId())).thenReturn(Optional.empty());

    //when
    ThrowingCallable updateUserExecutable = () -> userService.updateUser(notExistingUser);

    //then
    assertThatThrownBy(updateUserExecutable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User has not been found!");
    verify(userRepository, times(1)).findById(notExistingUser.getId());
    verify(userRepository, times(0)).save(notExistingUser);
  }

  @Test
  void shouldThrowExceptionOnUpdateWhenGivenUserIsNull() {
    //given
    User nullUser = null;
    when(userRepository.save(nullUser)).thenThrow(IllegalArgumentException.class);

    //when
    ThrowingCallable updateUserExecutable = () -> userService.updateUser(nullUser);

    //then
    assertThatThrownBy(updateUserExecutable)
        .isInstanceOf(IllegalArgumentException.class);
    verify(userRepository, times(0)).save(nullUser);
  }

  @Test
  void shouldDeleteUserWhenUserWithGivenIdExists() {
    //given
    User user = makeUser();
    Long id = user.getId();
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    //when
    userService.deleteUserById(id);

    //then
    verify(userRepository, times(1)).findById(id);
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  void shouldThrowExceptionWhenTryingToDeleteNotExistingUser() {
    //given
    Long id = 1L;
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    //when
    ThrowingCallable deleteUserExecutable = () -> userService.deleteUserById(id);

    //then
    assertThatThrownBy(deleteUserExecutable)
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User has not been found!");
    verify(userRepository, times(1)).findById(id);
  }

  @Test
  void shouldThrowExceptionWhenGivenIdToDeleteIsNull() {
    //given
    Long id = null;
    when(userRepository.findById(id)).thenThrow(IllegalArgumentException.class);

    //when
    ThrowingCallable deleteUserExecutable = () -> userService.deleteUserById(id);

    //then
    assertThatThrownBy(deleteUserExecutable)
        .isInstanceOf(IllegalArgumentException.class);
    verify(userRepository, times(1)).findById(id);
  }
}