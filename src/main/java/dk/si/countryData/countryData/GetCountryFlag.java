package dk.si.countryData.countryData;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class GetCountryFlag implements Callable<String> {

    private String countryCode;
    private final String SOAP_URL = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";

    public GetCountryFlag(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String call() throws Exception {
        return getCountryFlag(this.countryCode);
    }


    public String getCountryFlag(String countryCode) throws IOException {
        byte[] b = null;

        String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <CountryFlag xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                "      <sCountryISOCode>" + countryCode + "</sCountryISOCode>\n" +
                "    </CountryFlag>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";

        b = body.getBytes();


        // Create the connection where we're going to send the file.
        URL url = new URL(SOAP_URL);
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


        return responseObj.getJSONObject("soap:Envelope").getJSONObject("soap:Body")
                .getJSONObject("m:CountryFlagResponse").getString("m:CountryFlagResult");

    }


}
