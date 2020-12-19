package dk.si.countryData.countryData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.si.countryData.classes.CountryData;
import get.dk.si.route.MetaData;
import get.dk.si.route.Root;
import get.dk.si.route.Route;
import get.dk.si.route.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.*;


public class CountryDataClient {


    private Logger logger = LoggerFactory.getLogger(CountryDataClient.class.getName());

    private final Gson gson = new Gson();
    private final GetCountryByCity getCountryByCity = new GetCountryByCity();

    public void countryDataHandler(String message) {
        try {
            logger.info("recieved message");
            JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);

            String city = jsonMessage.get("metaData").getAsJsonObject().get("travelRequest").getAsJsonObject().get("cityTo").getAsString();


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
            logger.error(e.getLocalizedMessage());
        }
    }


    private CountryData getCountryDataConcurrent(String cityInput) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        logger.info("getCountryDataConcurrent, collection data from SOAP endpoints");
        JsonObject city = getCountryByCity.getCountryByCity(cityInput);
        String countryCode = city.get("countryCode").getAsString();
        String countryName = city.get("country").getAsString();

        ExecutorService service = Executors.newFixedThreadPool(3);
        Future<String> futureFlag = service.submit(new GetCountryFlag(countryCode));
        Future<String> futureCurrency = service.submit(new GetCountryCurrency(countryCode));


        String flag = futureFlag.get(3, TimeUnit.SECONDS);

        String currency = futureCurrency.get(3, TimeUnit.SECONDS);

        service.shutdown();
        CountryData countryData = new CountryData(countryCode, countryName, flag, currency);

        service.shutdown();
        return countryData;
    }

}


