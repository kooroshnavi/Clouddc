package com.navi.dcim.center;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CenterServiceImpl implements CenterService {

    private CenterRepository centerRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    public Center getCenter(int centerId) {
        return centerRepository.findById(centerId).get();
    }

    @Override
    public List<Center> getDefaultCenterList() {
        List<Integer> centerIds = Arrays.asList(1, 2, 3);
        return centerRepository.findAllByIdIn(centerIds);
    }

    @Override
    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }
}
