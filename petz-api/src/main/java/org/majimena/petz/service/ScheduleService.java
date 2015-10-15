package org.majimena.petz.service;

import org.majimena.petz.domain.Schedule;
import org.majimena.petz.domain.examination.ScheduleCriteria;

import java.util.List;

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
}
