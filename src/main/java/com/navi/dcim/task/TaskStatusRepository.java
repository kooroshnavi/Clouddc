package com.navi.dcim.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {

    List<TaskStatus> findByActive(boolean active);

    List<TaskStatus> findBynextDue(LocalDate date);


}
