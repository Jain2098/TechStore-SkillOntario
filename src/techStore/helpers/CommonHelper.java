package techStore.helpers;

import techStore.logger.AppLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CommonHelper {

    public static String extract(String json, String key) {
        String pattern = "\"" + key + "\":";
        int index = json.indexOf(pattern);
        if (index == -1) return null;

        index += pattern.length();      // this makes pointer to reach after like "name":HERE

        // make sure we are at first character
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }

        StringBuilder value = new StringBuilder();
        // checking if its String then we need to move to one more pointer
        boolean isString = json.charAt(index) == '"';
        if (isString) index++;

        while (index < json.length()) {
            char c = json.charAt(index);
            if (isString && c == '"') break;
            if (!isString && (c == ',' || c == '}')) break;
            value.append(c);
            index++;
        }

        return value.toString().trim();
    }

    // <T> means it will deal with Generic Type and even if return void it needed.
    // Function<String, Optional<T>>    :   Generic function which pass String as arguments and return as Option<T> type.
    public static <T> List<T> readJsonFile(String fileName, Function<String, Optional<T>> parser) {
        List<T> resultList = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("[]");
            } catch (IOException e) {
                AppLogger.error(e.getMessage(), e);
            }
            return resultList;
        }

        StringBuilder jsonRaw = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line = reader.readLine();
            while(line != null) {
                jsonRaw.append(line.trim());
                line = reader.readLine();
            }
        } catch (IOException e) {
            AppLogger.error(e.getMessage(), e);
            return resultList;
        }

        String jsonContent = jsonRaw.toString();
        if (jsonContent.startsWith("[") && jsonContent.endsWith("]")){
            jsonContent = jsonContent.substring(1, jsonContent.length()-1).trim();
        }

        if (jsonContent.isBlank()) return resultList;

        String[] items = jsonContent.split("},\\s*\\{");

        for (String item: items) {
            item = item.trim();
            if(!item.startsWith("{")) item = "{" + item;
            if (!item.endsWith("}")) item = item + "}";

            parser.apply(item).ifPresent(resultList::add);
        }
        return resultList;
    }

}
