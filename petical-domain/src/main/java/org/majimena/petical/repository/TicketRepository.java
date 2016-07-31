package org.majimena.petical.repository;

import org.majimena.petical.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * チケットリポジトリ.
 */
public interface TicketRepository extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    /**
     * 日別のチケット数と売上高を取得する.
     *
     * @param clinicId クリニックID
     * @param start    開始日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param end      終了日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @return 日付文字列（yyyy-MM-dd形式）、売上高、チケット数のオブジェクト配列
     */
    @Query(value = "" +
            "select date_format(end_date_time, '%Y-%m-%d') as date, coalesce(sum(total - discount),0) as sales, count(id) as cnt " +
            "from ticket " +
            "where clinic_id=:clinicId and end_date_time between :startDateTime and :endDateTime " +
            "group by date_format(end_date_time, '%Y-%m-%d')", nativeQuery = true)
    List<Object[]> sumDailySalesAndCount(@Param("clinicId") String clinicId, @Param("startDateTime") String start, @Param("endDateTime") String end);

    /**
     * 月別のチケット数と売上高を取得する.
     *
     * @param clinicId クリニックID
     * @param start    開始日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param end      終了日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @return 日付文字列（yyyy-MM形式）、売上高、チケット数のオブジェクト配列
     */
    @Query(value = "" +
            "select date_format(end_date_time, '%Y-%m') as date, coalesce(sum(total - discount),0) as sales, count(id) as cnt " +
            "from ticket " +
            "where clinic_id=:clinicId and end_date_time between :startDateTime and :endDateTime " +
            "group by date_format(end_date_time, '%Y-%m')", nativeQuery = true)
    List<Object[]> sumMonthlySalesAndCount(@Param("clinicId") String clinicId, @Param("startDateTime") String start, @Param("endDateTime") String end);

    /**
     * 指定のステート以外のチケットを取得する.
     *
     * @param clinicId クリニックID
     * @param start    開始日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param end      終了日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param states   ステート
     * @return 日時文字列（yyyy-MM-dd HH:mm形式）、チケット数のオブジェクト配列
     */
    @Query(value = "" +
            "select date_format(start_date_time, '%Y-%m-%d %H:%i') as date, count(id) as cnt " +
            "from ticket " +
            "where clinic_id=:clinicId and state not in (:states) and start_date_time between :startDateTime and :endDateTime " +
            "group by date_format(start_date_time, '%Y-%m-%d %H:%i')", nativeQuery = true)
    List<Object[]> countNotStateTickets(@Param("clinicId") String clinicId, @Param("startDateTime") String start, @Param("endDateTime") String end, @Param("states") String... states);

    /**
     * 指定のステートのチケットを取得する.
     *
     * @param clinicId クリニックID
     * @param start    開始日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param end      終了日文字列（yyyy-MM-dd HH:mm:ss形式）
     * @param states   ステート
     * @return 日時文字列（yyyy-MM-dd HH:mm形式）、チケット数のオブジェクト配列
     */
    @Query(value = "" +
            "select date_format(start_date_time, '%Y-%m-%d %H:%i') as date, count(id) as cnt " +
            "from ticket " +
            "where clinic_id=:clinicId and state in (:states) and start_date_time between :startDateTime and :endDateTime " +
            "group by date_format(start_date_time, '%Y-%m-%d %H:%i')", nativeQuery = true)
    List<Object[]> countStateTickets(@Param("clinicId") String clinicId, @Param("startDateTime") String start, @Param("endDateTime") String end, @Param("states") String... states);

}
