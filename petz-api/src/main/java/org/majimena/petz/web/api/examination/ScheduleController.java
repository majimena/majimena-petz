package org.majimena.petz.web.api.examination;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * ユーザスケジュールコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    /**
     * スケジュールサービス.
     */
    @Inject
    private ScheduleService scheduleService;

    /**
     * スケジュールバリデータ.
     */
    @Inject
    private ScheduleValidator scheduleValidator;

    /**
     * スケジュールサービスを設定する.
     *
     * @param scheduleService スケジュールサービス
     */
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * スケジュールバリデータを設定する.
     *
     * @param scheduleValidator スケジュールバリデータ
     */
    public void setScheduleValidator(ScheduleValidator scheduleValidator) {
        this.scheduleValidator = scheduleValidator;
    }

    /**
     * ログインユーザの診察スケジュールを取得する.
     *
     * @param criteria スケジュールクライテリア
     * @return 該当するスケジュール一覧
     */
    @Timed
    @RequestMapping(value = "/schedules", method = RequestMethod.GET)
    public ResponseEntity<List<Schedule>> get(@Valid ScheduleCriteria criteria) {
        // ログインユーザIDで検索する
        String userId = SecurityUtils.getCurrentUserId();
        criteria.setUserId(userId);

        // 月単位でスケジュールを取得する
        List<Schedule> schedules = scheduleService.getSchedulesByScheduleCriteria(criteria);
        return ResponseEntity.ok().body(schedules);
    }
}
