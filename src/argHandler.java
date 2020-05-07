import java.io.*;

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

    /**
     * Constructs a new argHandler based on the argument array from main().
     *
     * @param args the arguments passed in from the command line through main()
     */
    public argHandler(String[] args) {
        doExit = true; // Flag is flipped per the arguments for whether or not main() should immediately exit based on context
        version = getAsset("version.txt"); // Not quite sure why I decided to do this, but I'm going to make argHandler in charge of the version number throughout the rest of the program

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                default:
                    System.out.println("Unrecognized flag: \"" + args[i] + "\". Exiting.");
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
                    doExit = false;
            }
        }

        if (args.length == 0) {
            printAsset("help.txt");
        }
    }

    /**
     * Determines from the given arguments whether or not the program should exit. Always check this first, and exit main() if required
     * @return whether or not main() should immediately exit
     */
    public boolean doExit() {
        return doExit;
    }

    /**
     * Returns the contents of version.txt
     * @return the version number
     */
    public String getVersion() {
        return version;
    }

    /**
     * Prints out a file from inside the compiled JAR
     * @param assetName the name of the file you wish to print
     */
    private void printAsset(String assetName) {
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
     * @param assetName the name of the file you wish to get the first line from
     * @return the first line of the file specified
     */
    private String getAsset(String assetName) {
        InputStream strm = getClass().getResourceAsStream(assetName); // Open the file as a stream

        try (BufferedReader br = new BufferedReader(new InputStreamReader(strm))) { // Open the stream as a BufferedReader
            return br.readLine(); // Returns the first line
        } catch (IOException e) {
            System.out.println("Hmmm this should never happen... It seems your JAR file may be corrupted. Try contacting the author");
            return "JAR file corruption detected. Dev made an oopsie";
        }
    }
}
