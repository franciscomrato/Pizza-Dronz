package uk.ac.ed.inf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;

public class GetClient {

    public static final String RESTAURANT_URL = "restaurants";
    public static final String ORDER_URL = "orders";
    public static final String CENTRAL_AREA_URL = "centralArea";
    public static final String NO_FLY_ZONES_URL = "noFlyZones";
    public static final String ALIVE_URL = "isAlive";

    public static Restaurant[] restaurants;
    public static Order[] orders;
    public static NamedRegion centralArea;
    public static NamedRegion[] noFlyZones;

    public static boolean isAlive;
    public void getFromRest(String[] args) {
        //makes sure both date and url are provided
        if (args.length < 2){
            System.err.println("the base URL and date must be provided");
            //System.exit(1);
        }

        //adds a dash to the end of the url provided if it doesn't have one
        var baseUrl = args[1];
        if (!baseUrl.endsWith("/")){
            baseUrl += "/";
        }

        //checks if url is valid
        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            System.err.println("The URL is invalid: " + x);
            //System.exit(2);
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        //gets all the restaurants from the rest server
        try {
            restaurants = mapper.readValue(new URL(baseUrl + RESTAURANT_URL), Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //gets all the orders from the rest server for the day specified
        try {
            orders = mapper.readValue(new URL(baseUrl + ORDER_URL +"/" + args[0]), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //gets the central area from the rest server
        try {
            centralArea = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //gets the no-fly zones from the rest server
        try {
            noFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONES_URL), NamedRegion[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //gets boolean saying if system is alive
        try {
            isAlive = mapper.readValue(new URL(baseUrl + ALIVE_URL), boolean.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Restaurant[] getRestaurants()
    {
        return restaurants;
    }

    public Order[] getOrders()
    {
        return orders;
    }

    public NamedRegion getCentralArea()
    {
        return centralArea;
    }
    public NamedRegion[] getNoFlyZones()
    {
        return noFlyZones;
    }
    public boolean getIsAlive()
    {
        return isAlive;

    }
}
