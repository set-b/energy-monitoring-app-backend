package com.example.ecommerce.models;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.*;

import java.time.Instant;

// TODO delete CSV content column!!
@Entity
@Table(indexes = {
        @Index(name = "idx_query", columnList = "site,field,commodity_category,time")
})
public class EnergyMonitoringData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO convert to instant
    @CsvDate("yyyy-MM-dd HH:mm:ssXXX")
    @CsvBindByName(column = "_time")
    @Column(name = "time", nullable =false)
    private Instant timestamp;

    @CsvBindByName(column = "_value")
    @Column(name = "reading_value", nullable = false)
    private double value;

    @CsvBindByName(column = "CTYPE")
    private String commodityType;

    @CsvBindByName(column = "CTYPEC")
    private String commodityCategory;

    @CsvBindByName(column = "ETYPE")
    private String deviceType;

    @CsvBindByName(column = "_field")
    private String field;

    @Column(name = "site", nullable = false)
    private String site;

    public EnergyMonitoringData() {
    }

    public EnergyMonitoringData(Long id, Instant timestamp, double value, String commodityType, String commodityCategory, String deviceType, String field, String site) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
        this.commodityType = commodityType;
        this.commodityCategory = commodityCategory;
        this.deviceType = deviceType;
        this.field = field;
        this.site = site;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(String commodityType) {
        this.commodityType = commodityType;
    }

    public String getCommodityCategory() {
        return commodityCategory;
    }

    public void setCommodityCategory(String commodityCategory) {
        this.commodityCategory = commodityCategory;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}