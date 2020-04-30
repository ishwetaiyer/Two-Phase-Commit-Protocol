package cs223.simulator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.json";
    private static final JSONParser parser = new JSONParser();
    private static JSONObject config = null;

    /**
     * checks whether there is a configuration file at {@link #CONFIG_FILE_PATH} in json format,
     * and loads it if there is.
     * <p>
     * WARNING: should be called before {@link #getConfig()} method.
     */
    public static void initialize() {
        try {
            config = (JSONObject) parser.parse(new FileReader(CONFIG_FILE_PATH));
        } catch (IOException e) {
            System.err.println("Could not read file: " + CONFIG_FILE_PATH);
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Error parsing config file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return the config stored in {@link #config} parameter. Can be null if {@link #initialize()} has not been called.
     * @see #initialize()
     */public static JSONObject getConfig() {
        return config;
    }

}
