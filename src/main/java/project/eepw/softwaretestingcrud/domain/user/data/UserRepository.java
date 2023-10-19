package project.eepw.softwaretestingcrud.domain.user.data;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.eepw.softwaretestingcrud.domain.user.entity.User;

import java.util.Optional;

@Repository
@Transactional
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
