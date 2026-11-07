package auto.group.integration;

import auto.group.integration.BackendModels.FallbackStation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/kutak")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "fuel-stations-fallback")
public interface FallbackFuelStationsClient {
    @GET
    List<FallbackStation> listStations();
}
