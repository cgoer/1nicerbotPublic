import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCall {

    public double call(String extension) throws Exception {
        String urlBase = "https://v3.exchangerate-api.com/pair/PUTIN YOUR EXCHANGRATE TOKEN HERE/";
        StringBuilder result = new StringBuilder();
        result.append(urlBase);
        result.append(extension);
        String url = result.toString();


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // get rewuest
        con.setRequestMethod("GET");
        //request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();


        //JSON response auslesen und als double weitergeben
        JSONObject myResponse = new JSONObject(response.toString());
        double rate = myResponse.getDouble("rate");

        return rate;
    }
}

