package io.playground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigurePostgresDatabase
class MultipleJoinFetchTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    void findingOneOrder() {
        final var sort = Sort.by(List.of(
                Sort.Order.asc("p.id"),
                Sort.Order.asc("pi.id")));

        // select o1_0.id,o1_0.created_at,c1_0.id,c1_0.name,p1_0.order_id,p1_1.id,i1_0.product_id,i1_0.id,i1_0.url,p1_1.name
        // from product_order o1_0
        // join (product_order_item p1_0 join product p1_1 on p1_1.id=p1_0.product_id) on o1_0.id=p1_0.order_id
        // join product_image i1_0 on p1_1.id=i1_0.product_id
        // join customer c1_0 on c1_0.id=o1_0.customer_id
        // where o1_0.id=?
        // order by p1_0.product_id asc,i1_0.id asc
        final var order = orderRepository.findOneOrder(1L, sort).orElseThrow();

        assertJohnSmithOrder(order);

        assertThat(order.getProducts())
                .filteredOn(product -> product.getId() == 1L)
                .first().satisfies(MultipleJoinFetchTest::assertNotebook);

        assertThat(order.getProducts())
                .filteredOn(product -> product.getId() == 2L)
                .first().satisfies(MultipleJoinFetchTest::assertPencil);
    }

    @Test
    @Transactional
    void findingAllOrders() {
        final var pageable = PageRequest.of(0, 2, Sort.by(List.of(
                Sort.Order.asc("p.id"),
                Sort.Order.asc("pi.id"))));

        // select o1_0.id
        // from product_order o1_0
        // left join (product_order_item p1_0 join product p1_1 on p1_1.id=p1_0.product_id) on o1_0.id=p1_0.order_id
        // left join product_image i1_0 on p1_1.id=i1_0.product_id
        // order by p1_0.product_id asc,i1_0.id asc
        // offset ?rows fetch first ? rows only
        final var orderIds = orderRepository.findAllOrderIds(pageable);

        // select o1_0.id,o1_0.created_at,c1_0.id,c1_0.name,p1_0.order_id,p1_1.id,i1_0.product_id,i1_0.id,i1_0.url,p1_1.name
        // from product_order o1_0
        // join (product_order_item p1_0 join product p1_1 on p1_1.id=p1_0.product_id) on o1_0.id=p1_0.order_id
        // join product_image i1_0 on p1_1.id=i1_0.product_id
        // join customer c1_0 on c1_0.id=o1_0.customer_id
        // where o1_0.id in(?,?)
        final var orders = orderRepository.findAllOrders(orderIds);

        assertThat(orders.size()).isEqualTo(pageable.getPageSize());
        assertThat(orders)
                .map(Order::getId)
                .containsOnly(1L, 2L);

        assertThat(orders)
                .filteredOn(order -> order.getId() == 1L)
                .first()
                .satisfies(MultipleJoinFetchTest::assertJohnSmithOrder)
                .satisfies(order -> {
                    assertThat(order.getProducts())
                            .filteredOn(product -> product.getId() == 1L)
                            .first().satisfies(MultipleJoinFetchTest::assertNotebook);

                    assertThat(order.getProducts())
                            .filteredOn(product -> product.getId() == 2L)
                            .first().satisfies(MultipleJoinFetchTest::assertPencil);
                });

        assertThat(orders)
                .filteredOn(order -> order.getId() == 2L)
                .first()
                .satisfies(MultipleJoinFetchTest::assertMaryJaneOrder)
                .satisfies(order -> {
                    assertThat(order.getProducts())
                            .filteredOn(product -> product.getId() == 1L)
                            .first().satisfies(MultipleJoinFetchTest::assertNotebook);
                });
    }

    private static void assertJohnSmithOrder(final Order order) {
        assertThat(order.getCreatedAt()).isNotNull();

        assertThat(order.getCustomer()).satisfies(customer -> {
            assertThat(customer.getId()).isEqualTo(1L);
            assertThat(customer.getName()).isEqualTo("John Smith");
        });

        assertThat(order.getProducts()).hasSize(2);

        assertThat(order.getProducts())
                .extracting(Product::getId)
                .containsOnly(1L, 2L);
    }

    private static void assertMaryJaneOrder(final Order order) {
        assertThat(order.getCreatedAt()).isNotNull();

        assertThat(order.getCustomer()).satisfies(customer -> {
            assertThat(customer.getId()).isEqualTo(2L);
            assertThat(customer.getName()).isEqualTo("Mary Jane");
        });

        assertThat(order.getProducts()).hasSize(1);

        assertThat(order.getProducts())
                .extracting(Product::getId)
                .containsOnly(1L);
    }

    private static void assertNotebook(final Product product) {
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Notebook");
        assertThat(product.getImages()).hasSize(2);
        assertThat(product.getImages())
                .extracting(ProductImage::getId)
                .containsOnly(1L, 2L);

        assertThat(product.getImages())
                .filteredOn(image -> image.getId() == 1L)
                .first().satisfies(image -> assertThat(image.getUrl()).isEqualTo("https://playground.io/images/products/1/image-1.jpg"));
        assertThat(product.getImages())
                .filteredOn(image -> image.getId() == 2L)
                .first().satisfies(image -> assertThat(image.getUrl()).isEqualTo("https://playground.io/images/products/1/image-2.jpg"));
    }

    private static void assertPencil(final Product product) {
        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("Pencil");
        assertThat(product.getImages()).hasSize(2);
        assertThat(product.getImages())
                .extracting(ProductImage::getId)
                .containsOnly(3L, 4L);

        assertThat(product.getImages())
                .filteredOn(image -> image.getId() == 3L)
                .first().satisfies(image -> assertThat(image.getUrl()).isEqualTo("https://playground.io/images/products/2/image-1.jpg"));
        assertThat(product.getImages())
                .filteredOn(image -> image.getId() == 4L)
                .first().satisfies(image -> assertThat(image.getUrl()).isEqualTo("https://playground.io/images/products/2/image-2.jpg"));
    }
}
