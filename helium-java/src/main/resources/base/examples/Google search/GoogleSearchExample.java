/**
 * This script uses Helium to automatically perform a Google search for the
 * term "Helium", and opens the Wikipedia article on the subject. If all goes 
 * well, it prints "Test passed". Otherwise, it prints "Test failed :(".
 */

import static com.heliumhq.API.*;

public class GoogleSearchExample {
	public static void main(String[] args) {
		startChrome();
		goTo("google.com/?hl=en");
		write("Helium");
		press(ENTER);
		click("Helium - Wikipedia");
		if (getDriver().getTitle().contains("Wikipedia"))
			System.out.println("Test passed!");
		else
			System.out.println("Test failed :(");
		killBrowser();
	}
}