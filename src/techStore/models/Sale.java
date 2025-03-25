package techStore.models;

import techStore.helpers.IdGenerator;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Sale {
    private String id;
    private ZonedDateTime dateTime;
    private String productId;
    private int quantity;
    private BigDecimal price;

    public Sale(String productID, int quantity, BigDecimal price) {
        this.id = IdGenerator.generateSalesPaddedId();
        this.dateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        this.productId = productID;
        this.quantity = quantity;
        this.price = price;
    }

    public Sale(String id, ZonedDateTime dateTime, String productId, int quantity, BigDecimal price) {
        this.id = id;
        this.dateTime = dateTime;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
