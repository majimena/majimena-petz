package org.majimena.petz.repository;

import org.majimena.petz.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * スケジュールリポジトリ.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, String>, JpaSpecificationExecutor<Schedule> {

}
