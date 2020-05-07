import alerts.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import static alerts.alertEnum.unknown;

/**
 * Handles user arguments from the main() thread.
 * This should be used to parse user arguments and be accessed to retrieve the options they specified. You should also make sure that the first thing you do in main() is to check this for doExit()
 *
 * @author TheFuzzyFish
 */
public class argHandler {
    private boolean doExit;
    private String version;
    private double fanThreshold;
    private boolean doMonFan;
    private double tempThreshold;
    private boolean doMonTemp;
    private boolean doAlert;
    private alertEnum alertType;
    private alertAbstract alert;
    private String logPath;
    private boolean doLog;

    /**
     * Constructs a new argHandler based on the argument array from main().
     *
     * @param args the arguments passed in from the command line through main()
     */
    public argHandler(String[] args) {
        doExit = true; // Flag is flipped per the arguments for whether or not main() should immediately exit based on context
        version = getAsset("version.txt"); // Not quite sure why I decided to do this, but I'm going to make argHandler in charge of the version number throughout the rest of the program

        /* If there were no arguments supplied, print the help file */
        if (args.length == 0) {
            printAsset("help.txt");
            return;
        }

        /* Loop through the arguments and detect valid flags */
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                default:
                    System.out.println("Unrecognized flag \"" + args[i] + "\". Exiting.");
                    break;
                case "-h":
                case "-H":
                case "--help":
                    printAsset("help.txt");
                    break;
                case "-v":
                case "-V":
                case "--version":
                    System.out.println("IPMIwatch version " + version);
                    printAsset("legal.txt");
                    break;
                case "-f":
                case "-F":
                case "--fan":
                    i++; // Move forward 1 argument
                    try {
                        this.fanThreshold = Double.parseDouble(args[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Hmm, I don't think \"" + args[i] + "\" is a number...");
                        return;
                    }

                    this.doMonFan = true;
                    this.doExit = false;
                    break;
                case "-t":
                case "-T":
                case "--temp":
                    i++; // Move forward 1 argument
                    /* Check if the user-supplied temperature is even a number */
                    try {
                        this.tempThreshold = Double.parseDouble(args[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Hmm, I don't think \"" + args[i] + "\" is a number...");
                        return;
                    }

                    this.doMonTemp = true;
                    this.doExit = false;
                    break;
                case "--alert":
                    i++; // Move forward 1 argument
                    this.alertType = classifyAlert(args[i]);

                    /* Depending on the type of alert, create appropriate abstract class */
                    switch (this.alertType) {
                        default:
                        case unknown:
                            System.out.println("I don't recognize \"" + args[i] + "\", perhaps you're trying to create an alert that is not yet supported?");
                            return;
                        case discord:
                            alert = new discord(args[i]);
                            this.doAlert = true;
                            break;
                    }
                    break;
                case "--log":
                    i++; // Move forward 1 argument
                    /* Check if path is valid */
                    try {
                        Paths.get(args[i]);
                    } catch (InvalidPathException e) {
                        System.out.println("It appears that \"" + args[i] + "\" isn't a valid filesystem path. Please provide a path where we can write your log file.");
                        return;
                    }

                    this.doLog = true;
                    this.logPath = args[i];
                    break;
            }
        }
    }

    /**
     * Determines from the given arguments whether or not the program should exit. Always check this first, and exit main() if required
     *
     * @return whether or not main() should immediately exit
     */
    public boolean doExit() {
        return doExit;
    }

    /**
     * Returns the contents of version.txt
     *
     * @return the version number
     */
    public String getVersion() {
        return version;
    }

    /**
     * Prints out a file from inside the compiled JAR
     *
     * @param assetName the name of the file you wish to print
     */
    public void printAsset(String assetName) {
        InputStream strm = getClass().getResourceAsStream(assetName); // Open the file as a stream

        try (BufferedReader br = new BufferedReader(new InputStreamReader(strm))) { // Open the stream as a BufferedReader
            String line;

            while ((line = br.readLine()) != null) { // Print each line in the BufferedReader
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Hmmm this should never happen... It seems your JAR file may be corrupted. Try contacting the author");
        }
    }

    /**
     * Returns the first line in the specified file from the JAR
     * This method is most notably used to get the version number from /version.txt in the assets dir
     *
     * @param assetName the name of the file you wish to get the first line from
     * @return the first line of the file specified
     */
    public String getAsset(String assetName) {
        InputStream strm = getClass().getResourceAsStream(assetName); // Open the file as a stream

        try (BufferedReader br = new BufferedReader(new InputStreamReader(strm))) { // Open the stream as a BufferedReader
            return br.readLine(); // Returns the first line
        } catch (IOException e) {
            System.out.println("Hmmm this should never happen... It seems your JAR file may be corrupted. Try contacting the author");
            return "JAR file corruption detected. Dev made an oopsie";
        }
    }

    /**
     * Gets the alert object
     * @return the alert object
     */
    public alertAbstract getAlert() {
        return this.alert;
    }

    /**
     * Gets the alert type enumerator
     * @return the alert type enum
     */
    public alertEnum getAlertType() {
        return this.alertType;
    }

    /**
     * Whether or not an alert needs to be made
     * @return whether or not an alert needs to be made
     */
    public boolean doAlert() {
        return this.doAlert;
    }

    /**
     * The path in which the log file should be written
     * @return the path in which the log file should be written
     */
    public String getLogPath() {
        return this.logPath;
    }

    /**
     /**
     * Return whether or not you should log the session
     * @return whether or not you should log the session
     */
    public boolean doLog() {
        return this.doLog;
    }

    /**
     * Return the set fan threshold (%)
     * @return the set fan threshold (%)
     */
    public double getFanThreshold() {
        return this.fanThreshold;
    }

    /**
     * Return the set temp threshold (C)
     * @return the set temp threshold (C)
     */
    public double getTempThreshold() {
        return this.tempThreshold;
    }

    /**
     * Return whether or not you should monitor the fan speeds
     * @return whether or not you should monitor the fan speeds
     */
    public boolean doMonFan() {
        return this.doMonFan;
    }

    /**
     * Return whether or not you should monitor the temperatures
     * @return whether or not you should monitor the temperatures
     */
    public boolean doMonTemp() {
        return this.doMonTemp;
    }

    /**
     * Classifies an alert based on its substrings
     *
     * @param alertAddress the alert to attempt to identify
     * @return an enumerator to identify a type of alert
     */
    private alertEnum classifyAlert(String alertAddress) {
        if (alertAddress.toLowerCase().contains("discordapp.com/api/webhooks/")) {
            return alertEnum.discord;
        } else {
            return unknown;
        }
    }
}
