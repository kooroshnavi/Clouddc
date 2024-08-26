package ir.tic.clouddc.center;

import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CenterService {

    Location getRefrencedLocation(Long locationId);

    List<Location> getLocationListExcept(List<Long> locationId);

    List<Location> getLocationList();

    void verifyRackDevicePosition(List<Rack> rackList);

    Optional<Location> getLocation(Long locationId);

    ////    Repository Projection name convention: EntityFiled1Field2...Projection
    interface CenterIdNameProjection {
        short getId();

        String getName();
    }

    void updateRackDevicePosition(Long rackId, Set<String> newPositionStringList);

    LocationStatus getCurrentLocationStatus(Location location);

    Model getCenterLandingPageModel(Model model);

    List<CenterIdNameProjection> getCenterIdAndNameList();

    Model modelForCenterController(Model model);
}
