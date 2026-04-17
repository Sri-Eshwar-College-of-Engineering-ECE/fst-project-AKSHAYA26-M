package com.symptomchecker.model;

import java.util.List;

public class SymptomRequest {
    private String patientName;
    private int patientAge;
    private String gender;
    private List<String> symptoms;
    private String severity;
    private String duration;

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getPatientAge() { return patientAge; }
    public void setPatientAge(int patientAge) { this.patientAge = patientAge; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
