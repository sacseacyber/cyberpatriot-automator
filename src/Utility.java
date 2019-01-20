import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utility {
    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    public static boolean isLinux() {
        return !Utility.isWindows();
    }

    public static boolean isAdministrator() {
        if (Utility.isLinux()) {
            try {
                Process proc = Runtime.getRuntime().exec("whoami");

                proc.waitFor();

                InputStream stdout = proc.getInputStream();

                BufferedReader results = new BufferedReader(new InputStreamReader(stdout));

                String result = "", out;

                while ((out = results.readLine()) != null) {
                    result += out;
                }

                return result.equals("root");
            } catch (InterruptedException | IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
