package auto.group.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public final class BackendModels {
    private BackendModels() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RentalCar(Long id, Long currentLocationId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RentalLocation(Long id, String name, String city, String address) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PrimaryStation(Long id, String name, String city) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FallbackStation(Long id, String nev, String cim) {
    }

    public record NearbyStation(Long id, String name, String city, String address, String source) {
    }
}
