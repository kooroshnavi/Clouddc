package com.navi.dcim.task;


import com.navi.dcim.task.TaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailRepository extends JpaRepository<TaskDetail, Integer> {
    List<TaskDetail> findAllByPerson_IdAndActive(int id, boolean active);
}
