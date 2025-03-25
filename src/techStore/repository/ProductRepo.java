package techStore.repository;

import techStore.Constants;
import techStore.logger.AppLogger;
import techStore.models.Product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static techStore.helpers.CommonHelper.extract;
import static techStore.helpers.CommonHelper.readJsonFile;

public class ProductRepo {
    private final String PRODUCTS_FILE_PATH = Constants.PRODUCTS_FILE_PATH;

    // parse single product from JSON
    private Optional<Product> parseProductFromJson(String json) {
        try {
            String id = extract(json, "id");
            if (id == null || id.isBlank()) return Optional.empty();
            String name = Objects.requireNonNullElse(extract(json, "name"), "");
            String category = Objects.requireNonNullElse(extract(json, "category"), "");
            BigDecimal price = new BigDecimal(Objects.requireNonNullElse(extract(json, "price"), "0"));
            int quantity = Integer.parseInt(Objects.requireNonNullElse(extract(json, "quantity"), "0"));
            String supplier = Objects.requireNonNullElse(extract(json, "supplier"), "");
            String discountRaw = extract(json, "discount");
            Integer discount = (discountRaw == null || discountRaw.equals("null")) ? null : Integer.parseInt(discountRaw);

            // AppLogger.log("Parsed: id=" + id + ", name=" + name + ", price=" + price + ", qty=" + quantity);

            if (name.isBlank() || category.isBlank() || supplier.isBlank() || price.compareTo(BigDecimal.ZERO) < 0) {
                AppLogger.error("Invalid product data found while parsing: one or more required fields are missing or invalid.");
                return Optional.empty();
            }

            Product product = new Product(id, name, category, price, quantity, supplier, discount);
            return Optional.of(product);
        } catch (Exception e) {
            AppLogger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    // retrieve all Products
    public List<Product> getAllProducts() {
        /*
        List<Product> productList = new ArrayList<>();
        StringBuilder json = new StringBuilder();

        File file = new File(PRODUCTS_FILE_PATH);

        // create new file if not exist.
        // return empty List.
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("[]");
            } catch (IOException e) {
                AppLogger.error(e.getMessage(), e);
            }
            return productList;
        }

        // Read File Content
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                json.append(line.trim());
                line = reader.readLine();
            }
            // clean way
            // reader.lines().forEach(line -> json.append(line.trim()));

        } catch (IOException e) {
            AppLogger.error(e.getMessage(), e);
            return productList;
        }

        // Parse File Content
        String content = json.toString();

        // remove "[" and "]"
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1).trim();
        }

        if (content.isEmpty()) return productList;

        String[] productsRaw = content.split("},\\s*\\{");

        for (String p : productsRaw) {
            p = p.trim();
            if (!p.startsWith("{")) p = "{" + p;
            if (!p.endsWith("}")) p = p + "}";

            Optional<Product> product = parseProductFromJson(p);
            // productList.add(product);
            product.ifPresent(productList::add);
        }
        return productList;

         */
        // Function<String, Optional<Product>> parser = json -> parseProductFromJson(json);
        return readJsonFile(PRODUCTS_FILE_PATH, this::parseProductFromJson);
    }

    // update all products to json
    public void saveAllProducts(List<Product> products) {
        // open the file. create if not exist.
        File file = new File(PRODUCTS_FILE_PATH);

        // Not required since FileWriter creates file if not exist.
        /*
        if (!file.exists()) {
            try {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    AppLogger.error("FILE NOT CREATED.");
                    throw new RuntimeException("FILE NOT CREATED...");
                }
            } catch (IOException e) {
                AppLogger.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
         */

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("[\n");

            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);

                writer.write("{\n");
                writer.write("\"id\":\"" + p.getId() + "\",\n");
                writer.write("\"name\":\"" + p.getName() + "\",\n");
                writer.write("\"category\":\"" + p.getCategory() + "\",\n");
                writer.write("\"price\":" + p.getPrice() + ",\n");
                writer.write("\"quantity\":" + p.getQuantity() + ",\n");
                writer.write("\"supplier\":\"" + p.getSupplier() + "\",\n");
                writer.write("\"discount\":" + (p.getDiscount() != null ? p.getDiscount() : "null") + "\n");
                writer.write("}");

                if (i < products.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("]");

        } catch (IOException e) {
            AppLogger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    AppLogger.error(e.getMessage(), e);
                }
            }
        }


    }

    // single product
    public Optional<Product> getProductById(String id) {
        List<Product> allProducts = getAllProducts();

        for (Product p : allProducts) {
            if (p.getId().equals(id)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    // find single product when all product list given
    public Optional<Product> getProductById(List<Product> allProducts, String id) {
        for (Product p : allProducts) {
            if (p.getId().equals(id)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    // add product
    public Optional<Product> addProduct(String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {

        Product product = new Product(name, category, price, quantity, supplier, discount);

        List<Product> allExistingProducts = getAllProducts();

        AppLogger.log("ALL PRODUCT SIZE: " + allExistingProducts.size());
        allExistingProducts.forEach(p -> AppLogger.log("ID: " + p.getId() + "\tNAME: " + p.getName()));

        allExistingProducts.add(product);

        saveAllProducts(allExistingProducts);

        return Optional.of(product);
    }

    // update product
    public Optional<Product> updateProduct(String id, String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {
        List<Product> allProducts = getAllProducts();
        Optional<Product> filterProduct = allProducts.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
        if (filterProduct.isEmpty()) return Optional.empty();

        Product product = filterProduct.get();

        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setSupplier(supplier);
        product.setDiscount(discount);

        saveAllProducts(allProducts);

        return Optional.of(product);

    }

    public Optional<Product> updateProduct(Product product) {
        List<Product> allProducts = getAllProducts();
        String productID = product.getId();
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.getId().endsWith(productID)) {
                allProducts.set(i, product);
                break;
            }
        }
        saveAllProducts(allProducts);
        return Optional.of(product);
    }

    // delete product
    public boolean deleteProduct(String id) {
        List<Product> allProducts = getAllProducts();
        boolean isRemoved = allProducts.removeIf(p -> id.equals(p.getId()));
        if (isRemoved) saveAllProducts(allProducts);
        return isRemoved;
    }


}
