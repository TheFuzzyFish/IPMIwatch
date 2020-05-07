import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        argHandler options = new argHandler(args);
        if (options.doExit()) {
            return;
        }

        while (true) {
            Runtime.getRuntime().gc();

            Process sensors = null;
            try {
                sensors = Runtime.getRuntime().exec("/usr/bin/ipmitool sensor");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Scanner scan = new Scanner(sensors.getInputStream());

            if (args[0].equals("temp")) {
                String line = scan.nextLine();
                while (!line.startsWith("Temp ")) {
                    line = scan.nextLine();
                }

                ArrayList<Double> temps = new ArrayList<>();
                while (line.startsWith("Temp ")) {
                    String[] values = line.split("\\|");
                    double thisTemp = Double.parseDouble(values[1].trim());

                    if (thisTemp > Double.parseDouble(args[1])) {
                        temps.add(thisTemp);
                    }
                    line = scan.nextLine();
                }

                if (temps.size() > 0) {
                    String message = temps.size() + " temperatures just exceeded " + args[1] + "C";
                    System.out.println(message);
                    alert(message);
                }
            } else if (args[0].equals("fans")) {
                String line = scan.nextLine();

                while (!line.startsWith("Fan ")) {
                    line = scan.nextLine();
                }

                ArrayList<Double> fanSpeeds = new ArrayList<>();
                while (line.startsWith("Fan ")) {
                    String[] values = line.split("\\|");
                    double thisSpeed = Double.parseDouble(values[1].trim());

                    if (thisSpeed > Double.parseDouble(args[1])) {
                        fanSpeeds.add(thisSpeed);
                    }
                    line = scan.nextLine();
                }

                if (fanSpeeds.size() > 0) {
                    String message = fanSpeeds.size() + " fans just exceeded " + args[1] + "%";
                    System.out.println(message);
                    alert(message);
                }
            } else {
                System.out.println("No known command issued");
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void alert (String message) {
        try {
            Runtime.getRuntime().exec("/usr/local/share/scripts/notificator9000.sh " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}