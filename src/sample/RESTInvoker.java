package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class RESTInvoker {
    private final String requestURL;
    private final String name;
    private final String password;

    public RESTInvoker(String requestURL, String name, String password) {
        this.requestURL = requestURL;
        this.name = name;
        this.password = password;
    }

    public String makeGetRequest(){

            String authString = name + ":" + password;
          //  System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
          //  System.out.println("Base64 encoded auth string: " + authStringEnc);

        try {
            URL url = new URL(requestURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();

            /*System.out.println("*** BEGIN ***");
            System.out.println(result);
            System.out.println("*** END ***");*/

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}