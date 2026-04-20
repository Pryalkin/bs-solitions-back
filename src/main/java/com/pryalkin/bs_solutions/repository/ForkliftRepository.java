package com.pryalkin.bs_solutions.repository;

import com.pryalkin.bs_solutions.model.Forklift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForkliftRepository extends JpaRepository<Forklift, Long> {
    List<Forklift> findByNumberContainingIgnoreCase(String number);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Downtime d WHERE d.forklift.id = :forkliftId")
    boolean hasDowntimes(@Param("forkliftId") Long forkliftId);
}
