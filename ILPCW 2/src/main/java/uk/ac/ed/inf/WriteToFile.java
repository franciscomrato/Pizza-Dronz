package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile
{
    void fileCreator(String date, JsonArray orders, JsonArray flightPathMoves, JsonObject dronePath)
    {
        //creates three json files
        try (FileWriter deliveries =
                     new FileWriter(new File("resultfiles", "deliveries-" +date + ".json"));
             FileWriter flightpath = new FileWriter(new File("resultfiles", "flightpath-" +date + ".json"));
             FileWriter drone = new FileWriter(new File("resultfiles", "drone-" +date + ".geojson"))) {

            //writes to the three json files
            deliveries.write(orders.toString());
            flightpath.write(flightPathMoves.toString());
            drone.write(dronePath.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
