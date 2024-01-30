package ir.tic.clouddc.center;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;
    private final SalonPmDueRepository salonPmDueRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, SalonPmDueRepository salonPmDueRepository) {
        this.centerRepository = centerRepository;
        this.salonPmDueRepository = salonPmDueRepository;
    }

    @Override
    public Salon getSalon(int salonId) {
        return centerRepository.findById(salonId).get();
    }

    @Override
    public List<Salon> getDefaultCenterList() {
        List<Integer> centerIds = Arrays.asList(1, 2, 3);
        return centerRepository.findAllByIdIn(centerIds);
    }

    @Override
    public List<SalonPmDue> getTodayCenterPmList(LocalDate due) {
        return salonPmDueRepository.findByDue(due);
    }

    @Override
    public List<Salon> getSalonList() {
        return centerRepository.findAll();
    }

    @Override
    public List<SalonPmDue> getTodaySalonPmList() {
        return salonPmDueRepository.findByDue(LocalDate.now());
    }
}
