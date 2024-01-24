package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandlerTest extends TestCase {

    LngLatHandler test = new LngLatHandler();
    public void testDistanceTo() {
        LngLat point1 = new LngLat(0,0);
        LngLat point2 = new LngLat(0,5);
        assertEquals(5.0,test.distanceTo(point1,point2));
    }

    public void testIsCloseTo() {
        LngLat point1 = new LngLat(0,0);
        LngLat point2 = new LngLat(0,0.00014);
        assertTrue(test.isCloseTo(point1,point2));
    }

    public void testIsNotCloseTo() {
        LngLat point1 = new LngLat(0,0);
        LngLat point2 = new LngLat(0,0.00015);
        assertFalse(test.isCloseTo(point1,point2));
    }

    public void testIsInCentralArea() {
        LngLat point = new LngLat(0.5,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInCentralArea(point,centralArea));
    }


    public void testIsOutCentralArea() {
        LngLat point = new LngLat(1.5,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertFalse(test.isInCentralArea(point,centralArea));
    }

    public void testIsInRegion() {
        LngLat point = new LngLat(0.5,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInCentralArea(point,centralArea));
    }

    public void testIsInRegionEdge() {
        LngLat point = new LngLat(0.5,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionCorner() {
        LngLat point = new LngLat(1,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionTriangle() {
        LngLat point = new LngLat(0.25,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionTriangleEdge() {
        LngLat point = new LngLat(0.5,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }


    public void testIsInRegionTriangleCorner() {
        LngLat point = new LngLat(0,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsOutRegionTriangle() {
        LngLat point = new LngLat(1,0);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertFalse(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionPentagon() {
        LngLat point = new LngLat(0.5,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0), new LngLat(0.5,-0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionPentagonEdge() {
        LngLat point = new LngLat(0.5,-0.25);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0), new LngLat(0.5,-0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionPentagonCorner() {
        LngLat point = new LngLat(1,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0), new LngLat(0.5,-0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionHexagon() {
        LngLat point = new LngLat(0.1, -0.1);
        LngLat[] vertices = {new LngLat(0, 0), new LngLat(0, 1), new LngLat(1, 1), new LngLat(1, 0), new LngLat(0.75, -0.5), new LngLat(0.25, -0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point, centralArea));
    }

    public void testIsInRegionHexagonEdge() {
        LngLat point = new LngLat(0.5,0.5);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0), new LngLat(0.75,-0.5), new LngLat(0.25,-0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsInRegionHexagonCorner() {
        LngLat point = new LngLat(1,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0), new LngLat(0.75,-0.5), new LngLat(0.25,-0.5)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertTrue(test.isInRegion(point,centralArea));
    }

    public void testIsOutOfRegion() {
        LngLat point = new LngLat(1.1,1);
        LngLat[] vertices = {new LngLat(0,0),new LngLat(0,1),new LngLat(1,1),new LngLat(1,0)};
        NamedRegion centralArea = new NamedRegion("Central Area", vertices);
        assertFalse(test.isInRegion(point,centralArea));
    }

    public void testNextPosition() {
        LngLat startPoint = new LngLat(0.0,0.0);
        assertEquals(0.0,test.nextPosition(startPoint,0).lat());
        assertEquals(0.00015,test.nextPosition(startPoint,0).lng());
    }
}