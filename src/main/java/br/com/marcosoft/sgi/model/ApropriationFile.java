package br.com.marcosoft.sgi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApropriationFile {
    private List<TaskRecord> tasksRecords = new ArrayList<TaskRecord>();
    private Map<String, String> config = new HashMap<String, String>();

    public List<TaskRecord> getTasksRecords() {
        return tasksRecords;
    }
    public void setTasksRecords(List<TaskRecord> tasksRecords) {
        this.tasksRecords = tasksRecords;
    }
    public Map<String, String> getConfig() {
        return config;
    }
    public void setConfig(Map<String, String> config) {
        this.config = config;
    }



}
