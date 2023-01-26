package io.playground;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select o
            from Order o
            join fetch o.products p
            join fetch p.images pi
            join fetch o.customer c
            where o.id = ?1
            """)
    Optional<Order> findOneOrder(Long orderId, Sort sort);

    @Query("""
            select o.id
            from Order o
            left outer join o.products p
            left outer join p.images pi
            """)
    List<Long> findAllOrderIds(Pageable pageable);

    @Query("""
            select o
            from Order o
            join fetch o.products p
            join fetch p.images pi
            join fetch o.customer c
            where o.id in (?1)
            """)
    List<Order> findAllOrders(List<Long> orderIds);
}
