package ir.tic.clouddc.cloud;

public interface CloudService {

    void saveManualData(ManualData manualData);

    Ceph getXasCurrentCephData();

    interface ProviderIdNameProjection {
        int getId();

        String getName();

        char getType();
    }
}
