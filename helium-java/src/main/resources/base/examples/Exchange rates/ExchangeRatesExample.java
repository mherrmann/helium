/**
 * In the script below we present how Helium can be used to automatically read
 * data from websites and make it available for further processing.
 */

import static com.heliumhq.API.*;
import org.openqa.selenium.WebDriver;
 
public class ExchangeRatesExample {
 
    public static void main(String[] args) {
        startFirefox();
        System.out.println(getExchangeRate("EUR", "USD"));
        System.out.println(getExchangeRate("USD", "JPY"));
        killBrowser();
    }
 
    private static String getExchangeRate(
        String baseCurrency, String counterCurrency
    ) {
        String currencyPair = baseCurrency + counterCurrency;
        goTo(String.format(
            "http://finance.yahoo.com/q?s=%s=X", currencyPair
        ));
        return $(".time_rtq_ticker").getWebElement().getText();
    }
 
}