package com.example.ecommerce.models;

import jakarta.persistence.*;

import java.time.Instant;

// TODO delete CSV content column!!
@Entity
public class EnergyMonitoringData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // timestamp
    @Column(nullable = false)
    private Instant timestamp;

    // TODO combine _value and _field into an Embeddable
    // private Measurement measurement

    // CTYPE
    @Column(nullable = false)
    private String commodityType;

    // CTYPEC
    @Column(nullable = true)
    private String commodityCategory;

    // ETYPE - priceprovider is the only value that is NOT a DEVICE
    @Column(nullable = false)
    private String deviceType;

    // Lob = "large object"
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    // getters/setters

    public EnergyMonitoringData() {
    }

    public EnergyMonitoringData(Long id, Instant timestamp, String commodityType, String commodityCategory, String deviceType, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.commodityType = commodityType;
        this.commodityCategory = commodityCategory;
        this.deviceType = deviceType;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}