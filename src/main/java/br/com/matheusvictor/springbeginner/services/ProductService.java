package br.com.matheusvictor.springbeginner.services;

import br.com.matheusvictor.springbeginner.controllers.ProductController;
import br.com.matheusvictor.springbeginner.exceptions.ProductExistsException;
import br.com.matheusvictor.springbeginner.models.ProductModel;
import br.com.matheusvictor.springbeginner.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductModel createProduct(ProductModel productModel) throws ProductExistsException {

        // Verifica se já existe um produto com mesmo nome e a mesma descrição
        if (productRepository.existsByNameAndDescription(productModel.getName(), productModel.getDescription())) {
            throw new ProductExistsException();
        }

        return productRepository.save(productModel);
    }

    public List<ProductModel> getAllProducts() {

        var products = productRepository.findAll();

        if (!products.isEmpty()) {
            for (ProductModel product : products) {
                UUID id = product.getId();
                // Adiciona um link para o recurso específico
                product.add(
                        linkTo(
                                // Define o método que será chamado. Nesta caso, o método getProductById da classe ProductController
                                methodOn(ProductController.class).getProductById(id)
                        ).withSelfRel()
                );
            }
        }
        return products;
    }

    public Optional<ProductModel> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(ProductModel productModel) {
        productRepository.delete(productModel);
    }
}
