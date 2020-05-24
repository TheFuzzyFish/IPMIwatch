package alerts;

import java.io.IOException;

/**
 * So... you're looking to make an alert mechanism. Well, you're in the right place!
 * Extend this class with your alert and override the send() method, along with any other method you may want to implement.
 * You'll also want to add an identifier in the alertEnum.java file.
 * Finally, add some substring unique to your alert address in argHandler.classifyAlert(). Then tool around in the main switch case "--alert" of the argHandler and its embedded switch statement. Make a new case for your enumerator to create a new alert of your subclass.
 * Once you get it working, feel free to fork this repo, commit your changes, and submit a pull request with your feature, I'd be happy to integrate it into the official program.
 * Good luck!
 *
 * @author TheFuzzyFish
 */
public abstract class alertAbstract {
    protected String sender;
    protected String body;

    /**
     * Set the String to use as the origin of the alert. This could be a name, email address, server hostname, etc
     * @param sender the identification of the sender (this program)
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Set the body of the alert. This should be able to include the string literal "\n" to be parsed by send()
     * @param body the body of the alert
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Execute the alert-sending mechanism. This could use SMTP, Discord, IRC, or whatever other mode you want to implement
     * @throws RuntimeException if something doesn't work in the process of sending the alert
     */
    public void send() throws IOException {
        throw new RuntimeException("Dev didn't override send() method in alert");
    }
}
