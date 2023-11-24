package com.navi.dcim.center;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    public Center getCenter(int centerId) {
        return centerRepository.findById(centerId).get();
    }

    @Override
    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }
}
