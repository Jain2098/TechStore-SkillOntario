package techStore.repository;

import techStore.Constants;
import techStore.logger.AppLogger;
import techStore.models.Sale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static techStore.helpers.CommonHelper.extract;
import static techStore.helpers.CommonHelper.readJsonFile;

public class SaleRepo {
    private final String SALES_FILE_PATH = Constants.SALES_FILE_PATH;

    private Optional<Sale> parseSaleFromJson(String json) {
        try {
            String id = extract(json, "id");
            String dateTime_Raw = extract(json, "dateTime");
            String productId = extract(json, "productId");
            String quantity_Raw = extract(json, "quantity");
            String price_Raw = extract(json, "price");

            // boolean hasNull = Stream.of(id, dateTime_Raw, productId, quantity_Raw, price_Raw).anyMatch(s -> s == null || s.isBlank());
            // if (hasNull) return Optional.empty();

            if (id == null || id.isBlank() ||
                    dateTime_Raw == null || dateTime_Raw.isBlank() ||
                    productId == null || productId.isBlank() ||
                    quantity_Raw == null || quantity_Raw.isBlank() ||
                    price_Raw == null || price_Raw.isBlank()) {
                return Optional.empty();
            }

            ZonedDateTime dateTime = ZonedDateTime.parse(dateTime_Raw);
            int quantity = Integer.parseInt(quantity_Raw);
            BigDecimal price = new BigDecimal(price_Raw);

            Sale sale = new Sale(id, dateTime, productId, quantity, price);
            return Optional.of(sale);
        } catch (Exception e) {
            AppLogger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<Sale> getAllSales() {
        Function<String, Optional<Sale>> parser = (String json) -> parseSaleFromJson(json);
        return readJsonFile(SALES_FILE_PATH, parser);
    }

    private void saveAllSales(List<Sale> allSales) {
        File file = new File(SALES_FILE_PATH);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("[\n");
            for (int i = 0; i < allSales.size(); i++) {
                Sale s = allSales.get(i);
                writer.write("{\n");
                writer.write("\"id\":\"" + s.getId() + "\",\n");
                writer.write("\"dateTime\":\"" + s.getDateTime() + "\",\n");
                writer.write("\"productId\":\"" + s.getProductId() + "\",\n");
                writer.write("\"quantity\":" + s.getQuantity() + ",\n");
                writer.write("\"price\":\"" + s.getPrice() + "\"\n");
                writer.write("}");

                if (i < allSales.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]");

        } catch (IOException e) {
            AppLogger.error(e.getMessage(), e);
        }
    }

    public Optional<Sale> processNewSale(String productId, int quantity, BigDecimal price) {
        Sale newSale = new Sale(productId, quantity, price);
        List<Sale> allSales = getAllSales();
        allSales.add(newSale);
        saveAllSales(allSales);
        return Optional.of(newSale);
    }


}
