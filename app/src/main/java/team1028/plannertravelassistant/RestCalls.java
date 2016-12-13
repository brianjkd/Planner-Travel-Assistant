package team1028.plannertravelassistant;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to interface with RESTful API and make appropriate calls
 */
public class RestCalls {
    private static final int READ_TIMEOUT = 1000 * 10; // Milliseconds
    private static final int CONNECT_TIMEOUT = 1000 * 15; // Milliseconds

	// Read byte input stream TODO isn't there a built-in function for this?
    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();

	        // Read to end of byte stream
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

	// Perform a GET request for the appropriate URL
    public static String doGet(String aUrl) throws IOException {
	    // Prepare to open connection
        String returnedString = "";
        URL url = new URL(aUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECT_TIMEOUT);

        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.setRequestProperty("connection", "close"); // prevent freezing?
        // Starts the query
        conn.connect(); // we have a connections

	    // Attempt to get response
        int response = conn.getResponseCode();
        try {
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream is = new BufferedInputStream(conn.getInputStream());
                returnedString = readStream(is);
                is.close();
            }
        } finally {
            conn.disconnect();
        }

        return returnedString;
    }

    // Perform a GET request using JSON (to the appropriate URL)
    public static String doPost(String aUrl, String postBody) throws IOException {
	    // Prepare for connection
        String returnedString = "";
        URL url = new URL(aUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true); // we are posting some body

        conn.setRequestProperty("connection", "close"); // prevent freezing?
        // Starts the query
        conn.connect(); // we have a connections

	    // Prepare to write data
        OutputStream os = conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(postBody); // write json to the server
        osw.flush();
        osw.close();

        int response = conn.getResponseCode();

	    // Attempt to make POST request
        try {
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream is = new BufferedInputStream(conn.getInputStream());
                returnedString = readStream(is);
                is.close();
            }
        } finally {
            conn.disconnect();
        }
        return returnedString;
    }
}