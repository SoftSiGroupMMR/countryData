package dk.si.countryData.countryData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class CountryDataClient {

    private final Gson gson = new Gson();


    public static void main(String[] args) throws IOException {
        CountryDataClient countryDataClient = new CountryDataClient();


        JsonObject city = countryDataClient.getCountryByCity("lyngby");


        String countryCode = city.get("countryCode").getAsString();
        System.out.println(countryCode);

    }

    public void getCountryData(String message) {
        String city = "copenhagen";

        try {
            JsonObject obj = getCountryByCity(city);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private JsonObject getCountryByCity(String city) throws IOException {
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


    public double getTemperatureSOAP(double value, String type) throws IOException {

        byte[] b = null;

        if (type == "fahr") {
            String fToC = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    "    <soap12:Body>\n" +
                    "        <FahrenheitToCelsius xmlns=\"https://www.w3schools.com/xml/\">\n" +
                    "            <Fahrenheit>" + value + "</Fahrenheit>\n" +
                    "        </FahrenheitToCelsius>\n" +
                    "    </soap12:Body>\n" +
                    "</soap12:Envelope>";

            b = fToC.getBytes();

        } else {
            String cTOF = "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    "    <soap12:Body>\n" +
                    "        <CelsiusToFahrenheit xmlns=\"https://www.w3schools.com/xml/\">\n" +
                    "            <Celsius>" + value + "</Celsius>\n" +
                    "        </CelsiusToFahrenheit>\n" +
                    "    </soap12:Body>\n" +
                    "</soap12:Envelope>";
            b = cTOF.getBytes();
        }


        String SOAPUrl = "https://www.w3schools.com/xml/tempconvert.asmx";

        // Create the connection where we're going to send the file.
        URL url = new URL(SOAPUrl);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;


        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Type", "text/xml;");
        //httpConn.setRequestProperty("SOAPAction",SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);

        // Everything's set up; send the XML that was read in to b.
        OutputStream out = httpConn.getOutputStream();
        out.write(b);
        out.close();

        // Read the response and write it to standard out.

        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String inputLine;


        String output = "";
        while ((inputLine = in.readLine()) != null)
            output += inputLine;

        in.close();
        JSONObject responseObj = XML.toJSONObject(output);


        if (type == "fahr") {
            return responseObj.getJSONObject("soap:Envelope").getJSONObject("soap:Body")
                    .getJSONObject("FahrenheitToCelsiusResponse").getDouble("FahrenheitToCelsiusResult");
        }
        if (type == "cel") {
            return responseObj.getJSONObject("soap:Envelope").getJSONObject("soap:Body")
                    .getJSONObject("CelsiusToFahrenheitResponse").getDouble("CelsiusToFahrenheitResult");
        }
        return 0;
    }


}


