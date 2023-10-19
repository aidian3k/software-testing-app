package project.eepw.softwaretestingcrud.domain.post.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.eepw.softwaretestingcrud.domain.post.entity.Post;

@Repository
interface PostRepository extends JpaRepository<Post, Long> {}
