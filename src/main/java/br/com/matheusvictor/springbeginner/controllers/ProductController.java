package br.com.matheusvictor.springbeginner.controllers;

import br.com.matheusvictor.springbeginner.dtos.ProductRecordDto;
import br.com.matheusvictor.springbeginner.models.ProductModel;
import br.com.matheusvictor.springbeginner.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> createProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        // A anotation @Valid valida o objeto de entrada, garantindo que os campos estão preenchidos corretamente
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {

        List<ProductModel> products = productRepository.findAll();

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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable(value = "id") UUID id) {

        Optional<ProductModel> productModelOptional = productRepository.findById(id);

        if (productModelOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        productModelOptional.get().add(
                linkTo(
                        methodOn(ProductController.class).getAllProducts()
                ).withRel("All Products")
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productModelOptional.get());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(
            @PathVariable(value = "id") UUID id,
            @RequestBody @Valid ProductRecordDto productRecordDto
    ) {

        Optional<ProductModel> productModelOptional = productRepository.findById(id);

        if (productModelOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        var productModel = productModelOptional.get();
        BeanUtils.copyProperties(productRecordDto, productModel);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {

        Optional<ProductModel> productModelOptional = productRepository.findById(id);

        if (productModelOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        productRepository.delete(productModelOptional.get());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Product deleted successfully");
    }
}
