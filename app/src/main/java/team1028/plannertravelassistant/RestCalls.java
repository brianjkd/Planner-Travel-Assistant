package team1028.plannertravelassistant;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestCalls {

    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static String doGet(String myurl) throws IOException {
        String returnedString = "";
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000 * 10 /* milliseconds */);
        conn.setConnectTimeout(1000 * 15 /* milliseconds */);

        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.setRequestProperty("connection", "close"); // prevent freezing?
        // Starts the query
        conn.connect(); // we have a connections

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

    // post some json to a url
    public static String doPost(String myurl, String postBody) throws IOException {
        String returnedString = "";
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000 * 10 /* milliseconds */);
        conn.setConnectTimeout(1000 * 15 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true); // we are posting some body

        conn.setRequestProperty("connection", "close"); // prevent freezing?
        // Starts the query
        conn.connect(); // we have a connections

        OutputStream os = conn.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
        osw.write(postBody); // write json to the server
        osw.flush();
        osw.close();

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

}
