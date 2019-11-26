/** 
 * This script presents how Helium can be used to automatically update your
 * Facebook status. When you run it, it first prompts you for your Facebook
 * username and password. Once you have entered this, Helium logs you in and
 * updates your status.
 */

import static com.heliumhq.API.*;
import java.util.Scanner;
 
public class FacebookExample {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Email address registered with Facebook:");
        String email = sc.nextLine();
        System.out.println("Password registered with Facebook:");
        String password = sc.nextLine();
        sc.close();
        startChrome("facebook.com");
        write(email, into("Email or Phone"));
        write(password, into("Password"));
        click("Log In");
        write("Test", into("Update Status"));
        click("Post");
    }
}