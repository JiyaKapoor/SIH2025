package com.OceanHazard.demo.entity;

public class FilterData {
    public String disasterType;
    public Double latitude;   // new field
    public Double longitude;
    public String date;
    public String source;
    public Integer maxDaysOld;   // e.g. last 7 days
    public Double radiusKm;
}
