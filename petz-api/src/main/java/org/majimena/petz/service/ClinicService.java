package org.majimena.petz.service;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.domain.clinic.ClinicOutline;
import org.majimena.petz.domain.clinic.ClinicOutlineCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * クリニックサービス.
 */
public interface ClinicService {

    /**
     * クリニック検索条件をもとにクリニックを検索する.
     *
     * @param criteria 検索条件
     * @param pageable ページ
     * @return 該当するクリニック
     */
    Page<Clinic> findClinicsByClinicCriteria(ClinicCriteria criteria, Pageable pageable);

    /**
     * ユーザIDをもとに勤務先のクリニックを取得する.
     *
     * @param userId ユーザID
     * @return 該当するクリニック
     */
    List<Clinic> getMyClinicsByUserId(String userId);

    /**
     * クリニックを取得する.
     *
     * @param clinicId クリニックID
     * @return 該当するクリニック
     */
    Optional<Clinic> getClinicById(String clinicId);

    /**
     * クリニックのアウトライン情報を取得する.
     *
     * @param criteria 検索条件
     * @return 該当するクリニックのアウトライン
     */
    Optional<ClinicOutline> findClinicOutlineByClinicOutlineCriteria(ClinicOutlineCriteria criteria);

    /**
     * クリニックを保存する.
     *
     * @param clinic クリニック
     * @return 保存したクリニック
     */
    Clinic saveClinic(Clinic clinic);

    /**
     * クリニックを変更する.
     *
     * @param clinic クリニック
     * @return 変更したクリニック
     */
    Clinic updateClinic(Clinic clinic);

    /**
     * クリニックを削除する.
     *
     * @param clinicId クリニックID
     */
    void deleteClinic(String clinicId);

    List<ClinicStaff> getClinicStaffsById(String clinicId);

    void deleteClinicStaff(String clinicId, String userId);
}
