package uk.ac.ed.inf;

import junit.framework.TestCase;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetClientTest extends TestCase
{
    private static final String VALID_BASE_URL = "https://ilp-rest.azurewebsites.net";

    private GetClient getClient;

    public void setUp() {
        getClient = new GetClient();
    }

    public void testGetFromRest() {
        String[] args = {"2023-11-15", VALID_BASE_URL};
        getClient.getFromRest(args);

        assertNotNull(getClient.getRestaurants());
        assertNotNull(getClient.getOrders());
        assertNotNull(getClient.getCentralArea());
        assertNotNull(getClient.getNoFlyZones());
        assertTrue(getClient.getIsAlive());
    }

    public void testGetFromRestBackslash() {
        String[] args = {"2023-11-15", "https://ilp-rest.azurewebsites.net/"};
        getClient.getFromRest(args);

        assertNotNull(getClient.getRestaurants());
        assertNotNull(getClient.getOrders());
        assertNotNull(getClient.getCentralArea());
        assertNotNull(getClient.getNoFlyZones());
        assertTrue(getClient.getIsAlive());
    }

    public void testShortArgs() {
        String[] args = new String[]{"a"};
        assertThrows(RuntimeException.class, () ->{
            getClient.getFromRest(args);
        });
    }

    public void testInvalidDate() {
        String[] args = {"2023", VALID_BASE_URL};
        assertThrows(RuntimeException.class, () ->{
                getClient.getFromRest(args);
        });
    }

    public void testInvalidURL() {
        String[] args = {"2023-11-15", "VALID_BASE_URL"};
        assertThrows(RuntimeException.class, () ->{
            getClient.getFromRest(args);
        });
    }
}
