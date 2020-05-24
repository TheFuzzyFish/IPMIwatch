import alerts.*;

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

            /* Processes temperature data */
            if (options.doMonTemp()) {
                String line = scan.nextLine(); // Scan one line at a time

                /* Loop through the output until you find a temperature sensor (which starts with "Temp ") */
                while (!line.startsWith("Temp ")) {
                    line = scan.nextLine();
                }

                /* Scan all temperatures above the threshold specified by the user into an ArrayList */
                ArrayList<Double> temps = new ArrayList<>(); // Used to store the temperature values from all sensors
                double maxTemp = -1; // Used to store the max temperature.
                while (line.startsWith("Temp ")) {
                    String[] values = line.split("\\|");
                    double thisTemp = Double.parseDouble(values[1].trim());

                    /* Adds the temperature to the ArrayList if it's above the user-defined threshold */
                    if (thisTemp > options.getTempThreshold()) {
                        temps.add(thisTemp);

                        /* Sets the max temperature if applicable */
                        if (thisTemp > maxTemp) {
                            maxTemp = thisTemp;
                        }
                    }

                    line = scan.nextLine(); // Continue traversing the temperature sensors
                }

                /* If there were any temperatures above the user-defined threshold, determine action */
                if (temps.size() > 0) {
                    String message = temps.size() + " temperatures just exceeded " + args[1] + "C with one reading as high as " + maxTemp + " degrees.";
                    System.out.println(message);

                    if (options.doAlert()) {
                        System.out.println("Sending alert.");
                        alert(message);
                    }
                }
            }

            scan.reset(); // Start the scanner back at the top

            /* Processes fan data */
            if (options.doMonFan()) {
                String line = scan.nextLine(); // Scan one line at a time

                /* Loop through the output until you find a fan sensor (which starts with "Fan ") */
                while (!line.startsWith("Fan ")) {
                    line = scan.nextLine();
                }

                /* Scan all fan speeds above the threshold specified by the user into an ArrayList */
                ArrayList<Double> fans = new ArrayList<>(); // Used to store the fan speed percents from all sensors
                double maxFan = -1; // Used to store the max fan speed
                while (line.startsWith("Fan ")) {
                    String[] values = line.split("\\|");
                    double thisFan = Double.parseDouble(values[1].trim());

                    /* Adds the fan speeds to the ArrayList if it's above the user-defined threshold */
                    if (thisFan > options.getFanThreshold()) {
                        fans.add(thisFan);

                        /* Sets the max temperature if applicable */
                        if (thisFan > maxFan) {
                            maxFan = thisFan;
                        }
                    }

                    line = scan.nextLine(); // Continue traversing the fan sensors
                }

                /* If there were any fan speeds above the user-defined threshold, determine action */
                if (fans.size() > 0) {
                    String message = fans.size() + " fans just exceeded " + args[1] + "% with one reading as high as " + maxFan + "%";
                    System.out.println(message);

                    if (options.doAlert()) {
                        System.out.println("Sending alert.");
                        alert(message);
                    }
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an alert, assumes that you already checked to see if an alert needs sent
     * @param message the message to send in the alert
     */
    public static void alert (String message) {
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