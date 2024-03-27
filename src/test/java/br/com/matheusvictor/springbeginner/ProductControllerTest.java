package br.com.matheusvictor.springbeginner;

import br.com.matheusvictor.springbeginner.constants.ExceptionsMessages;
import br.com.matheusvictor.springbeginner.controllers.ProductController;
import br.com.matheusvictor.springbeginner.dtos.ProductRecordDto;
import br.com.matheusvictor.springbeginner.exceptions.ProductExistsException;
import br.com.matheusvictor.springbeginner.models.ProductModel;
import br.com.matheusvictor.springbeginner.repositories.ProductRepository;
import br.com.matheusvictor.springbeginner.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest
public class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductModel product1, product2, product3;

    @BeforeEach
    public void setup() {
        product1 = new ProductModel();
        product1.setId(UUID.randomUUID());
        product1.setName("Product 1");
        product1.setDescription("Product 1");
        product1.setPrice(new BigDecimal("10.0"));

        product2 = new ProductModel();
        product2.setId(UUID.randomUUID());
        product2.setName("Product 2");
        product2.setDescription("Product 2");

        var products = Arrays.asList(product1, product2);
    }

    @Test
    @DisplayName("Should return all products")
    public void testGetAllProducts() {
        when(productService.getAllProducts())
                .thenReturn(Arrays.asList(product1, product2));

        var products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertEquals(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Arrays.asList(product1, product2)),
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(products)
        );
    }

    @Test
    @DisplayName("Should create a new product")
    public void testCreateProductSuccessfully() {
        when(productService.createProduct(product1))
                .thenReturn(product1);

        when(productRepository.existsByNameAndDescription(product1.getName(), product1.getDescription()))
                .thenReturn(false);

        when(productRepository.save(product1))
                .thenReturn(product1);

        ProductModel newProduct = productService.createProduct(product1);
        assertEquals(product1, newProduct);
        assertEquals(
                ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(product1),
                ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(newProduct)
        );
    }

    @Test
    @DisplayName("Should throw exception when try create a new product with a name and description existing in another product")
    public void testCreateProductWithExistingDescription() {

        product3 = new ProductModel();
        product3.setId(UUID.randomUUID());
        product3.setName("Product 1");
        product3.setDescription("Product 1");

        when(productService.createProduct(product3))
                .thenThrow(new ProductExistsException());

        when(productRepository.existsByNameAndDescription(product3.getName(), product3.getDescription()))
                .thenReturn(true);

        when(productService.getAllProducts())
                .thenReturn(Arrays.asList(product1, product2));

        var products = productService.getAllProducts();

        assertThrows(ProductExistsException.class, () -> productService.createProduct(product3));
        assertEquals(
                ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ExceptionsMessages.PRODUCT_EXISTS.getMessage()),
                ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ExceptionsMessages.PRODUCT_EXISTS.getMessage())
        );
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductByIdSuccessfully() {
        when(productService.getProductById(product1.getId()))
                .thenReturn(Optional.of(product1));

        when(productService.getAllProducts())
                .thenReturn(Arrays.asList(product1, product2));

        var productOptional = productService.getProductById(product1.getId());
        productOptional.ifPresent(productModel -> productModel.add(
                linkTo(
                        methodOn(ProductController.class).getAllProducts()
                ).withRel("All Products")
        ));

        assertEquals(product1, productOptional.get());
        assertEquals(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(product1),
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(productOptional.get())
        );
    }

    @Test
    public void testGetNotFoundProductById() {
        when(productService.getProductById(UUID.randomUUID()))
                .thenReturn(Optional.empty());

        var emptyOptional = productService.getProductById(UUID.randomUUID());

        assertEquals(Optional.empty(), emptyOptional);
        assertEquals(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ExceptionsMessages.PRODUCT_NOT_FOUND.getMessage()),
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ExceptionsMessages.PRODUCT_NOT_FOUND.getMessage())
        );
    }

    @Test
    public void testUpdateProductSuccessfully() {
        ProductModel productToUpdateModel = new ProductModel();

        when(productService.getProductById(product1.getId()))
                .thenReturn(Optional.of(product1));

        when(productService.createProduct(product1))
                .thenReturn(product1);

        when(productRepository.save(product1))
                .thenReturn(product1);

        var productRecordDto = new ProductRecordDto(
                "Update Product 1",
                "Update Product 1",
                new BigDecimal("20.0")
        );

        var optional = productService.getProductById(product1.getId());

        if (optional.isPresent()) {
            productToUpdateModel = optional.get();
            BeanUtils.copyProperties(productRecordDto, productToUpdateModel);
        }

        var updatedProduct = productService.createProduct(productToUpdateModel);

        assertEquals(product1, updatedProduct);
        assertEquals(productRecordDto.name(), updatedProduct.getName());
        assertEquals(productToUpdateModel.getName(), updatedProduct.getName());
        assertNotEquals("Product 1", updatedProduct.getName());
    }
}
