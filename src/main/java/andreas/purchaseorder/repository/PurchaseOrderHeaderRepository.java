package andreas.purchaseorder.repository;

import andreas.purchaseorder.entity.PurchaseOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderHeaderRepository extends JpaRepository<PurchaseOrderHeader, Integer> {

    @Query("SELECT h FROM PurchaseOrderHeader h LEFT JOIN FETCH h.details WHERE h.id = :id")
    Optional<PurchaseOrderHeader> findByIdWithDetails(@Param("id") Integer id);

}
