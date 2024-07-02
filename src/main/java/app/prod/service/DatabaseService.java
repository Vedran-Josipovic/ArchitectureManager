package app.prod.service;

import app.prod.model.Address;
import app.prod.model.Location;
import app.prod.model.VirtualLocation;
import app.prod.utils.DatabaseUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseService {
    public static List<Location> getLocationsByType(String type) {
        List<Location> allLocations = DatabaseUtils.getLocations();
        return allLocations.stream()
                .filter(location -> {
                    if (type == null) return false;
                    if (type.equals("Address") && location instanceof Address) return true;
                    if (type.equals("VirtualLocation") && location instanceof VirtualLocation) return true;
                    return false;
                })
                .collect(Collectors.toList());
    }
}
