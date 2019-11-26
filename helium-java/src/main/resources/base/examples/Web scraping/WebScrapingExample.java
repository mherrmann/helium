/**
 * Helium can be used to extract data from websites. After reading the data it 
 * can be further processed using standard libraries. In the example below we
 * extract Helium's advantages listed on our homepage and write them into a csv
 * file.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
 
import static com.heliumhq.API.*;
 
public class WebScrapingExample {
    public static void main(String[] args) throws IOException {
        startChrome("heliumhq.com");
 
        List<$> advantageNames = findAll($("dt", below("Advantages")));
        List<$> advantages = findAll($("dd", below("Advantages")));
 
        assert advantageNames.size() == advantages.size();
 
        FileWriter writer = new FileWriter("helium_advantages.csv");
        String eol = System.getProperty("line.separator");
        writer.append("Advantage Name;Advantage Description" + eol);
        for (int i = 0; i < advantages.size(); i++) {
            writer.append(advantageNames.get(i).getWebElement().getText());
            writer.append(';');
            writer.append(advantages.get(i).getWebElement().getText());
            writer.append(eol);
        }
        writer.flush();
        writer.close();
 
        killBrowser();
    }
}