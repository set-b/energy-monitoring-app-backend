package com.example.ecommerce.repositories;

import com.example.ecommerce.models.EnergyMonitoringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * This repository is used for saving CSV file as a String. **This will be changed in the future**
 * Long is the {id} of the CSV file being loaded
 */
@Repository
public interface EnergyMonitoringRepository extends JpaRepository<EnergyMonitoringData, Long> {
    List<EnergyMonitoringData> findAllByTimestampBetween(Instant startOfDay, Instant endOfWindow);

    @Query("""
        SELECT COALESCE(SUM(e.value), 0)
        FROM EnergyMonitoringData e
        WHERE e.field = :field
          AND e.commodityCategory = :category
          AND e.timestamp >= :start
          AND e.timestamp <= :now
        """)
    double sumPower(@Param("field") String field,
                    @Param("category") String category,
                    @Param("start") Instant start,
                    @Param("now") Instant now);

    List<EnergyMonitoringData> findAllByTimestampLessThan(Instant todayDate);


    @Query("""
    SELECT COALESCE(SUM(e.value), 0.0)
    FROM EnergyMonitoringData e
    WHERE e.timestamp < :timestamp
      AND e.field = :field
      AND e.commodityCategory IN :commodityCategories
    """)
    double sumValueByTimestampBeforeAndFieldAndCommodityCategoryIn(
            @Param("timestamp") Instant timestamp,
            @Param("field") String field,
            @Param("commodityCategories")
            List<String> commodityCategories
    );



    @Query("""
    SELECT HOUR(e.timestamp) AS hourOfDay,
           AVG(e.value) AS avgSupplyPower
    FROM EnergyMonitoringData e
    WHERE e.field = 'POW'
      AND e.commodityCategory = 'supply'
      AND e.timestamp >= :windowStart
      AND e.timestamp <= :windowEnd
    GROUP BY HOUR(e.timestamp)
    ORDER BY hourOfDay
    """)
    List<Object[]> avgSupplyByHour(@Param("windowStart") Instant windowStart,
                                   @Param("windowEnd") Instant windowEnd);
}
