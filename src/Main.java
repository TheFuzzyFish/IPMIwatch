import alerts.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
public class Main {
    static argHandler options;

    public static void main(String[] args) {
        options = new argHandler(args);
        if (options.doExit()) {
            return;
        }

        boolean hasTempAlertBeenSentAlready = false;
        boolean hasFanAlertBeenSentAlready = false;
        while (true) {
            /* Get the output of ipmitool */
            Process sensors = null;
            try {
                sensors = Runtime.getRuntime().exec("ipmitool sensor");
            } catch (IOException e) {
                System.out.println("Package \"ipmitool\" undefined in scope. Do you have it installed?");
                return;
            }

            Scanner scan = new Scanner(sensors.getInputStream()); // Start a scanner of that output for processing
            String line = scan.nextLine(); // Scan one line at a time


            ArrayList<Double> tempsAboveThreshold = new ArrayList<>(); // Used to store the temperature values from all sensors
            ArrayList<Double> fansAboveThreshold = new ArrayList<>(); // Used to store the fan speed percents from all sensors
            double maxTemp = -1; // Used to store the max temperature.
            double maxFan = -1; // Used to store the max fan speed

            while (scan.hasNext()) {
                line = scan.nextLine();

                /* Loop through the output until you find the next set of fan or temperature sensors */
                while (!line.startsWith("Temp ") && !line.startsWith("Fan ") && scan.hasNext()) {
                    line = scan.nextLine();
                }

                if (options.doMonTemp() && line.startsWith("Temp ")) { // Scan all temperatures above the threshold specified by the user into an ArrayList
                    String[] values = line.split("\\|");
                    double thisTemp = Double.parseDouble(values[1].trim());

                    /* Adds the temperature to the ArrayList if it's above the user-defined threshold */
                    if (thisTemp > options.getTempThreshold()) {
                        tempsAboveThreshold.add(thisTemp);

                        /* Sets the max temperature if applicable */
                        if (thisTemp > maxTemp) {
                            maxTemp = thisTemp;
                        }
                    }
                } else if (options.doMonFan() && line.startsWith("Fan ")) { // Scan all fans above the threshold specified by the user into an ArrayList
                    /* Scan all fan speeds above the threshold specified by the user into an ArrayList */
                    String[] values = line.split("\\|");
                    double thisFan = Double.parseDouble(values[1].trim());

                    /* Adds the fan speeds to the ArrayList if it's above the user-defined threshold */
                    if (thisFan > options.getFanThreshold()) {
                        fansAboveThreshold.add(thisFan);

                        /* Sets the max temperature if applicable */
                        if (thisFan > maxFan) {
                            maxFan = thisFan;
                        }
                    }
                }
            }

            /* If there were any temperatures above the user-defined threshold, determine action */
            if (tempsAboveThreshold.size() > 0) {
                String message = tempsAboveThreshold.size() + " temperatures just exceeded threshold with one reading as high as " + maxTemp + " degrees. ";
                System.out.print(message);

                if (options.doAlert() && !hasTempAlertBeenSentAlready) {
                    System.out.print("Sending alert.");
                    alert(message);
                    hasTempAlertBeenSentAlready = true;
                } else if (options.doAlert() && hasTempAlertBeenSentAlready) {
                    System.out.print("Suppressing alert for consecutive reading.");
                }
                System.out.print("\n");
            } else {
                hasTempAlertBeenSentAlready = false;
            }

            /* If there were any fan speeds above the user-defined threshold, determine action */
            if (fansAboveThreshold.size() > 0) {
                String message = fansAboveThreshold.size() + " fans just exceeded threshold with one reading as high as " + maxFan + "%. ";
                System.out.print(message);

                if (options.doAlert() && !hasFanAlertBeenSentAlready) {
                    System.out.print("Sending alert.");
                    alert(message);
                    hasFanAlertBeenSentAlready = true;
                } else if (options.doAlert() && hasFanAlertBeenSentAlready) {
                    System.out.print("Suppressing alert for consecutive reading.");
                }
                System.out.print("\n");
            } else {
                hasFanAlertBeenSentAlready = false;
            }

            // Check sensors every 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Something straight up broke. Literally no idea how this could've happened, you're on your own.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an alert, assumes that you already checked to see if an alert needs sent
     *
     * @param message the message to send in the alert
     */
    public static void alert(String message) {
        switch (options.getAlertType()) {
            default:
            case unknown:
                System.out.println("Unknown alert type defined. Perhaps you made a typo, or you're trying to send an alert that is not currently supported");
                break;
            case discord:
                discord send = (discord) options.getAlert(); // Gets the appropriate object from the argHandler

                /* Attempt to set the Discord bot name as this machine's hostname, but just sets it as "IPMIwatch" if it can't determine the hostname */
                try {
                    send.setSenderAsHostname();
                } catch (IOException e) {
                    send.setSender("IPMIwatch");
                }

                send.setBody(message); // Sets the alert body as the determined message

                /* Attempt to send the alert. */
                try {
                    send.send();
                } catch (IOException e) {
                    System.out.println("Unknown error sending alert.");
                }
                break;
        }
    }
}