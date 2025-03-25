package techStore.helpers;

import techStore.Constants;
import techStore.logger.AppLogger;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

enum Counter {
    PRODUCT,
    SALES
}

// since as per requirements: "ID: An auto-generated, zero-padded 8-digit number".
public class IdGenerator {
    // we only store the last id used in the file, so even the program restarts it will keep track of the id's.
    // private static final String COUNTER_FILE = "counter.txt";
    // using AtomicInteger make sure it is not affected by multiple threads as ThreadSafe.
    private static final AtomicInteger product_counter = new AtomicInteger(loadLastId(Counter.PRODUCT));
    private static final AtomicInteger sale_counter = new AtomicInteger(loadLastId(Counter.SALES));

    private static String getFileName(Counter counter) {
        if (counter == Counter.PRODUCT) {
            return Constants.PRODUCT_COUNTER_FILE;
        } else if (counter == Counter.SALES) {
            return Constants.SALES_COUNTER_FILE;
        }
        return null;
    }

    // load the last id from the file
    private static int loadLastId(Counter counter) {
        String fileName = getFileName(counter);
        if (fileName == null) throw new RuntimeException("INVALID TYPE.");

        FileReader file = null;
        BufferedReader reader = null;
        try {
            file = new FileReader(fileName);
            reader = new BufferedReader(file);
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            // file not found or invalid number start with 1
            AppLogger.error("file not found or invalid number | starting with 1", e);
            return 1;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                } else if (file != null) {
                    file.close();
                }
            } catch (Exception e) {
                AppLogger.error(e.getMessage(), e);
            }
        }
    }

    public static void saveCurrentId(int id, Counter counter) {
        String fileName = getFileName(counter);
        if (fileName == null) throw new RuntimeException("INVALID TYPE.");

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(String.valueOf(id));
        } catch (IOException e) {
            AppLogger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                AppLogger.error(e.getMessage(), e);
            }
        }

    }

    public static synchronized int generateProductId() {
        int id = product_counter.getAndIncrement();
        saveCurrentId(product_counter.get(), Counter.PRODUCT);
        return id;
    }

    public static String generateProductPaddedId() {
        return String.format("%08d", generateProductId());
    }


    public static synchronized int generateSalesId() {
        int id = sale_counter.getAndIncrement();
        saveCurrentId(sale_counter.get(), Counter.SALES);
        return id;
    }

    public static String generateSalesPaddedId() {
        return String.format("%08d", generateSalesId());
    }


}
