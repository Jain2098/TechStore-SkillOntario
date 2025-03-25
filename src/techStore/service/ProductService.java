package techStore.service;

import techStore.logger.AppLogger;
import techStore.models.Product;
import techStore.repository.ProductRepo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ProductService {
    private final ProductRepo productRepo;
    private List<Product> cachedProducts;


    private static final Integer QUANTITY_THRESHOLD = 3;

    public ProductService() {
        this.productRepo = new ProductRepo();
        this.cachedProducts = productRepo.getAllProducts();
    }

    public int getQuantityThreshold() {
        return QUANTITY_THRESHOLD;
    }

    public void refreshProducts() {
        this.cachedProducts = productRepo.getAllProducts();
    }

    public Optional<List<Product>> getAllProducts() {
        if (cachedProducts.isEmpty()) refreshProducts();
        return Optional.of(cachedProducts);
    }

    public Optional<Product> getProduct(String id) {
        if (id.isBlank() || id.length() < 8) return Optional.empty();
        return productRepo.getProductById(cachedProducts, id);
    }

    public Optional<Product> addProduct(String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {
        if (name.isBlank() || category.isBlank() || supplier.isBlank() || price.compareTo(BigDecimal.ZERO) <= 0 || quantity < 0) {
            AppLogger.error("addProduct : Incomplete product data.");
            return Optional.empty();
        }
        Optional<Product> p = productRepo.addProduct(name, category, price, quantity, supplier, discount);
        p.ifPresent(cachedProducts::add);
        // System.out.println(cachedProducts);
        // p.ifPresent(product -> {
        //     cachedProducts.add(product);
        // });
        return p;
    }

    public Optional<Product> updateProduct(Product product){
        return productRepo.updateProduct(product);
    }

    public Optional<Product> updateProduct(String id, String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {
        if (id == null || id.isBlank() || name.isBlank() || category.isBlank() || supplier.isBlank() || price.compareTo(BigDecimal.ZERO) < 0) {
            AppLogger.error("updateProduct : Incomplete product data.");
            return Optional.empty();
        }
        Optional<Product> updatedProduct = productRepo.updateProduct(id, name, category, price, quantity, supplier, discount);
        if (updatedProduct.isPresent()) {
            for (int i = 0; i < cachedProducts.size(); i++) {
                if (cachedProducts.get(i).getId().equals(updatedProduct.get().getId())) {
                    cachedProducts.set(i, updatedProduct.get());
                }
            }
        }
        return updatedProduct;
    }

    public boolean deleteProduct(String id) {
        if (id == null || id.isBlank()) return false;
        boolean isDeleted = productRepo.deleteProduct(id);
        if (isDeleted) {
            cachedProducts.removeIf(p -> p.getId().equals(id));
        }
        return isDeleted;
    }

    public Optional<List<Product>> lowStockReport() {
        AppLogger.log("LOW STOCK REPORT CALLED", Level.INFO);
        List<Product> lowStockProducts = new ArrayList<>();

        for (Product p : cachedProducts) {
            if (p.getQuantity() < QUANTITY_THRESHOLD) {
                lowStockProducts.add(p);
                AppLogger.log("ID: " + p.getId() + "\tNAME: " + p.getName());
            }
        }


        return Optional.of(lowStockProducts);

    }

}
