package org.majimena.petz.web.api.examination;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ScheduleService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by todoken on 2015/10/17.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicScheduleStatusController {

    @Inject
    private ScheduleService scheduleService;

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/schedules/{scheduleId}/statuses", method = RequestMethod.POST)
    public ResponseEntity<Schedule> put(@PathVariable String clinicId, @PathVariable String scheduleId) {
        // クリニックの権限チェックとIDのコード体系チェック
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        ErrorsUtils.throwIfNotIdentify(scheduleId);

        // スケジュールを更新する
        Schedule created = scheduleService.signalScheduleStatus(scheduleId);
        return ResponseEntity.ok().body(created);
    }
}
