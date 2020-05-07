public class argHandler {
    private boolean doExit;

    public argHandler(String[] args) {
        doExit = true; // Flag is flipped per the arguments for whether or not main() should immediately exit based on context

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                case "--help":
                    System.out.println("Congrats! You printed a help message");
            }
        }
    }

    public boolean doExit() {
        return doExit;
    }
}
