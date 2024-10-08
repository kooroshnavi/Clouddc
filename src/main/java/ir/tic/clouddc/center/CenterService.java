package ir.tic.clouddc.center;

import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CenterService {

    long HALL_1_ID = 10000;
    long HALL_2_ID = 10001;
    long HALL_CR2_ID = 10002;
    long ROOM_1_ID = 10003;
    long ROOM_2_ID = 10004;
    long ROOM_412_ID = 10005;

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

    Model getCenterLandingPageModel(Model model);

    List<CenterIdNameProjection> getCenterIdAndNameList();

    Model modelForCenterController(Model model);
}
