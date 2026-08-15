package andreas.purchaseorder.repository;

import andreas.purchaseorder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByPhoneAndIdNot(String phone, Integer id);

}
