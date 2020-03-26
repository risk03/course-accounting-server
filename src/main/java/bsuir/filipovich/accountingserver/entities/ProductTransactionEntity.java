package bsuir.filipovich.accountingserver.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product_transaction", schema = "accounting")
public class ProductTransactionEntity {
    private int productTransactionId;
    private BigDecimal quantity;
    private ProductEntity productByProductId;

    @Id
    @Column(name = "product_transaction_id", nullable = false)
    public int getProductTransactionId() {
        return productTransactionId;
    }

    public void setProductTransactionId(int productTransactionId) {
        this.productTransactionId = productTransactionId;
    }

    @Basic
    @Column(name = "quantity", nullable = false, precision = 3)
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductTransactionEntity that = (ProductTransactionEntity) o;
        return productTransactionId == that.productTransactionId &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productTransactionId, quantity);
    }

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    public ProductEntity getProductByProductId() {
        return productByProductId;
    }

    public void setProductByProductId(ProductEntity productByProductId) {
        this.productByProductId = productByProductId;
    }
}
