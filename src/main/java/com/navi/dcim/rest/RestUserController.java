package com.navi.dcim.rest;

import com.navi.dcim.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestUserController {


    private final TaskService taskService;

    @Autowired
    public RestUserController(TaskService taskService) {
        this.taskService = taskService;
    }

   /* @GetMapping("/")
    private PersianDate getTime() {
        PersianDate today = PersianDate.now();
        return today;
    }*/

  /*  @GetMapping("/api/gettodaytasks")
    private List<Task> getTasks() {
        return taskService.getTaskList();
    }

    @GetMapping("/api/gettaskstatus")
    private List<TaskStatus> getTaskStatus() {
        return taskService.getTaskStatus();
    }*/

    /*@GetMapping("/api/usertasks/{id}")
    private List<Task> getUserTask(@PathVariable int id) {
        return taskService.getUserTask(id);
    }*/

  @GetMapping("/api/settodaytask")
    private void setTodayTasks() {
      taskService.updateTodayTasks();
    }


}
