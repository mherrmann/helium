/**
 * The following code uses Helium to implement a bot for the popular 
 * Likes-trading platform AddMeFast:
 */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
 
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
 
import static com.heliumhq.API.*;
 
public class AddMeFastBot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("AddMeFast username:");
        String addmefastUser = sc.nextLine();
        System.out.println("AddMeFast password:");
        String addmefastPw = sc.nextLine();
        System.out.println("Twitter username:");
        String twitterUser = sc.nextLine();
        System.out.println("Twitter password:");
        String twitterPw = sc.nextLine();

        startChrome();
 
        // First, ensure we're logged into Twitter:
        goTo("twitter.com");
        write(twitterUser, into("Username or email"));
        write(twitterPw, into("Password"));
        click("Sign in");
 
        // Log in to AddMeFast:
        goTo("addmefast.com");
        write(addmefastUser, into("Email"));
        write(addmefastPw, into("Password"));
        click("Login");
 
        // Now follow as many pages as possible:
        goTo("http://addmefast.com/free_points/twitter");
        Config.setImplicitWaitSecs(10);
        while (true) {
            try {
                final String pageTitle = getPageTitle();
                click("Follow");
                boolean alreadyFollowing = Button("Following").exists();
                boolean pageDoesNotExist = Text("Sorry, that page").exists();
                if (alreadyFollowing || pageDoesNotExist) {
                    getDriver().close();
                    click("Skip");
                } else {
                    List<Button> followBtns = findAll(Button("Follow"));
                    Collections.sort(followBtns, new Comparator<Button>() {
                        @Override
                        public int compare(Button b1, Button b2) {
                            return b1.getY() - b2.getY();
                        }
                    });
                    Button topmostFollowBtn = followBtns.get(0);
                    click(topmostFollowBtn);
                    getDriver().close();
                }
                ExpectedCondition<Boolean> pageTitleChanged = 
                    new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver input) {
                            return ! getPageTitle().equals(pageTitle);
                        }
                    };
                waitUntil(pageTitleChanged);
            } catch (Exception e) {
                System.out.println("Got " + e + ". Restarting...");
                goTo("http://addmefast.com/free_points/twitter");
            }
        }
    }
    private static String getPageTitle() {
        return $(".fb_page_title").getWebElement().getText();
    }
}