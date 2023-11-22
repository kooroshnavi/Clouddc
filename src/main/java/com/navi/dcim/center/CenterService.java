package com.navi.dcim.center;

import com.navi.dcim.model.Center;

import java.util.List;

public interface CenterService {
    Center getCenter(int centerId);
    List<Center> getCenterList();
}
