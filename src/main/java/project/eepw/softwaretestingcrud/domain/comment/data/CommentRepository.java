package project.eepw.softwaretestingcrud.domain.comment.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.eepw.softwaretestingcrud.domain.comment.entity.Comment;

@Repository
interface CommentRepository extends JpaRepository<Comment, Long> {}
