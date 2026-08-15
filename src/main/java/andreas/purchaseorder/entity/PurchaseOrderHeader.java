package andreas.purchaseorder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "po_h")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PurchaseOrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datetime")
    private LocalDateTime datetime;

    @Column(length = 500)
    private String description;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "total_cost")
    private Integer totalCost;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_datetime", updatable = false)
    private LocalDateTime createdDatetime;

    @LastModifiedDate
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @OneToMany(mappedBy = "purchaseOrderHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderDetail> details = new ArrayList<>();

    public void addDetail(PurchaseOrderDetail detail) {
        details.add(detail);
        detail.setPurchaseOrderHeader(this);
    }

    public void removeDetail(PurchaseOrderDetail detail) {
        details.remove(detail);
        detail.setPurchaseOrderHeader(null);
    }

}
