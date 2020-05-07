package alerts;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Send an alert via the Discord Webhook API
 *
 * @author TheFuzzyFish
 */
public class discord extends alertAbstract {
    private String webhook;

    /**
     * Set the Discord Webhook API URL. This is your gatekey to your alert server
     *
     * @param url Discord Webhook API
     */
    public discord(String url) {
        this.webhook = url;
    }

    /**
     * Set the Discord username of the bot to the hostname of this computer
     *
     * @throws IOException if the hostname could not be determined
     */
    public void setSenderAsHostname() throws IOException {
        this.setSender(new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream())).readLine());
    }

    /**
     * Sends the Discord Webhook alert
     *
     * @throws RuntimeException if connection fails for some reason
     */
    @Override
    public void send() throws IOException {
        /* Set up JSON content */
        JSONObject json = new JSONObject();
        json.put("username", this.sender);
        json.put("content", this.body.replace("\n", "\\n")); // JSON doesn't actually support newlines, but Discord knows how to deal with the string literal "\n"

        /* Initiate connection */
        HttpsURLConnection connection = (HttpsURLConnection) new URL(this.webhook).openConnection();

        /* Send data */
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "https://github.com/TheFuzzyFish/IPMIwatch");
        connection.setDoOutput(true);
        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes());
        stream.flush();

        /* Close connection */
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }

    /**
     * Used to represent a JSON entry
     */
    private class JSONObject {
        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val.toString());
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}
