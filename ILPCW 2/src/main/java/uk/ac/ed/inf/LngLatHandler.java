package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.CentralRegionVertexOrder;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.*;
import java.awt.geom.Path2D;


public class LngLatHandler implements LngLatHandling {


    public double distanceTo(uk.ac.ed.inf.ilp.data.LngLat startPosition, uk.ac.ed.inf.ilp.data.LngLat endPosition) {
        return (Math.sqrt(Math.pow((startPosition.lng()- endPosition.lng()),2) + Math.pow((startPosition.lat()- endPosition.lat()),2)));
    }

    public boolean isCloseTo(uk.ac.ed.inf.ilp.data.LngLat startPosition, uk.ac.ed.inf.ilp.data.LngLat otherPosition) {
        if (distanceTo(startPosition,otherPosition) >= SystemConstants.DRONE_IS_CLOSE_DISTANCE)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean isInCentralArea(uk.ac.ed.inf.ilp.data.LngLat point, NamedRegion centralArea) {
        return isInRegion(point, centralArea);
    }

    public boolean isInRegion(uk.ac.ed.inf.ilp.data.LngLat position, NamedRegion region) {
        //finds is a point is in a region by drawing a line from the point to infinity and checks how many lines it crosses, if odd it is in the region, if even it isn't
        double gradient = 0;
        uk.ac.ed.inf.ilp.data.LngLat point1;
        uk.ac.ed.inf.ilp.data.LngLat point2;
        int linesCrossedRight = 0;
        int linesCrossedLeft = 0;
        int verticesCrossedRight = 0;
        int verticesCrossedLeft = 0;
        double maxLng = region.vertices()[0].lng();
        double minLng = region.vertices()[0].lng();
        double latOnLine;
        //loop through all the vertices in the region
        for (int i = 0; i < region.vertices().length; i++)
        {
            //makes sure array never goes out of bounds
            if (i == region.vertices().length -1)
            {
                point2 = region.vertices()[0];
                point1 = region.vertices()[i];
            }
            else
            {
                point2 = region.vertices()[i+1];
                point1 = region.vertices()[i];
            }
            //maximum and minimum latitude found for certain edge cases
            if (point1.lng() > maxLng)
            {
                maxLng = point1.lng();
            }
            else if (point1.lng() < minLng)
            {
                minLng = point1.lng();
            }

            //if position falls between both points longitude then it is possible the line can be crossed
            if (position.lng() >= Math.min(point1.lng(),point2.lng()) && (position.lng() <= Math.max(point1.lng(),point2.lng())))
            {
                //point's latitude on polygon line found where it would intersect with line from point to infinity
                gradient = (point2.lat() - point1.lat())/(point2.lng() - point1.lng());
                latOnLine = point1.lat() - gradient * (point1.lng() - position.lng());

                //checks if line from point to infinity would intersect with line on polygon
                if (latOnLine >= position.lat())
                {
                    //checks if vertices are crossed for edge cases
                    if ((latOnLine == point1.lat()) ^ (latOnLine == point2.lat()))
                    {
                        verticesCrossedRight ++;
                    }
                    else
                    {
                        linesCrossedRight++;
                    }
                }
                // line from point to negative infinity also checked for edge cases
                else if(latOnLine <= position.lat())
                {
                    if ((latOnLine == point1.lat()) ^ (latOnLine == point2.lat()))
                    {
                        verticesCrossedLeft ++;
                    }
                    else
                    {
                        linesCrossedLeft++;
                    }
                }
            }
        }
        //checks for certain edge cases
        if (verticesCrossedRight == 0)
        {
            return linesCrossedRight % 2 == 1;
        }
        else if (verticesCrossedLeft == 0)
        {
            return linesCrossedLeft % 2 == 1;
        }
        else if (!(position.lng() == maxLng || position.lng() == minLng))
        {
            return true;
        }
        else
        {
            return (linesCrossedRight + linesCrossedLeft) % 2 == 1;
        }
    }

    public uk.ac.ed.inf.ilp.data.LngLat nextPosition(uk.ac.ed.inf.ilp.data.LngLat startPosition, double angle) {
        double nextLng = startPosition.lng() + SystemConstants.DRONE_MOVE_DISTANCE * (Math.cos(Math.toRadians(angle)));
        double nextLat = startPosition.lat() + SystemConstants.DRONE_MOVE_DISTANCE * (Math.sin(Math.toRadians(angle)));
        return new uk.ac.ed.inf.ilp.data.LngLat(nextLng, nextLat);
    }
}
