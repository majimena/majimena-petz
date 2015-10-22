package org.majimena.petz.service;

import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;

import java.util.List;
import java.util.Optional;

/**
 * スケジュールサービス.
 */
public interface ScheduleService {

    /**
     * スケジュールクライテリアをもとに、スケジュールを検索する.
     *
     * @param criteria スケジュールクライテリア
     * @return 該当するスケジュールの一覧
     */
    List<Schedule> getSchedulesByScheduleCriteria(ScheduleCriteria criteria);

    /**
     * スケジュールIDをもとに、スケジュールを取得する.
     *
     * @param scheduleId スケジュールID
     * @return 該当するスケジュール
     */
    Optional<Schedule> getScheduleByScheduleId(String scheduleId);

    /**
     * スケジュールを新規作成する.
     *
     * @param schedule スケジュール
     * @return 登録したスケジュール
     */
    Schedule saveSchedule(Schedule schedule);

    /**
     * スケジュールを更新する.
     *
     * @param schedule スケジュール
     * @return 更新したスケジュール
     */
    Schedule updateSchedule(Schedule schedule);

    /**
     * スケジュールのIDをもとに、スケジュールを削除する.
     *
     * @param scheduleId スケジュールのID
     */
    void deleteScheduleByScheduleId(String scheduleId);

    /**
     * スケジュールのステータスを次に進める.
     *
     * @param scheduleId スケジュールのID
     * @return 更新したスケジュール
     */
    Schedule signalScheduleStatus(String scheduleId);
}
