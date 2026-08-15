package andreas.purchaseorder.repository;

import andreas.purchaseorder.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Integer> {

    boolean existsByItemId(Integer itemId);

}
