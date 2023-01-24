package io.playground;

import io.playground.product.ProductRepository;
import io.playground.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigurePostgresDatabase
class DynamicUpdateTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void updatingEveryColumn() {
        // select p1_0.id,p1_0.description,p1_0.name,p1_0.quantity from product p1_0 where p1_0.id=?
        final var notebook = productRepository.findById(1L).orElseThrow();
        notebook.setName("Notebook");

        // update product set description=?, name=?, quantity=? where id=?
        productRepository.save(notebook);

        // select p1_0.id,p1_0.description,p1_0.name,p1_0.quantity from product p1_0 where p1_0.id=?
        final var notebookUpdated = productRepository.findById(1L).orElseThrow();
        assertThat(notebookUpdated.getId()).isEqualTo(notebook.getId());
        assertThat(notebookUpdated.getName()).isEqualTo("Notebook");
        assertThat(notebookUpdated.getDescription()).isEqualTo(notebook.getDescription());
        assertThat(notebookUpdated.getQuantity()).isEqualTo(notebook.getQuantity());
    }

    @Test
    void updatingOnlyColumnsChanged() {
        // select v1_0.id,v1_0.brand,v1_0.model,v1_0.release_year from vehicle v1_0 where v1_0.id=?
        final var vehicle = vehicleRepository.findById(1L).orElseThrow();
        vehicle.setModel("F-250");
        vehicle.setReleaseYear(Year.of(2022));

        // update vehicle set model=?, release_year=? where id=?
        vehicleRepository.save(vehicle);

        // select v1_0.id,v1_0.brand,v1_0.model,v1_0.release_year from vehicle v1_0 where v1_0.id=?
        final var vehicleUpdated = vehicleRepository.findById(1L).orElseThrow();
        assertThat(vehicleUpdated.getId()).isEqualTo(vehicle.getId());
        assertThat(vehicleUpdated.getBrand()).isEqualTo(vehicle.getBrand());
        assertThat(vehicleUpdated.getModel()).isEqualTo("F-250");
        assertThat(vehicleUpdated.getElectric()).isEqualTo(vehicle.getElectric());
        assertThat(vehicleUpdated.getReleaseYear()).isEqualTo(Year.of(2022));
    }
}
