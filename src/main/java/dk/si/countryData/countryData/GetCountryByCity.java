package dk.si.countryData.countryData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetCountryByCity {

    private final Gson gson = new Gson();

    public JsonObject getCountryByCity(String city) throws IOException {
        //Info about API
        //https://wirefreethought.github.io/geodb-cities-api-docs/#operation--v1-geo-cities--cityId--get
        String cityDataURL = "http://geodb-free-service.wirefreethought.com/v1/geo/cities?namePrefix="+ city +"&limit=1&offset=0&hateoasMode=false";

        // Create the connection where we're going to send the file.
        URL url = new URL(cityDataURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;


        // Set the appropriate HTTP parameters.
        httpConn.setRequestMethod("GET");
        httpConn.setDoOutput(false);
        httpConn.setDoInput(true);

        // Read the response and write it to standard out.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String inputLine;


        String output = "";
        while ((inputLine = in.readLine()) != null)
            output += inputLine;

        in.close();

        JsonObject json = gson.fromJson(output, JsonObject.class);
        JsonObject cityObj = json.get("data").getAsJsonArray().get(0).getAsJsonObject();

        return cityObj;
    }

}
