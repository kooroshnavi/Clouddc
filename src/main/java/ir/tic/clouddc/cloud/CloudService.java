package ir.tic.clouddc.cloud;

import java.util.Optional;

public interface CloudService {

    void saveManualData(ManualData manualData);

    Optional<Ceph> getXasCurrentCephData();

    interface ProviderIdNameProjection {
        int getId();

        String getName();

        char getType();
    }
}
