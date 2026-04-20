package com.pryalkin.bs_solutions.repository;

import com.pryalkin.bs_solutions.model.Downtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DowntimeRepository extends JpaRepository<Downtime, Long> {
    List<Downtime> findByForkliftIdOrderByStartDateDesc(Long forkliftId);
}