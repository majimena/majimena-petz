package org.majimena.petz.web.api.examination;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ScheduleService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * スケジュールコントローラ.
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

    /**
     * スケジュールを新規作成する.
     *
     * @param schedule 登録するスケジュール情報
     * @param errors   エラーオブジェクト
     * @return 登録したスケジュール
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/schedules", method = RequestMethod.POST)
    public ResponseEntity<Schedule> post(@RequestBody @Valid Schedule schedule, BindingResult errors) throws BindException {
        // カスタムバリデーションを行う
        scheduleValidator.validate(schedule, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // スケジュールを保存する
        Schedule created = scheduleService.saveSchedule(schedule);
        return ResponseEntity.created(
                URI.create("/api/v1/schedules/" + created.getId())).body(created);
    }

    /**
     * スケジュールを更新する.
     *
     * @param scheduleId スケジュールID
     * @param schedule   更新するスケジュール情報
     * @param errors     エラーオブジェクト
     * @return 更新したスケジュール
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/schedules/{scheduleId}", method = RequestMethod.PUT)
    public ResponseEntity<Schedule> put(@PathVariable String scheduleId, @RequestBody @Valid Schedule schedule, BindingResult errors) throws BindException {
        // ユーザ権限のチェックとIDのコード体系チェック
        SecurityUtils.throwIfNotCurrentUser(schedule.getUser().getId());
        ErrorsUtils.throwIfNotIdentify(scheduleId);

        // カスタムバリデーションを行う
        scheduleValidator.validate(schedule, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // スケジュールを更新する
        Schedule created = scheduleService.updateSchedule(schedule);
        return ResponseEntity.ok().body(created);
    }

    /**
     * スケジュールを削除する.
     *
     * @param scheduleId スケジュールID
     * @return レスポンスステータス（200）
     */
    @Timed
    @RequestMapping(value = "/schedules/{scheduleId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String scheduleId) {
        // TODO ユーザ権限のチェックとIDのコード体系チェック
//        SecurityUtils.throwIfNotCurrentUser(schedule.getUser().getId());
        ErrorsUtils.throwIfNotIdentify(scheduleId);

        // スケジュールを更新する
        scheduleService.deleteScheduleByScheduleId(scheduleId);
        return ResponseEntity.ok().build();
    }
}
