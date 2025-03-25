package techStore.main;

import techStore.models.Product;
import techStore.models.Sale;
import techStore.response.SaleResponse;
import techStore.service.ProductService;
import techStore.service.SaleService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private final ProductService productService;
    private final SaleService saleService;
    private final Scanner sc;


    public Main() {
        productService = new ProductService();
        saleService = new SaleService(productService);
        sc = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("-".repeat(64));
        System.out.println("=== Welcome to TechStore Inventory & Sales Management System ===");
        System.out.println("-".repeat(64) + "\n");
        while (true) {
            mainMenu();
            int choice = menuChoice(0, 9);
            handleChoices(choice);
        }
    }

    private void mainMenu() {
        // System.out.println("-".repeat(64));
        System.out.println("-".repeat(13));
        System.out.println("| MAIN MENU |");
        System.out.println("-".repeat(13));
        System.out.println("PRODUCTS:");
        System.out.println("1. GET All Products");
        System.out.println("2. GET Single Product");
        System.out.println("3. ADD New Product");
        System.out.println("4. UPDATE Product");
        System.out.println("5. DELETE Product");
        System.out.println("\nSALES:");
        System.out.println("6. CREATE Sale");
        System.out.println("\nSUMMARY:");
        System.out.println("7. View Sales Report");
        System.out.println("8. Low Stock Products");
        System.out.println("0. EXIT");
    }

    private int menuChoice(int min, int max) {
        int choice;
        while (true) {
            try {
                System.out.print("Enter Your Input: ");
                choice = sc.nextInt();
                // if (choice >= min && choice < max) break;
                if (choice >= min && choice <= max) break;
                System.out.println("!!Incorrect Input Data. Try Again !!");
            } catch (Exception e) {
                System.out.println("!!Incorrect Input Data. Try Again !!");
            } finally {
                sc.nextLine();
            }
        }
        return choice;
    }

    private void handleChoices(int choice) {
        switch (choice) {
            case 1 -> getAllProducts();
            case 2 -> getSingleProduct();
            case 3 -> addNewProduct();
            case 4 -> updateProduct();
            case 5 -> deleteProduct();
            case 6 -> createSale();
            case 7 -> viewSalesReport();
            case 8 -> viewLowStockItems();

            case 0 -> {
                System.out.println("Exiting..");
                System.exit(0);
            }
            default -> System.out.println("I am Lost..");
        }
    }

    private void endCommonDisplay() {
        System.out.println("\n0. Exit\n1. MainMenu\n" + "-".repeat(15));
        int choice = menuChoice(0, 1);
        if (choice == 0) {
            System.out.println("Exiting..");
            System.exit(0);
        }
    }

    private String convertUtcToLocal(ZonedDateTime utcTime) {
        ZonedDateTime localZoned = utcTime.withZoneSameInstant(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return localZoned.format(formatter);
    }

    // START PRODUCT DISPLAY WRAPPER //
    private void displayProductWrapper(Product product) {
        System.out.println("-".repeat(120));
        System.out.printf("%-10s %-20s %-20s %-10s %-10s %-20s %-10s%n",
                "ID", "Name", "Category", "Price", "Qty", "Supplier", "Discount");
        displayProduct(product);
        System.out.println("-".repeat(120));
    }

    private void displayProductWrapper(List<Product> products) {
        System.out.println("-".repeat(110));
        System.out.printf("%-10s %-20s %-20s %-10s %-10s %-20s %-10s%n",
                "ID", "Name", "Category", "Price", "Qty", "Supplier", "Discount");
        System.out.println("-".repeat(110));

        for (Product p : products) {
            displayProduct(p);
        }
    }

    private void displayProduct(Product p) {
        System.out.printf("%-10s %-20.20s %-20.20s %-10s %-10d %-20.20s %-10s%n",
                p.getId(),
                p.getName(),
                p.getCategory(),
                "$" + p.getPrice(),
                p.getQuantity(),
                p.getSupplier(),
                (p.getDiscount() == null ? "-" : p.getDiscount() + "%")
        );
    }
    // END PRODUCT WRAPPER //

    // START SALES DISPLAY WRAPPER //
    private void displaySaleWrapper(List<Sale> sales, double totalRevenue) {
        System.out.println("-".repeat(80));
        System.out.printf("%-10s %-20s %-10s %-10s %-10s %-10s%n",
                "ID", "DateTime", "ProductID", "Quantity", "Price", "Total");
        System.out.println("-".repeat(80));

        for (Sale sale : sales) {
            displaySale(sale);
            totalRevenue += sale.getPrice().doubleValue() * sale.getQuantity();
        }

    }

    private void displaySaleWrapper(Sale sale) {
        System.out.println("-".repeat(100));
        System.out.printf("%-10s %-20s %-10s %-10s %-10s %-10s%n",
                "ID", "DateTime", "ProductID", "Quantity", "Price", "Total");
        displaySale(sale);
        System.out.println("-".repeat(100));
    }

    private void displaySale(Sale s) {
        System.out.printf("%-10s %-20s %-10s %-10d %-10s %-10s%n",
                s.getId(),
                convertUtcToLocal(s.getDateTime()),
                s.getProductId(),
                s.getQuantity(),
                "$" + s.getPrice(),
                "$" + s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity()))
        );
    }
    // END SALES WRAPPER //

    private String formatProductId(String id) {
        if (!id.matches("\\d{1,8}")) {
            return null;
        }

        int len = id.length();
        if (len < 8) {
            return "0".repeat(8 - len) + id;
        }
        return id;
    }

    private Optional<Product> getProductByID() {
        while (true) {

            System.out.print("Enter Product ID (or 0 to Main Menu, q to Exit): ");
            String id = sc.nextLine().trim();

            if (id.isBlank()) {
                System.out.println("!! Input cannot be Blank !!");
                continue;
            }

            if (id.equals("0")) return Optional.empty();

            if (id.equalsIgnoreCase("q")) {
                System.out.println("Exiting..");
                System.exit(0);
            }

            String formattedId = formatProductId(id);
            if (formattedId == null) {
                System.out.println("!! Invalid Product ID format !!");
                continue;
            }
            Optional<Product> product = productService.getProduct(formattedId);

            if (product.isEmpty()) {
                System.out.println("!! Product not found with ID: " + formattedId + " !!");
                continue;
            }

            return product;
        }

    }

    // ------- START INPUT HANDLERS ------- //
    private String getStringInput(String title, String defaultValue) {
        while (true) {
            System.out.print(title);
            String input = sc.nextLine().trim();

            if (input.equals("0")) return null; // user cancels
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting..");
                System.exit(0);
            }

            // If default value exists, and input is blank, return default
            if (defaultValue != null && input.isBlank()) return defaultValue;

            // If input is not blank, return input
            if (!input.isBlank()) return input;

            // No default, and blank input → show warning
            System.out.println("!! Input cannot be blank !!");
        }
    }

    private BigDecimal getBigDecimalInput(String title, BigDecimal defaultValue) {
        while (true) {
            System.out.print(title);
            String input = sc.nextLine().trim();

            if (input.equals("0")) return null;
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting..");
                System.exit(0);
            }

            if (input.isBlank() && defaultValue != null) return defaultValue;

            if (input.isBlank()) {
                System.out.println("!! Input cannot be blank !!");
                continue;
            }

            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("!! Price cannot be negative !!");
                    continue;
                }
                return BigDecimal.valueOf(value);
            } catch (NumberFormatException e) {
                System.out.println("!! Invalid price format !!");
            }
        }
    }

    private Integer getIntInput(String title, Integer defaultValue) {
        while (true) {
            System.out.print(title);
            String input = sc.nextLine().trim();

            if (input.equals("0")) return null;
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting..");
                System.exit(0);
            }

            if (input.isBlank() && defaultValue != null) return defaultValue;

            if (input.isBlank()) {
                System.out.println("!! Input cannot be blank !!");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("!! Quantity cannot be negative !!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("!! Invalid quantity format !!");
            }
        }
    }

    private Integer getDiscountInput(String title, Integer defaultDiscount) {
        while (true) {
            System.out.print(title);
            String input = sc.nextLine().trim();

            if (input.equals("0")) return null; // go to main menu
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting..");
                System.exit(0);
            }

            if (input.isBlank()) return defaultDiscount;

            if (input.equalsIgnoreCase("none") || input.equalsIgnoreCase("null")) {
                return 0; // remove discount
            }

            try {
                int value = Integer.parseInt(input);
                if (value < 0 || value > 100) {
                    System.out.println("!! Discount must be between 0 and 100 !!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("!! Invalid discount format !!");
            }
        }
    }
    // ------- END INPUT HANDLERS ------- //

    // DISPLAY ALL PRODUCTS //
    private void getAllProducts() {
        Optional<List<Product>> allProductsRaw = productService.getAllProducts();

        if (allProductsRaw.isEmpty()) {
            System.out.println("Error retrieving the products..");
        } else {
            List<Product> allProducts = allProductsRaw.get();
            if (allProducts.isEmpty()) {
                System.out.println("No products found");
            } else {
                displayProductWrapper(allProducts);
            }
        }

        endCommonDisplay();
    }

    // DISPLAY SINGLE PRODUCT //
    private void getSingleProduct() {
        while (true) {
            Optional<Product> productOpt = getProductByID();
            if (productOpt.isEmpty()) break;
            displayProductWrapper(productOpt.get());
        }
    }

    // DISPLAY ADD NEW PRODUCT //
    private void addNewProduct() {
        System.out.println("-".repeat(20));
        System.out.println("ADD NEW PRODUCT");
        System.out.println("-".repeat(20));

        String name, category, supplier;
        BigDecimal price;
        Integer quantity;
        Integer discount;

        System.out.println("'0' to Main Menu, 'q' to Exit at any time.");

        while (true) {
            name = getStringInput("Enter Name: ", null);
            if (name == null) return;

            category = getStringInput("Enter Category: ", null);
            if (category == null) return;

            price = getBigDecimalInput("Enter Price: ", null);
            if (price == null) return;

            quantity = getIntInput("Enter Quantity: ", null);
            if (quantity == null) return;

            supplier = getStringInput("Enter Supplier: ", null);
            if (supplier == null) return;

            discount = getDiscountInput("Enter Discount %: ", 0);
            if (discount == null) return;

            Optional<Product> product = productService.addProduct(name, category, price, quantity, supplier, discount);
            if (product.isPresent()) {
                System.out.println("Product added successfully!");
                displayProductWrapper(product.get());
                break;
            } else {
                System.out.println("!! Failed to add product !!");
                System.out.print("Do you want to try again? (y/n): ");
                String retry = sc.nextLine().trim().toLowerCase();
                if (!retry.equals("y")) break;
            }
        }
        endCommonDisplay();
    }

    // DISPLAY UPDATE PRODUCT //
    private void updateProduct() {
        System.out.println("-".repeat(20));
        System.out.println("UPDATE PRODUCT");
        System.out.println("-".repeat(20));

        while (true) {
            Optional<Product> productOpt = getProductByID();
            System.out.println("Current Product Details:");
            if (productOpt.isEmpty()) return;

            Product product = productOpt.get();
            displayProductWrapper(product);

            System.out.println("'0' to Main Menu, 'q' to Exit at any time.");
            System.out.println("Enter new details (leave BLANK to keep current values):");

            String name = getStringInput("Name [" + product.getName() + "]: ", product.getName());
            if (name.equals("0")) return;

            String category = getStringInput("Category [" + product.getCategory() + "]: ", product.getCategory());
            if (category.equals("0")) return;

            BigDecimal price = getBigDecimalInput("Price [$" + product.getPrice() + "]: ", product.getPrice());
            if (price == null) return;

            Integer quantity = getIntInput("Quantity [" + product.getQuantity() + "]: ", product.getQuantity());
            if (quantity == null) return;

            String supplier = getStringInput("Supplier [" + product.getSupplier() + "]: ", product.getSupplier());
            if (supplier.equals("0")) return;


            Integer discount = getDiscountInput(
                    "Discount % [" + (product.getDiscount() == null ? "No discount" : product.getDiscount() + "%") + "] (or 'none' for no discount, 0 to Main Menu, q to Exit): ",
                    product.getDiscount()
            );

            if (discount == null && product.getDiscount() == null) return;

            System.out.print("Confirm update? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) return;

            Optional<Product> updatedProduct = productService.updateProduct(
                    product.getId(), name, category, price, quantity, supplier, discount
            );

            if (updatedProduct.isPresent()) {
                System.out.println("✓ Product updated successfully!");
                displayProductWrapper(updatedProduct.get());
                break;
            } else {
                System.out.println("!! Failed to update product !!");
                System.out.print("Do you want to try again? (y/n): ");
                String retry = sc.nextLine().trim().toLowerCase();
                if (!retry.equals("y")) break;
            }
        }
        endCommonDisplay();
    }

    // DISPLAY DELETE PRODUCT //
    private void deleteProduct() {
        System.out.println("-".repeat(20));
        System.out.println("DELETE PRODUCT");
        System.out.println("-".repeat(20));

        while (true) {
            Optional<Product> productOpt = getProductByID();
            if (productOpt.isEmpty()) break;
            Product product = productOpt.get();
            displayProductWrapper(product);

            System.out.print("Are you sure you want to delete this product? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                boolean deleted = productService.deleteProduct(product.getId());
                if (deleted) {
                    System.out.println("Product deleted successfully!");
                    break;
                } else {
                    System.out.println("!! Failed to delete product !!");
                    System.out.print("Do you want to try again? (y/n): ");
                    String retry = sc.nextLine().trim().toLowerCase();
                    if (!retry.equals("y")) break;
                }
            } else {
                System.out.println("Product deletion cancelled.");
                break;
            }
        }

        endCommonDisplay();
    }

    // DISPLAY CREATE SALE //
    private void createSale() {
        System.out.println("-".repeat(20));
        System.out.println("CREATE SALE");
        System.out.println("-".repeat(20));

        while (true) {
            Optional<Product> productOpt = getProductByID();
            if (productOpt.isEmpty()) break;

            Product product = productOpt.get();
            displayProductWrapper(product);

            if (product.getQuantity() == 0) {
                System.out.println("!! Product is out of stock !!");
                System.out.print("Do you want to select another product? (y/n): ");
                String retry = sc.nextLine().trim().toLowerCase();
                if (!retry.equals("y")) break;
                continue;
            }

            int quantity = 0;
            boolean validQuantity = false;

            while (!validQuantity) {
                System.out.print("Enter Quantity to sell (available: " + product.getQuantity() + ") (or 0 to Main Menu, q to Exit): ");
                String quantityStr = sc.nextLine().trim();

                if (quantityStr.equals("0")) return;
                if (quantityStr.equalsIgnoreCase("q")) {
                    System.out.println("Exiting..");
                    System.exit(0);
                }

                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        System.out.println("!! Quantity must be positive !!");
                        continue;
                    }
                    if (quantity > product.getQuantity()) {
                        System.out.println("!! Insufficient stock! Available: " + product.getQuantity() + " !!");
                        continue;
                    }
                    validQuantity = true;
                } catch (NumberFormatException e) {
                    System.out.println("!! Invalid quantity format !!");
                }
            }

            SaleResponse response = saleService.createSale(product.getId(), quantity);
            if (response.isSuccess()) {
                Sale sale = response.getSale();
                System.out.println("Sale created successfully!");
                displaySaleWrapper(sale);
                System.out.print("Do you want to create another sale? (y/n): ");
            } else {
                System.out.println("!! Failed to create sale: " + response.getMessage() + " !!");
                System.out.print("Do you want to try again? (y/n): ");
            }
            String retry = sc.nextLine().trim().toLowerCase();
            if (!retry.equals("y")) break;
        }
    }

    // DISPLAY REPORT: SALES//
    private void viewSalesReport() {
        System.out.println("-".repeat(20));
        System.out.println("SALES REPORT");
        System.out.println("-".repeat(20));
        while (true) {
            System.out.println("Select time range:");
            System.out.println("1. Today");
            System.out.println("2. This Week");
            System.out.println("3. This Month");
            System.out.println("4. Custom Range");
            System.out.println("0. Back to Main Menu");

            int choice = menuChoice(0, 4);
            if (choice == 0) return;

            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now();

            switch (choice) {
                case 1 -> {
                    // Today
                    start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                    end = LocalDateTime.now();
                }
                case 2 -> {
                    // This Week
                    start = LocalDateTime.now().minusDays(7);
                    end = LocalDateTime.now();
                }
                case 3 -> {
                    // This Month
                    start = LocalDateTime.now().minusDays(30);
                    end = LocalDateTime.now();
                }
                case 4 -> {
                    // Custom Range
                    boolean validDates = false;
                    while (!validDates) {
                        try {
                            System.out.println("Enter start date (YYYY-MM-DD) (or 0 to Main Menu, q to Exit):");
                            String startDateStr = sc.nextLine().trim();

                            if (startDateStr.equals("0")) return;
                            if (startDateStr.equalsIgnoreCase("q")) {
                                System.out.println("Exiting..");
                                System.exit(0);
                            }

                            System.out.println("Enter end date (YYYY-MM-DD) (or 0 to Main Menu, q to Exit):");
                            String endDateStr = sc.nextLine().trim();

                            if (endDateStr.equals("0")) return;
                            if (endDateStr.equalsIgnoreCase("q")) {
                                System.out.println("Exiting..");
                                System.exit(0);
                            }

                            start = LocalDateTime.parse(startDateStr + "T00:00:00");
                            end = LocalDateTime.parse(endDateStr + "T23:59:59");

                            if (start.isAfter(end)) {
                                System.out.println("!! Start date cannot be after end date !!");
                                continue;
                            }
                            validDates = true;
                        } catch (Exception e) {
                            System.out.println("!! Invalid date format. Use YYYY-MM-DD format !!");
                        }
                    }
                }
            }

            List<Sale> sales = saleService.getSalesInRange(start, end);

            if (sales.isEmpty()) {
                System.out.println("No sales found in the selected time range.");
            } else {
                double totalRevenue = 0.0;
                displaySaleWrapper(sales, totalRevenue);

                System.out.println("-".repeat(80));
                System.out.println("Total Sales: " + sales.size());
                System.out.println("Total Revenue: $" + String.format("%.2f", totalRevenue));
                System.out.println("-".repeat(80));
            }

            System.out.print("Generate another report? (y/n): ");
            String again = sc.nextLine().trim().toLowerCase();
            if (!again.equals("y")) break;
        }

        // endCommonDisplay();
    }

    // DISPLAY REPORT: LOW STOCK ITEMS//
    private void viewLowStockItems() {
        System.out.println("-".repeat(45) + "  LOW STOCK ITEMS  " + "-".repeat(45));

        Optional<List<Product>> lowStockOpt = productService.lowStockReport();

        if (lowStockOpt.isEmpty()) {
            System.out.println("!! Error retrieving low stock products !!");
        } else {
            List<Product> lowStockProducts = lowStockOpt.get();
            if (lowStockProducts.isEmpty()) {
                System.out.println("No products with low stock (below threshold of " +
                        productService.getQuantityThreshold() + ").");
            } else {
                System.out.println("Products with stock below threshold of " +
                        productService.getQuantityThreshold() + ":");

                displayProductWrapper(lowStockProducts);

                System.out.println("-".repeat(110));
                System.out.println("Total low stock products: " + lowStockProducts.size());
            }
        }

        endCommonDisplay();
    }

}