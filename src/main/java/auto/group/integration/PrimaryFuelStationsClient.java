package auto.group.integration;

import auto.group.integration.BackendModels.PrimaryStation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/stations")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "fuel-stations-primary")
public interface PrimaryFuelStationsClient {
    @GET
    List<PrimaryStation> listStations();
}
