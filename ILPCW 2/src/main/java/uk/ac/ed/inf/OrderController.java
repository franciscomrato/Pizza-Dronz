package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.time.Duration;
import java.time.Instant;


import java.io.File;
import java.time.LocalDate;


@SpringBootApplication
public class OrderController
{
    public static void main(String[] args)
    {
        Instant startInstant = Instant.now();
        SpringApplication.run(OrderController.class,args);
        String stringDate = args[0];

        //checks if the date is valid
        try {
            LocalDate date = LocalDate.parse(stringDate);
        } catch (Exception x) {
            System.err.println("The date is invalid: " + x);
            System.exit(1);
        }

        //gets rest data
        GetClient getRest = new GetClient();
        getRest.getFromRest(args);

        //checks if the system is alive
        if (!getRest.getIsAlive())
        {
            System.exit(0);
        }

        Order[] orders = getRest.getOrders();
        Restaurant[] restaurants = getRest.getRestaurants();
        NamedRegion centralArea = getRest.getCentralArea();
        NamedRegion[] noFlyZones = getRest.getNoFlyZones();
        LngLat appleton = new LngLat(-3.186874, 55.944494);
        LngLat endCoordinatesAppleton = appleton;

        JsonArray flightpath = new JsonArray();
        JsonArray geoJson = new JsonArray();

        OrderValidation orderValidation = new OrderValidation();
        LngLatHandler handler = new LngLatHandler();

        //loops through every order
        for (Order order : orders)
        {
            //validates the order
            orderValidation.validateOrder(order, restaurants);
            Restaurant orderRestaurant = orderValidation.getOrderRestaurant();
            if (order.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED))
            {
                PathFinder pathFinder1 = new PathFinder();
                //finds path from appleton to the restaurant
                LngLat endCoordinatesRestaurant = pathFinder1.aStar(endCoordinatesAppleton,orderRestaurant.location(),
                        noFlyZones, handler,false,false,centralArea);
                flightpath.addAll(pathFinder1.getJsonRecords(order.getOrderNo()));
                //adds a hover move to the json record for flightpath
                JsonObject hoverAtRestaurant = new JsonObject();
                hoverAtRestaurant.addProperty("orderNo",order.getOrderNo());
                hoverAtRestaurant.addProperty("fromLongitude",endCoordinatesRestaurant.lng());
                hoverAtRestaurant.addProperty("fromLatitude",endCoordinatesRestaurant.lat());
                hoverAtRestaurant.addProperty("angle",999);
                hoverAtRestaurant.addProperty("toLongitude",endCoordinatesRestaurant.lng());
                hoverAtRestaurant.addProperty("toLatitude",endCoordinatesRestaurant.lat());
                flightpath.add(hoverAtRestaurant);
                geoJson.addAll(pathFinder1.geoJsonPath());
                JsonArray restaurantHoverGeo = new JsonArray();
                restaurantHoverGeo.add(endCoordinatesRestaurant.lng());
                restaurantHoverGeo.add(endCoordinatesRestaurant.lat());
                geoJson.add(restaurantHoverGeo);

                //finds path from the restaurant to the central area
                PathFinder pathFinder2 = new PathFinder();
                LngLat destination = pathFinder2.closestInCentralArea(endCoordinatesRestaurant,centralArea,handler);
                LngLat endCoordinatesCentral = pathFinder2.aStar(endCoordinatesRestaurant,destination,noFlyZones,
                        handler, true,false,centralArea);
                flightpath.addAll(pathFinder2.getJsonRecords(order.getOrderNo()));
                geoJson.addAll(pathFinder2.geoJsonPath());

                //finds the path from the central area to appleton
                PathFinder pathFinder3 = new PathFinder();
                endCoordinatesAppleton = pathFinder3.aStar(endCoordinatesCentral,appleton,noFlyZones, handler,
                        false,true,centralArea);
                flightpath.addAll(pathFinder3.getJsonRecords(order.getOrderNo()));
                JsonObject hoverAtAppleton = new JsonObject();
                //adds hover move at appleton to flightpath json records
                hoverAtAppleton.addProperty("orderNo",order.getOrderNo());
                hoverAtAppleton.addProperty("fromLongitude",endCoordinatesAppleton.lng());
                hoverAtAppleton.addProperty("fromLatitude",endCoordinatesAppleton.lat());
                hoverAtAppleton.addProperty("angle",999);
                hoverAtAppleton.addProperty("toLongitude",endCoordinatesAppleton.lng());
                hoverAtAppleton.addProperty("toLatitude",endCoordinatesAppleton.lat());
                flightpath.add(hoverAtAppleton);
                geoJson.addAll(pathFinder3.geoJsonPath());
                JsonArray appletonHoverGeo = new JsonArray();
                appletonHoverGeo.add(endCoordinatesAppleton.lng());
                appletonHoverGeo.add(endCoordinatesAppleton.lat());
                geoJson.add(appletonHoverGeo);
                //sets order as delivered
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }

        //loop through orders to get json records for flightpath file
        JsonArray ordersJson = new JsonArray();
        for (Order order : orders)
        {
            JsonObject jsonRecord = new JsonObject();
            jsonRecord.addProperty("orderNo",order.getOrderNo());
            jsonRecord.addProperty("orderStatus",order.getOrderStatus().toString());
            jsonRecord.addProperty("orderValidationCode",order.getOrderValidationCode().toString());
            jsonRecord.addProperty("costInPence",order.getPriceTotalInPence());
            ordersJson.add(jsonRecord);
        }

        //gets coordinates in GeoJSON format
        JsonObject drone = new JsonObject();
        drone.addProperty("type","FeatureCollection");

        JsonArray features = new JsonArray();
        JsonObject feature = new JsonObject();
        feature.addProperty("type","Feature");

        JsonObject properties = new JsonObject();
        feature.add("properties", properties);
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type","LineString");
        geometry.add("coordinates",geoJson);
        feature.add("geometry", geometry);
        features.add(feature);
        drone.add("features",features);
        WriteToFile fileWriter = new WriteToFile();
        fileWriter.fileCreator(stringDate,ordersJson,flightpath,drone);

        Instant endInstant = Instant.now();
        Duration elapsedTime = Duration.between(startInstant, endInstant);

        System.out.println("Total execution time: " + elapsedTime.toMillis() + " milliseconds");
        System.exit(0);
    }
}
