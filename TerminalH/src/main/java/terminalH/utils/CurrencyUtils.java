package terminalH.utils;

import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

@Named
public class CurrencyUtils {
    public static Optional<Float> parsePrice(String price) {
        try {
            return Optional.of(NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue());
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public static String removeCurrencySymbol(String price) {
        return price.replace("₪", "");
    }
}