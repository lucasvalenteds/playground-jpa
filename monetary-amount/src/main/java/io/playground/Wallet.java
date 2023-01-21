package io.playground;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CompositeType;

import javax.money.MonetaryAmount;

@Entity
@Table
@Getter
@Setter
public final class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "amount", column = @Column(name = "balance_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "balance_currency"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount balance;
}
