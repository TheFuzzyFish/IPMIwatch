package alerts;

/**
 * The framework to set up an alert. You can implement this if you want to create your own mechanism to send alerts!
 */
public interface alert {
    public void setHeader(String header); // Used to set the header of the alert
    public void setBody(String body); // Used to set the body of the alert
    public void send(); // Used to actually send the alert, presumably interfacing with some API
}
