package org.Simulator.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class network {
    public static String url_GET(String URL) throws IOException {
        try {
            URL main_url = new URL(URL);
            HttpURLConnection  connect = (HttpURLConnection) main_url.openConnection();
            connect.setRequestMethod("GET");
            BufferedReader Recieved = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String lineString;
            StringBuilder  reponse = new StringBuilder(); 
            if((lineString = Recieved.readLine()) != null) {
                reponse.append(lineString);
            }
            Recieved.close();
            return reponse.toString();
        } catch(MalformedURLException error) {
            return "NULL";
        }
    }    
    public static String url_PUT(String URL, String data) throws IOException {
        try {
            URL main_url = new URL(URL); 
            HttpURLConnection  connect = (HttpURLConnection) main_url.openConnection(); 
            connect.setRequestMethod("PUT"); 
            connect.setRequestProperty("Content-Type", "application/json; utf-8");
            connect.setDoOutput(true);
            
            try (OutputStream os = connect.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            } catch(Exception error) {
                return "Failed to write data: " + error.getMessage();
            }
            
            int response = connect.getResponseCode();
            if(response != HttpURLConnection.HTTP_OK) {
                return "ERROR " + response + ": " + connect.getResponseMessage();
            }
            return "Sent " + data + " to " + URL;
        } catch(MalformedURLException error) {
            return "NULL";
        }
    }
}
