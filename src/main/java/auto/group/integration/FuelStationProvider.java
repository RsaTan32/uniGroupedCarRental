package auto.group.integration;

import auto.group.integration.BackendModels.FallbackStation;
import auto.group.integration.BackendModels.NearbyStation;
import auto.group.integration.BackendModels.PrimaryStation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class FuelStationProvider {
    private static final Logger LOG = Logger.getLogger(FuelStationProvider.class);

    @Inject
    @RestClient
    PrimaryFuelStationsClient primaryClient;

    @Inject
    @RestClient
    FallbackFuelStationsClient fallbackClient;

    public List<NearbyStation> listInCity(String city) {
        try {
            return primaryClient.listStations().stream()
                    .filter(station -> sameCity(station.city(), city))
                    .map(FuelStationProvider::toNearbyStation)
                    .toList();
        } catch (RuntimeException primaryFailure) {
            LOG.warn("Primary fuel station API failed; trying fallback API", primaryFailure);
            try {
                return fallbackClient.listStations().stream()
                        .filter(station -> addressContainsCity(station.cim(), city))
                        .map(station -> toNearbyStation(station, city))
                        .toList();
            } catch (RuntimeException fallbackFailure) {
                LOG.error("Both fuel station APIs failed", fallbackFailure);
                throw new WebApplicationException("Fuel station services are unavailable", 502);
            }
        }
    }

    static boolean sameCity(String stationCity, String requestedCity) {
        return stationCity != null && requestedCity != null
                && stationCity.trim().equalsIgnoreCase(requestedCity.trim());
    }

    static boolean addressContainsCity(String address, String requestedCity) {
        return address != null && requestedCity != null
                && address.toLowerCase(Locale.ROOT).contains(requestedCity.trim().toLowerCase(Locale.ROOT));
    }

    private static NearbyStation toNearbyStation(PrimaryStation station) {
        return new NearbyStation(station.id(), station.name(), station.city(), null, "stock-api");
    }

    private static NearbyStation toNearbyStation(FallbackStation station, String city) {
        return new NearbyStation(station.id(), station.nev(), city, station.cim(), "station-api");
    }
}
