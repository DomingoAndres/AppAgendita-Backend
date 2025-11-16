package com.microservice.microservice_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDTO {
    private Long totalTasks;
    private Long pendingTasks;
    private Long inProgressTasks;
    private Long completedTasks;
    private Long overdueTasks;
    private Long todayTasks;
    private Long thisWeekTasks;
    private Double completionRate;
    private Integer averageDurationMinutes;
}