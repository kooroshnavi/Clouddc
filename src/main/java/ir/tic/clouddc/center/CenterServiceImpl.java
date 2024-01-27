package ir.tic.clouddc.center;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;
    private final SalonChecklistRepository salonChecklistRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, SalonChecklistRepository salonChecklistRepository) {
        this.centerRepository = centerRepository;
        this.salonChecklistRepository = salonChecklistRepository;
    }

    @Override
    public Salon getCenter(int centerId) {
        return centerRepository.findById(centerId).get();
    }

    @Override
    public List<Salon> getDefaultCenterList() {
        List<Integer> centerIds = Arrays.asList(1, 2, 3);
        return centerRepository.findAllByIdIn(centerIds);
    }

    @Override
    public List<SalonChecklist> getTodayCenterPmList(LocalDate due) {
        return salonChecklistRepository.findByDue(due);
    }

    @Override
    public List<Salon> getCenterList() {
        return centerRepository.findAll();
    }
}
