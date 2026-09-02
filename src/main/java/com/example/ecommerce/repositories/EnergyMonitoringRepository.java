package com.example.ecommerce.repositories;

import com.example.ecommerce.models.EnergyMonitoringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EnergyMonitoringRepository extends JpaRepository<EnergyMonitoringData, Long> {

    // Derived query: add Site to the method name and a parameter.
    List<EnergyMonitoringData> findAllBySiteAndTimestampBetween(
            String site, Instant startOfDay, Instant endOfWindow);

    @Query("""
        SELECT COALESCE(SUM(e.value), 0)
        FROM EnergyMonitoringData e
        WHERE e.site = :site
          AND e.field = :field
          AND e.commodityCategory = :category
          AND e.timestamp >= :start
          AND e.timestamp <= :now
        """)
    double sumPower(@Param("site") String site,
                    @Param("field") String field,
                    @Param("category") String category,
                    @Param("start") Instant start,
                    @Param("now") Instant now);

    // Derived query: add Site.
    List<EnergyMonitoringData> findAllBySiteAndTimestampLessThan(
            String site, Instant todayDate);

    @Query("""
        SELECT COALESCE(SUM(e.value), 0.0)
        FROM EnergyMonitoringData e
        WHERE e.site = :site
          AND e.timestamp < :timestamp
          AND e.field = :field
          AND e.commodityCategory IN :commodityCategories
        """)
    double sumValueBySiteAndTimestampBeforeAndFieldAndCommodityCategoryIn(
            @Param("site") String site,
            @Param("timestamp") Instant timestamp,
            @Param("field") String field,
            @Param("commodityCategories") List<String> commodityCategories);

    @Query("""
        SELECT HOUR(e.timestamp) AS hourOfDay,
               AVG(e.value) AS avgSupplyPower
        FROM EnergyMonitoringData e
        WHERE e.site = :site
          AND e.field = 'POW'
          AND e.commodityCategory = 'supply'
          AND e.timestamp >= :windowStart
          AND e.timestamp <= :windowEnd
        GROUP BY HOUR(e.timestamp)
        ORDER BY hourOfDay
        """)
    List<Object[]> avgSupplyByHour(@Param("site") String site,
                                   @Param("windowStart") Instant windowStart,
                                   @Param("windowEnd") Instant windowEnd);

    @Query("""
    SELECT COALESCE(MAX(e.value), 0) - COALESCE(MIN(e.value), 0)
    FROM EnergyMonitoringData e
    WHERE e.site = :site
      AND e.deviceType = :device
      AND e.field = :counterField
      AND e.commodityCategory = :category
      AND e.timestamp >= :monthStart
      AND e.timestamp <= :monthEnd
    """)
    double counterDelta(@Param("site") String site,
                        @Param("device") String device,
                        @Param("counterField") String counterField,
                        @Param("category") String category,
                        @Param("monthStart") Instant monthStart,
                        @Param("monthEnd") Instant monthEnd);
}