package registration;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Entry point. Loads the seed data and starts the web front end. */
public class Main {

    public static void main(String[] args) throws Exception {
        Path dataDirectory = Paths.get(args.length > 0 ? args[0] : "data");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
        Registry registry = DataLoader.load(dataDirectory);
        new WebApp(registry).start(port);
    }
}
