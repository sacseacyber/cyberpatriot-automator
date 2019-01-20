public class Entry {
    private enum RunMode {
        SERVER,
        CLIENT
    }

    public static void main (String args[]) {
        RunMode mode;
        if (args.length == 0) {
            mode = RunMode.CLIENT;
        } else if (args[0].equals("server")) {
            mode = RunMode.SERVER;
        } else {
            mode = RunMode.CLIENT;
        }

        if (mode == RunMode.CLIENT) {
            if (!Utility.isAdministrator()) {
                System.out.println("Program needs to be run as administrator");
                System.exit(1);
            }
        } else {

        }
    }
}
