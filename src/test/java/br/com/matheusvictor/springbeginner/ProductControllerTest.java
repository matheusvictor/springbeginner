package br.com.matheusvictor.springbeginner;

import br.com.matheusvictor.springbeginner.exceptions.ProductExistsException;
import br.com.matheusvictor.springbeginner.models.ProductModel;
import br.com.matheusvictor.springbeginner.repositories.ProductRepository;
import br.com.matheusvictor.springbeginner.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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

        List<ProductModel> products = Arrays.asList(product1, product2);
    }

    @Test
    @DisplayName("Should return all products")
    public void testGetAllProducts() {
        when(productRepository.findAll())
                .thenReturn(Arrays.asList(product1, product2));

        List<ProductModel> products = productService.getAllProducts();
        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("Should throw exception when try create a new product with an existing description")
    public void testCreateProductWithExistingDescription() {
        when(productRepository.findAll())
                .thenReturn(Arrays.asList(product1, product2));

        when(productRepository.existsByNameAndDescription(product1.getName(), product1.getDescription()))
                .thenReturn(true);

        product3 = new ProductModel();
        product3.setId(UUID.randomUUID());
        product3.setName("Product 1");
        product3.setDescription("Product 1");

        List<ProductModel> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertThrows(ProductExistsException.class, () -> productService.createProduct(product3));
    }
}
