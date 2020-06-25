package usecases.userManagement.infrastructure.repositories;

import usecases.userManagement.domain.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

}
