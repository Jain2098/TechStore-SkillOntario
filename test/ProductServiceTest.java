import org.junit.jupiter.api.*;
import techStore.models.Product;
import techStore.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest {
    private static ProductService productService;

    @BeforeAll
    static void setup() {
        productService = new ProductService();
    }

    @Test
    @Order(1)
    public void testAddProduct_validData() {
        Optional<Product> result = productService.addProduct(
                "Keyboard", "Electronics", BigDecimal.valueOf(14.99), 20, "HP", null
        );
        assertTrue(result.isPresent());
        assertEquals("Keyboard", result.get().getName());
    }

    @Test
    @Order(2)
    public void testAddProduct_InvalidData() {
        Optional<Product> result = productService.addProduct(
                "Invalid", "Test", BigDecimal.valueOf(-1), 5, "TestSupplier", 5
        );

        assertTrue(result.isEmpty());
    }

    @Test
    @Order(3)
    public void testAddProduct_validData_WithDiscount() {
        Optional<Product> result = productService.addProduct(
                "Mouse", "Electronics", BigDecimal.valueOf(14.99), 20, "HP", 2
        );
        assertTrue(result.isPresent());
        assertEquals("Mouse", result.get().getName());
    }

    @Test
    @Order(4)
    public void testGetProduct_ExistingID_ShouldReturnProduct() {
        Product newProduct = productService.addProduct(
                "Printer",
                "Electronics",
                BigDecimal.valueOf(59.99),
                3,
                "HP",
                3
        ).get();

        Optional<Product> result = productService.getProduct(newProduct.getId());
        assertTrue(result.isPresent());
        assertEquals("Printer", result.get().getName());
    }

    @Test
    @Order(5)
    public void testGetProduct_ExistingID_ShouldFail() {
        Optional<Product> result = productService.getProduct("123");
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    public void testUpdateProduct_ShouldUpdateSuccessfully() {
        Product added = productService.addProduct("Monitor", "Electronics", BigDecimal.valueOf(199.99), 5, "Dell", 15).get();
        Optional<Product> result = productService.updateProduct(
                added.getId(), "Gaming Monitor", "Electronics", BigDecimal.valueOf(249.99), 2, "Dell", 10
        );

        assertTrue(result.isPresent());
        assertEquals("Gaming Monitor", result.get().getName());
        assertEquals(BigDecimal.valueOf(249.99), result.get().getPrice());
    }

    @Test
    @Order(7)
    public void testDeleteProduct_ShouldReturnTrue() {
        Product added = productService.addProduct("Temporary", "Test", BigDecimal.valueOf(10), 1, "Dummy", null).get();
        boolean deleted = productService.deleteProduct(added.getId());

        assertTrue(deleted);
        assertTrue(productService.getProduct(added.getId()).isEmpty());
    }

    @Test
    @Order(8)
    public void testLowStockProduct_containsLowStockItems() {
        Optional<Product> added = productService.addProduct("Laptop", "Electronics", BigDecimal.valueOf(599.99), 1, "HP", 15);
        assertTrue(added.isPresent(), "Product added Successfully");

        Optional<List<Product>> lowStockItems = productService.lowStockReport();
        assertTrue(lowStockItems.isPresent(), "It must have 1 min item.");

        lowStockItems.get().forEach((p) -> {
            assertTrue(
                    p.getQuantity() < productService.getQuantityThreshold(),
                    "Product: " + p.getId() + ", Quantity: " + p.getQuantity()
            );
        });


    }


}
