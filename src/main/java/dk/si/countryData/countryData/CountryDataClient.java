package dk.si.countryData.countryData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class CountryDataClient {

    private final GetCountryByCity getCountryByCity = new GetCountryByCity();


    public static void main(String[] args) throws IOException {
        CountryDataClient countryDataClient = new CountryDataClient();
        countryDataClient.getCountryDataConcurrent("lyngby");

    }


    public void getCountryDataConcurrent(String message) throws IOException {

        // Mangler håndtering af message til at finde travelTo string i metadata.


        JsonObject city = getCountryByCity.getCountryByCity(message);
        String countryCode = city.get("countryCode").getAsString();
        System.out.println(countryCode);


        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<String> futureFlag = service.submit(new GetCountryFlag(countryCode));
        Future<String> futureCurrency = service.submit(new GetCountryCurrency(countryCode));


        try {

            String flag = futureFlag.get(3, TimeUnit.SECONDS);
            System.out.println(flag);

            String currency = futureCurrency.get(3, TimeUnit.SECONDS);
            System.out.println(currency);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        service.shutdown();
    }



}


