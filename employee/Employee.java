package org.employee;

import org.manager.TaskManager;
import org.task.Status;
import org.task.Task;

public class Employee extends Thread{
    private int ID;
    private String name;
    private double totalActiveTime = 0;
    private double totalInactiveTime = 0;
    private double timeAtWork = 0;
    private double currentActiveTime = 0;

    private TaskManager taskManager;
    private Task currentTask;

    public Employee(int ID, String name, double totalActiveTime, double totalInactiveTime, double timeAtWork) {
        this.ID = ID;
        this.name = name;
        this.totalActiveTime = totalActiveTime;
        this.totalInactiveTime = totalInactiveTime;
        this.timeAtWork = timeAtWork;
    }

    public Employee(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                if (taskManager.isWorking()) {
                    if (currentTask == null) {
                        currentTask = taskManager.getNewTask(ID);
                        if (currentTask == null) {
                            Thread.sleep(10);
                            continue;
                        }
                        else {
                            taskManager.addToLogs("Employee with the ID: " + ID + " started working on task with the ID: " + currentTask.getId());
                            workOnTask(currentTask);
                        }
                    }
                    else {
                        taskManager.addToLogs("Employee with the ID: " + ID + " continued working on task with the ID: " + currentTask.getId());
                        workOnTask(currentTask);
                    }

                    if (currentTask == null) {
                        taskManager.addToLogs("Employee with the ID: " + ID + " completed the task");
                    }
                    else {
                        taskManager.addToLogs("Employee with the ID: " + ID + " paused the task with the ID: " + currentTask.getId());
                    }
                } else {
                    timeAtWork += taskManager.getWorkingDayHours();
                    totalInactiveTime = timeAtWork - totalActiveTime;
                    long sleepTime = (24 - taskManager.getWorkingDayHours()) * taskManager.getHoursToSeconds();
                    Thread.sleep(sleepTime * 1000);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void workOnTask(Task task) {
        int workingDayHours = taskManager.getWorkingDayHours();
        int hoursToSeconds = taskManager.getHoursToSeconds();

        double timeDifference = (task.getRequiredTime() - task.getSpentTime()) * hoursToSeconds;
        int currentTime = (workingDayHours * hoursToSeconds - taskManager.getTimeCounter().getGlobalTime());
        try {
            if (timeDifference > currentTime) {
                task.setSpentTime(task.getSpentTime() + (currentTime / hoursToSeconds));
                Thread.sleep(currentTime * 1000);
                totalActiveTime += currentTime / hoursToSeconds;
            }
            else {
                int randomNum = (int)(Math.random() * hoursToSeconds);
                timeDifference -= randomNum;
                double newTimeDifference = ((double)(Math.round((timeDifference / hoursToSeconds) * 10)) / 10);
                Thread.sleep((int)(timeDifference * 1000));
                task.setSpentTime(task.getSpentTime() + newTimeDifference);
                totalActiveTime += newTimeDifference;
                task.setStatus(Status.DONE);
                currentTask = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public double getTimeAtWork() {
        return timeAtWork;
    }

    public double getTotalInactiveTime() {
        return totalInactiveTime;
    }

    public double getTotalActiveTime() {
        return totalActiveTime;
    }

    public double getCurrentActiveTime() {
        return currentActiveTime;
    }

    public void setCurrentActiveTime(double currentActiveTime) {
        this.currentActiveTime = currentActiveTime;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public int getEmployeeId() {
        return ID;
    }

    public String getEmployeeName() {
        return name;
    }

}
