package pl.createcompetition.authorizationserver.newpackages.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByIdAndPassword(Long privateId, String password);

    Optional<User> findByIdAndEmail(Long id, String userName);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

}
