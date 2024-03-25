package br.com.matheusvictor.springbeginner.repositories;

import br.com.matheusvictor.springbeginner.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

    boolean existsByNameAndDescription(String name, String description);
}
