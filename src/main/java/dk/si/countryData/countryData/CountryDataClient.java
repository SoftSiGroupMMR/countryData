package dk.si.countryData.countryData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.si.countryData.classes.CountryData;
import get.dk.si.route.MetaData;
import get.dk.si.route.Root;
import get.dk.si.route.Route;
import get.dk.si.route.Util;

import java.io.*;
import java.util.concurrent.*;


public class CountryDataClient {

    private final Gson gson = new Gson();
    private final GetCountryByCity getCountryByCity = new GetCountryByCity();

/*
    public static void main(String[] args) throws IOException {
        CountryDataClient countryDataClient = new CountryDataClient();
        countryDataClient.getCountryDataConcurrent("lyngby");

    }
 */

    public void countryDataHandler(String message) {

        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        System.out.println(jsonMessage);

        String city = jsonMessage.get("metaData").getAsJsonObject().get("travelRequest").getAsJsonObject().get("cityTo").getAsString();
        System.out.println(city);

        try {

            CountryData countryData = getCountryDataConcurrent(city);



            Util util = new Util();
            Root root = util.rootFromJson(message);
            MetaData metaData = root.getMetaData();
            metaData.put("countryData", countryData);

            root.setMetaData(metaData);

            Route route = root.nextRoute();
            String json = util.rootToJson(root);
            util.sendToRoute(route, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private CountryData getCountryDataConcurrent(String cityInput) throws IOException {

        JsonObject city = getCountryByCity.getCountryByCity(cityInput);
        String countryCode = city.get("countryCode").getAsString();
        String countryName = city.get("country").getAsString();
        System.out.println(countryCode);


        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<String> futureFlag = service.submit(new GetCountryFlag(countryCode));
        Future<String> futureCurrency = service.submit(new GetCountryCurrency(countryCode));


        try {

            String flag = futureFlag.get(3, TimeUnit.SECONDS);
            System.out.println(flag);

            String currency = futureCurrency.get(3, TimeUnit.SECONDS);
            System.out.println(currency);

            service.shutdown();
            CountryData countryData = new CountryData(countryCode, countryName, flag, currency);
            return  countryData;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        service.shutdown();
        return null;
    }

}


