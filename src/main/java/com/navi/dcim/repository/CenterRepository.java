package com.navi.dcim.repository;

import com.navi.dcim.model.Center;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends CrudRepository<Center, Integer> {
}
