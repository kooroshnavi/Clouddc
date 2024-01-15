package com.navi.dcim.center;

import java.util.List;

public interface CenterService {
    Center getCenter(int centerId);
    List<Center> getDefaultCenterList();
    List<Center> getCenterList();
}
