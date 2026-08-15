package andreas.purchaseorder.repository;

import andreas.purchaseorder.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

}
