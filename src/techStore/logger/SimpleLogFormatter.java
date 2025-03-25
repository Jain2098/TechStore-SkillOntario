package techStore.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record){
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(record.getMillis()));

        String source = record.getSourceClassName();
        String simpleClass;
        if (source == null || !source.contains(".")){
            simpleClass = (source!=null) ? source : "UnKnownSource";
        } else {
            simpleClass = source.substring(source.lastIndexOf('.') + 1);
        }

        // String method = record.getSourceMethodName();
        // %n is crossPlatform compatible instead of \n.
        return String.format("%s : %s%n", date, record.getMessage());
    }
}
