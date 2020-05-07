package alerts;

import java.io.IOException;

/**
 * The framework to set up an alert. You can extend this if you want to create your own mechanism to send alerts!
 *
 * @author TheFuzzyFsh
 */
public abstract class alert {
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
