package auto.group.integration;

import auto.group.integration.BackendModels.NearbyStation;
import auto.group.integration.BackendModels.RentalCar;
import auto.group.integration.BackendModels.RentalLocation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/api/cars")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class NearbyStationsResource {
    @Inject
    @RestClient
    CarRentalClient carRentalClient;

    @Inject
    FuelStationProvider fuelStationProvider;

    @GET
    @Path("/{carId}/nearby-stations")
    public List<NearbyStation> findNearbyStations(
            @PathParam("carId") Long carId,
            @HeaderParam("Authorization") String authorization) {
        RentalCar car = fetchCar(carId, authorization);
        if (car == null || car.currentLocationId() == null) {
            throw new NotFoundException("Car or its current location was not found");
        }

        RentalLocation location = fetchLocation(car.currentLocationId(), authorization);
        if (location == null || location.city() == null || location.city().isBlank()) {
            throw new WebApplicationException("The car location has no city", 502);
        }

        return fuelStationProvider.listInCity(location.city());
    }

    private RentalCar fetchCar(Long carId, String authorization) {
        try {
            return carRentalClient.getCar(carId, authorization);
        } catch (WebApplicationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new WebApplicationException("Car rental service is unavailable", 502);
        }
    }

    private RentalLocation fetchLocation(Long locationId, String authorization) {
        try {
            return carRentalClient.getLocation(locationId, authorization);
        } catch (WebApplicationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new WebApplicationException("Car rental service is unavailable", 502);
        }
    }
}
