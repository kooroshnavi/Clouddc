package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskStatus;
import com.navi.dcim.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestUserController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    private PersianDate getTime() {
        PersianDate today = PersianDate.now();
        return today;
    }

    @GetMapping("/api/gettodaytasks")
    private List<Task> getTasks() {
        return taskService.getTaskList();
    }

    @GetMapping("/api/gettaskstatus")
    private List<TaskStatus> getTaskStatus() {
        return taskService.getTaskStatus();
    }

    /*@GetMapping("/api/usertasks/{id}")
    private List<Task> getUserTask(@PathVariable int id) {
        return taskService.getUserTask(id);
    }*/

    @GetMapping("/api/settodaytask")
    private void setTodayTasks() {
        taskService.updateTodayTasks();
    }


}
