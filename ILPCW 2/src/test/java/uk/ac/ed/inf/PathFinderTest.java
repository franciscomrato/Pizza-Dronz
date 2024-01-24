package uk.ac.ed.inf;

import junit.framework.Assert;
import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class PathFinderTest extends TestCase
{
    String rest_Url = "https://ilp-rest.azurewebsites.net";

    LngLat vertice1 = new LngLat (-3.190578818321228,55.94402412577528);
    LngLat vertice2 = new LngLat (-3.1899887323379517,55.94284650540911);
    LngLat vertice3 = new LngLat (-3.187097311019897,55.94328811724263);

    LngLat vertice4 = new LngLat (-3.187682032585144,55.944477740393744);
    LngLat vertice5 = new LngLat (-3.190578818321228,55.94402412577528);
    LngLat[] vertices = new LngLat[]{vertice1,vertice2,vertice3,vertice4,vertice5};

    NamedRegion GeorgeSquareArea = new NamedRegion("George Square Area", vertices);
    NamedRegion[] noFlyZones = new NamedRegion[]{GeorgeSquareArea};
    LngLat start = new LngLat(-3.202541470527649,55.943284737579376);
    LngLat dest = new LngLat(-3.1867072925136597,55.944316857024575);
    LngLatHandler lngLatHandler = new LngLatHandler();
    boolean toAppleton = false;
    boolean toCentral = false;

    LngLat vertice6 = new LngLat (-3.192473,55.946233);
    LngLat vertice7 = new LngLat (-3.192473,55.942617);
    LngLat vertice8 = new LngLat (-3.184319,55.942617);

    LngLat vertice9 = new LngLat (-3.184319,55.946233);
    NamedRegion centralArea = new NamedRegion("Central Area", new LngLat[]{vertice6,vertice7,vertice8,vertice9});

    public void testAStar ()
    {
        PathFinder pathFinder = new PathFinder();
        pathFinder.aStar(dest,start,noFlyZones,lngLatHandler,toAppleton,toCentral,centralArea);
        System.out.print(pathFinder.geoJsonPath());
    }

    public void testAStarToCentral ()
    {
        PathFinder pathFinder = new PathFinder();
        toCentral=true;
        start = new LngLat(-3.196746029497433,55.934280089997486);
        dest = new LngLat(-3.192473,55.942617);
        pathFinder.aStar(start,dest,noFlyZones,lngLatHandler,toAppleton,toCentral,centralArea);
        System.out.print(pathFinder.geoJsonPath());
    }

    public void testToAppleton ()
    {
        PathFinder pathFinder = new PathFinder();
        toCentral=false;
        toAppleton=true;
        start = new LngLat(-3.190925205236482,55.94349172948574);
        dest = new LngLat(-3.192473,55.942617);
        pathFinder.aStar(start,dest,noFlyZones,lngLatHandler,toAppleton,toCentral,centralArea);
        System.out.print(pathFinder.geoJsonPath());
    }

    public void testNoFlyZoneStart ()
    {
        PathFinder pathFinder = new PathFinder();
        start = new LngLat(-3.1888292718792, 55.94365962435589);
        Assert.assertEquals(pathFinder.aStar(start,dest,noFlyZones,lngLatHandler,toAppleton,toCentral,centralArea), null);
    }

    public void testNoFlyZoneEnd ()
    {
        PathFinder pathFinder = new PathFinder();
        dest = new LngLat(-3.1888292718792, 55.94365962435589);
        Assert.assertEquals(pathFinder.aStar(start,dest,noFlyZones,lngLatHandler,toAppleton,toCentral,centralArea), null);
    }
}