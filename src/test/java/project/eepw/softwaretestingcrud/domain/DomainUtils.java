package project.eepw.softwaretestingcrud.domain;

import project.eepw.softwaretestingcrud.domain.post.entity.Post;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.Collections;

public final class DomainUtils {
    private DomainUtils() {

    }

    public static Post makePost() {
        return Post.builder()
                .id(1L)
                .content("Some random post content")
                .user(makeUser())
                .build();
    }

    public static User makeUser() {
        return User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .surname("Doe")
                .password("password")
                .posts(Collections.emptySet())
                .build();
    }
}
