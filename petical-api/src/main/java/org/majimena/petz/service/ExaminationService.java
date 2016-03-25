package org.majimena.petz.service;

import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.ticket.ExaminationCriteria;

import java.util.List;
import java.util.Optional;

/**
 * 診察サービス.
 */
public interface ExaminationService {

    /**
     * 診察クライテリアをもとに、診察を検索する.
     *
     * @param criteria 診察クライテリア
     * @return 該当する診察の一覧
     */
    List<Examination> getExaminationsByExaminationCriteria(ExaminationCriteria criteria);

    /**
     * 診察IDをもとに、診察を取得する.
     *
     * @param examinationId 診察ID
     * @return 該当する診察
     */
    Optional<Examination> getExaminationByExaminationId(String examinationId);

    /**
     * 診察を新規作成する.
     *
     * @param examination 診察
     * @return 登録した診察
     */
    Examination saveExamination(Examination examination);

    /**
     * 診察を更新する.
     *
     * @param examination 診察
     * @return 更新した診察
     */
    Examination updateExamination(Examination examination);

    /**
     * 診察のIDをもとに、診察を削除する.
     *
     * @param clinicId      クリニックID
     * @param ticketId      チケットID
     * @param examinationId 診察ID
     */
    void deleteExaminationByExaminationId(String clinicId, String ticketId, String examinationId);
}
