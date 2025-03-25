package techStore.response;

import techStore.models.Sale;

public class SaleResponse {
    private boolean success;
    private String message;
    private Sale sale;

    public SaleResponse(boolean success, String message, Sale sale) {
        this.success = success;
        this.message = message;
        this.sale = sale;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Sale getSale() { return sale; }

    public static SaleResponse success(Sale sale) {
        return new SaleResponse(true, "Sale processed successfully.", sale);
    }

    public static SaleResponse failure(String message) {
        return new SaleResponse(false, message, null);
    }
}

