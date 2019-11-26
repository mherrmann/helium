/**
 * The following code shows how Helium can be used to automatically post a tweet
 * on Twitter:
 */

import static com.heliumhq.API.*;
import java.util.Scanner;
 
public class TwitterExample {
    private static String message = 
        "Trying web automation with #helium from @BugFreeSoftware. heliumhq.com";
 
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Twitter username:");
        String email = sc.nextLine();
        System.out.println("Twitter password:");
        String password = sc.nextLine();
        sc.close();
        startFirefox("twitter.com");
        write(email, into("Phone, email or username"));
        write(password, into("Password"));
        click("Log in");
        click("Tweet");
        write(message);
        click("Tweet");
        killBrowser();
    }
}