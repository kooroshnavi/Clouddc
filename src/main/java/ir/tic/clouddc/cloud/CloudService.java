package ir.tic.clouddc.cloud;

import ir.tic.clouddc.api.response.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CloudService {

    List<ServiceHistory> getServiceHistory(int serviceType, int providerID);

    Optional<? extends CloudProvider> getCloudProvider(int cloudProviderId);

    interface CloudProviderIDLocalDateProjection{
        int getId();

        LocalDateTime getDate();
    }

    void saveManualData(ManualData manualData);

    Optional<? extends CloudProvider> getCurrentService(int serviceType, int providerID);

    Response getXasCephUsageData();

    Optional<? extends CloudProvider> getServiceStatus(Integer serviceType);
}
