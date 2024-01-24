package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder {

    PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparing(node -> node.getTotalCost()));
    ArrayList<LngLat> closedList = new ArrayList<>();
    ArrayList<LngLat> path = new ArrayList<>();
    ArrayList<Double> anglesInPath = new ArrayList<>();

    LngLat aStar (LngLat start, LngLat dest, NamedRegion[] noFlyZones, LngLatHandler lngLatHandler, boolean toCentral, boolean toAppleton, NamedRegion centralArea)
    {
        //adds start to the open list and initializes its values
        Node startNode = new Node (start,0,lngLatHandler.distanceTo(start,dest),null,-1.0);
        openList.add(startNode);

        //loops while the open list still has nodes
        while (!(openList.isEmpty()))
        {
            //gets next point to be explored and adds it to the closed list
            Node nextNode = openList.poll();
            closedList.add(nextNode.getCurrentLngLat());

            //checks if journey is to the central area
            if (toCentral)
            {
                //if the journey is to the central area, it stops once a path to the central area is found
                if (lngLatHandler.isInCentralArea(nextNode.getCurrentLngLat(), centralArea)) {
                    path = getPath(nextNode);
                    anglesInPath = getAngles(nextNode);
                    return nextNode.getCurrentLngLat();
                }
            }
            else
            {
                //if the journey isn't to the central area it stops when the current point is close to the destination
                if (lngLatHandler.isCloseTo(nextNode.getCurrentLngLat(), dest)) {
                    path = getPath(nextNode);
                    anglesInPath = getAngles(nextNode);
                    return nextNode.getCurrentLngLat();
                }
            }
            //calculates nodes previous cost
            double childrenCost = nextNode.getPreviousCost() + SystemConstants.DRONE_MOVE_DISTANCE;
            //adds points children to the open list
            addChildren(lngLatHandler, nextNode, dest,childrenCost,noFlyZones,toAppleton, centralArea);

        }
        return null;
    }


    void addChildren(LngLatHandler lngLatHandler, Node currentNode, LngLat dest,double childCost,NamedRegion[] noFlyZones, boolean toAppleton, NamedRegion centralArea)
    {
        double angle = 0;
        //loops through all possible angles drone can move to
        for (int x = 0; x < 16; x++)
        {
            LngLat childLngLat = lngLatHandler.nextPosition(currentNode.getCurrentLngLat(), angle);
            //only adds the child to the open list if it isn't in the closed list or in a no-fly zone
            if (!(closedList.contains(childLngLat)))
            {
                if (inNoFlyZone(noFlyZones,childLngLat,lngLatHandler, toAppleton, centralArea))
                {
                    closedList.add(childLngLat);
                }
                else {
                    Node childNode = new Node (childLngLat,childCost,lngLatHandler.distanceTo(childLngLat,dest),currentNode,angle);
                    openList.add(childNode);
                }
            }
            //angle incremented
            angle += 22.5;
        }
    }

    boolean inNoFlyZone (NamedRegion[] noFlyZones,LngLat point, LngLatHandler handler, boolean toAppleton, NamedRegion centralArea)
    {
        //loops through no-fly zones to check if the point is in them
        for (NamedRegion region : noFlyZones)
        {
            if (handler.isInRegion(point, region))
            {
                return true;
            }
            //if the journey is to Appleton the drone can't leave the central area
            if (toAppleton)
            {
                if(!(handler.isInCentralArea(point,centralArea)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    //gets best path
    ArrayList<LngLat> getPath(Node destNode)
    {
        ArrayList<LngLat> startToDestination = new ArrayList<>();
        Node currentNode = destNode;
        //loops through every LngLat in the path until start is reached
        while ( currentNode != null)
        {
            startToDestination.add(currentNode.getCurrentLngLat());
            currentNode = currentNode.getParent();
        }
        //Path reversed so it goes from start to destination
        Collections.reverse(startToDestination);
        return startToDestination;
    }

    //gets angles drone moves in the path
    ArrayList<Double> getAngles(Node finalNode)
    {
        Node currentNode = finalNode;
        ArrayList<Double> angles = new ArrayList<>();
        while (currentNode.getAngle() != -1.0)
        {
            angles.add(currentNode.getAngle());
            currentNode = currentNode.getParent();
        }
        Collections.reverse(angles);
        return angles;
    }

    //turns path into a Json record
    JsonArray getJsonRecords(String orderNumber)
    {
        JsonArray records = new JsonArray();
        for (int x = 0; x < path.size()-1; x++)
        {
            JsonObject jsonRecord = new JsonObject();
            jsonRecord.addProperty("orderNo",orderNumber);
            jsonRecord.addProperty("fromLongitude",path.get(x).lng());
            jsonRecord.addProperty("fromLatitude",path.get(x).lat());
            jsonRecord.addProperty("angle",anglesInPath.get(x));
            jsonRecord.addProperty("toLongitude",path.get(x+1).lng());
            jsonRecord.addProperty("toLatitude",path.get(x+1).lat());
            records.add(jsonRecord);
        }
        return records;
    }

    //gets geoJson path drone goes through
    JsonArray geoJsonPath()
    {
        JsonArray coordinatePath = new JsonArray();
        for (int x = 0; x < path.size();x++)
        {
            LngLat point = path.get(x);
            double pointLng = point.lng();
            double pointLat = point.lat();
            JsonArray coordinate = new JsonArray();
            coordinate.add(pointLng);
            coordinate.add(pointLat);
            coordinatePath.add(coordinate);
        }
        return coordinatePath;
    }

    LngLat closestInCentralArea (LngLat point, NamedRegion centralArea, LngLatHandler handler)
    {
        LngLat[] centralAreaVertices = centralArea.vertices();
        double gradient;
        double c;
        double perpendicularGradient;
        double perpendicularC;
        LngLat vertice1;
        LngLat vertice2;
        double closestDistance = Double.MAX_VALUE;
        LngLat closestPoint = new LngLat(0,0);
        double commonX;
        double commonY;
        //loops through all the central area's vertices
        for (int x = 0; x < centralAreaVertices.length; x++)
        {
            double distance;
            LngLat closestPointOnLine;
            if (x == centralAreaVertices.length-1)
            {
                vertice1 = centralAreaVertices[x];
                vertice2 = centralAreaVertices[0];
            }
            else
            {
                vertice1 = centralAreaVertices[x];
                vertice2 = centralAreaVertices[x+1];
            }

            //checks if both vertices have the longitude to avoid divisions by zero
            if (vertice2.lng() == vertice1.lng())
            {
                //checks if point's latitude is between both vertices latitudes
                if ((point.lat() > Math.min(vertice1.lat(), vertice2.lat()))
                        && (point.lat() < Math.max(vertice1.lat(), vertice2.lat())))
                {
                    //if it is the closest point on the line will have the same latitude as the point
                    closestPointOnLine = new LngLat(vertice1.lng(),point.lat());
                    distance = handler.distanceTo(point, closestPointOnLine);
                }
                //else closest point will be one of the vertices
                else {
                    distance = handler.distanceTo(point, vertice1);
                    if (distance > handler.distanceTo(point, vertice2)) {
                        distance = handler.distanceTo(point, vertice2);
                        closestPointOnLine = vertice2;
                    } else {
                        closestPointOnLine = vertice1;
                    }
                }
            }

            //checks if both vertices have the latitude to avoid divisions by zero
            else if (vertice2.lat() == vertice1.lat())
            {
                //checks if point's longitude is between both vertices longitudes
                if ((point.lng() > Math.min(vertice1.lng(), vertice2.lng()))
                        && (point.lat() < Math.max(vertice1.lng(), vertice2.lng())))
                {
                    //if it is the closest point on the line will have the same longitude as the point
                    closestPointOnLine = new LngLat(point.lng(),vertice1.lat());
                    distance = handler.distanceTo(point, closestPointOnLine);
                }
                //else closest point will be one of the vertices
                else {
                    distance = handler.distanceTo(point, vertice1);
                    if (distance > handler.distanceTo(point, vertice2)) {
                        distance = handler.distanceTo(point, vertice2);
                        closestPointOnLine = vertice2;
                    } else {
                        closestPointOnLine = vertice1;
                    }
                }
            }
            else {
                //gets line and perpendicular line's equation
                gradient = (vertice2.lat() - vertice1.lat()) / (vertice2.lng() - vertice1.lng());
                c = vertice1.lat() - gradient * vertice1.lng();
                perpendicularGradient = -(1 / gradient);
                perpendicularC = point.lat() - perpendicularGradient * point.lng();
                //gets longitude value where both lines intersect
                commonX = (perpendicularC - c) / (gradient - perpendicularGradient);

                //checks if longitude value is between both vertices to make sure it is on the line
                if ((commonX > Math.min(vertice1.lng(), vertice2.lng()))
                        && (commonX < Math.max(vertice1.lng(), vertice2.lng())))
                {
                    //gets latitude value where both lines intersect
                    commonY = gradient * commonX + c;
                    closestPointOnLine = new LngLat(commonX, commonY);
                    distance = handler.distanceTo(point, closestPointOnLine);
                }
                //checks which vertice is closest
                else
                {
                    distance = handler.distanceTo(point, vertice1);
                    if (distance > handler.distanceTo(point, vertice2)) {
                        distance = handler.distanceTo(point, vertice2);
                        closestPointOnLine = vertice2;
                    } else {
                        closestPointOnLine = vertice1;
                    }
                }
            }

            //checks if current line has a closer point than the previous
            if (distance < closestDistance)
            {
                closestDistance = distance;
                closestPoint = closestPointOnLine;
            }
        }
        return closestPoint;
    }
}
