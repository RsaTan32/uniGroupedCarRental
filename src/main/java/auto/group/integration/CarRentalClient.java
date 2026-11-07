package auto.group.integration;

import auto.group.integration.BackendModels.RentalCar;
import auto.group.integration.BackendModels.RentalLocation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "car-rental")
public interface CarRentalClient {
    @GET
    @Path("/cars/{carId}")
    RentalCar getCar(@PathParam("carId") Long carId, @HeaderParam("Authorization") String authorization);

    @GET
    @Path("/locations/{locationId}")
    RentalLocation getLocation(@PathParam("locationId") Long locationId,
            @HeaderParam("Authorization") String authorization);
}
