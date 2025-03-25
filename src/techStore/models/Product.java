package techStore.models;

import techStore.helpers.IdGenerator;

import java.math.BigDecimal;

public class Product {
    // id is string because we are it requires to be padded 8-digit number.
    String id;
    String name;
    String category;
    BigDecimal price;
    int quantity;
    String supplier;
    Integer discount;

    public Product(String id, String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
        this.discount = discount;
    }

    public Product(String id, String name, String category, BigDecimal price, int quantity, String supplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    public Product(String name, String category, BigDecimal price, int quantity, String supplier, Integer discount) {
        // 8 digit unique id
        this.id = IdGenerator.generateProductPaddedId();
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}
