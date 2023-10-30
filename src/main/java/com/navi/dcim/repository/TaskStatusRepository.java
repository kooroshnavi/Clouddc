package com.navi.dcim.repository;

import com.navi.dcim.model.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
    @Override
    List<TaskStatus> findAll(Sort sort);
}
