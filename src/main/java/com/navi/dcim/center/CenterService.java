package com.navi.dcim.center;

import java.util.List;

public interface CenterService {
    Center getCenter(int centerId);
    List<Center> getCenterList();
}
