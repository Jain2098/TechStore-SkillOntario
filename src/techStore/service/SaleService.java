package techStore.service;

import techStore.models.Product;
import techStore.models.Sale;
import techStore.repository.SaleRepo;
import techStore.response.SaleResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SaleService {
    private final SaleRepo saleRepo;
    private final ProductService productService;

    public SaleService(ProductService productService) {
        this.productService = productService;
        this.saleRepo = new SaleRepo();
    }


    public List<Sale> getSalesInRange(LocalDateTime start, LocalDateTime end) {
        ZonedDateTime startUTC = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = end.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        return saleRepo.getAllSales().stream()
                .filter(sale -> {
                    ZonedDateTime saleTime = sale.getDateTime();
                    return !saleTime.isBefore(startUTC) && !saleTime.isAfter(endUTC);
                }).toList();
    }

    public SaleResponse createSale(String productId, int quantity) {
        if (quantity <= 0 || productId.length() < 8)
            return SaleResponse.failure("Invalid Arguments Data.");
        Optional<Product> getProduct = productService.getProduct(productId);
        if (getProduct.isEmpty()) return SaleResponse.failure("Product not Found");

        Product product = getProduct.get();

        int originalQuantity = product.getQuantity();
        if (quantity > originalQuantity) return SaleResponse.failure("In-Stock: " + originalQuantity);

        product.setQuantity(product.getQuantity() - quantity);
        Optional<Product> updateProduct = productService.updateProduct(product);
        if (updateProduct.isEmpty()) {
            return SaleResponse.failure("Failed in updating Product.");
        }

        Optional<Sale> sale = saleRepo.processNewSale(productId, quantity, product.getPrice());
        if (sale.isEmpty()) {
            // rollback
            product.setQuantity(originalQuantity);
            productService.updateProduct(product);
            return SaleResponse.failure("Failed to process sale.");
        }
        return SaleResponse.success(sale.get());
    }
}
