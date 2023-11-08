package com.navi.dcim.repository;

import com.navi.dcim.model.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CenterRepository extends JpaRepository<Center, Integer> {

}




