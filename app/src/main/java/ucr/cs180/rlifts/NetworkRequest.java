package ucr.cs180.rlifts;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.List;

/**
 * Created by isaaclong on 10/12/15.
 *
 * Used for basic http network requests to our server.
 * For now, this will just make requests to specific, server-side scripts.
 */
public class NetworkRequest {

    private String hostname; // our server --> http://45.55.29.36/
    public JSONArray response;


    public NetworkRequest(String hostname) {

        this.hostname = hostname;
        response = new JSONArray();
    }

    public JSONArray getResponse() { return this.response; }

    // script name is the path to the script on the server
    // http method is the method name, e.g. GET, POST
    // payload is what we are sending in the case of a POST or a PUT, will be empty otherwise
    public int send(String scriptName, String httpMethod, JSONArray payload) {
        try {
            URL url = new URL(hostname + scriptName);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod(httpMethod);
            urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConn.setDoInput(true);
            if(httpMethod.equals("POST")) {
                urlConn.setDoOutput(true);
                urlConn.setChunkedStreamingMode(0);
                if(urlConn.getOutputStream() == null) System.out.println("We got problems!");
                OutputStream out = new BufferedOutputStream(urlConn.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(payload.toString());

                writer.close();
                out.close();
            }

            // Handle response from server
            InputStream in = new BufferedInputStream(urlConn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;


            while((line = reader.readLine()) != null) {
                System.out.println("line: " + line);
                JSONObject j = null;
                try {
                    j = new JSONObject(line); // assuming gauranteed JSON responses from our server
                } catch (Exception e) {
                    System.out.println(""); // so we can see python errors from server and still finish execution
                }

                response.put(j);
            }

            reader.close();
            in.close();
            urlConn.disconnect();

        } catch(Exception e) {
            System.out.println("Debug in get request:\n" + e.getMessage());
            e.printStackTrace();
            return -1;
        }

        return 0;
    }
}