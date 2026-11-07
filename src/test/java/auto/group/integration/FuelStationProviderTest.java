package auto.group.integration;

import auto.group.integration.BackendModels.FallbackStation;
import auto.group.integration.BackendModels.NearbyStation;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuelStationProviderTest {
    @Test
    void cityMatchingIsCaseInsensitiveAndExact() {
        assertTrue(FuelStationProvider.sameCity("Budapest", "budapest"));
        assertFalse(FuelStationProvider.sameCity("Budapest", "Budapest II."));
    }

    @Test
    void fallbackAddressMustContainRequestedCity() {
        assertTrue(FuelStationProvider.addressContainsCity("Budapest, Andrássy út 1.", "Budapest"));
        assertFalse(FuelStationProvider.addressContainsCity("Szeged, Fő tér 1.", "Budapest"));
    }

    @Test
    void usesFallbackWhenPrimaryApiFails() {
        FuelStationProvider provider = new FuelStationProvider();
        provider.primaryClient = () -> {
            throw new WebApplicationException(500);
        };
        provider.fallbackClient = () -> List.of(new FallbackStation(7L, "Szeged kút", "Szeged, Fő tér 1."));

        List<NearbyStation> stations = provider.listInCity("Szeged");

        assertEquals(1, stations.size());
        assertEquals("station-api", stations.get(0).source());
        assertEquals("Szeged", stations.get(0).city());
    }

    @Test
    void reportsBadGatewayWhenBothApisFail() {
        FuelStationProvider provider = new FuelStationProvider();
        provider.primaryClient = () -> {
            throw new WebApplicationException(500);
        };
        provider.fallbackClient = () -> {
            throw new WebApplicationException(500);
        };

        WebApplicationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                WebApplicationException.class,
                () -> provider.listInCity("Szeged"));

        assertEquals(502, exception.getResponse().getStatus());
    }
}
