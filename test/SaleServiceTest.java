import org.junit.jupiter.api.*;
import techStore.models.Product;
import techStore.models.Sale;
import techStore.repository.SaleRepo;
import techStore.response.SaleResponse;
import techStore.service.ProductService;
import techStore.service.SaleService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaleServiceTest {
    private static SaleService saleService;
    private static String productId;
    private static ProductService productService;

    @BeforeAll
    static void setup() {
        productService = new ProductService();
        saleService = new SaleService(productService);
        // add a test product first
        Optional<Product> added = productService
                .addProduct("TestProduct", "TestCategory", BigDecimal.valueOf(50.00), 10, "TestSupplier", null);

        assertTrue(added.isPresent());
        productId = added.get().getId();
    }

    @Test
    @Order(1)
    public void testCreateSale_success() {
        SaleResponse response = saleService.createSale(productId, 2);
        assertTrue(response.isSuccess());
        assertNotNull(response.getSale());
        assertEquals(productId, response.getSale().getProductId());
    }

    @Test
    @Order(2)
    public void testCreateSale_invalidProductId() {
        SaleResponse response = saleService.createSale("123", 2);
        assertFalse(response.isSuccess());
        assertEquals("Invalid Arguments Data.", response.getMessage());
    }

    @Test
    @Order(3)
    public void testCreateSale_productNotFound() {
        SaleResponse response = saleService.createSale("00000099", 2);
        assertFalse(response.isSuccess());
        assertEquals("Product not Found", response.getMessage());
    }

    @Test
    @Order(4)
    public void testCreateSale_quantityTooHigh() {
        SaleResponse response = saleService.createSale(productId, 999);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().startsWith("In-Stock:"));
    }

    @Test
    @Order(5)
    public void testGetSalesInRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<Sale> sales = saleService.getSalesInRange(start, end);
        assertNotNull(sales);
        assertFalse(sales.isEmpty());

        // Ensure all returned sales are within range
        for (Sale sale : sales) {
            assertTrue(!sale.getDateTime().toLocalDateTime().isBefore(start));
            assertTrue(!sale.getDateTime().toLocalDateTime().isAfter(end));
        }
    }
}
