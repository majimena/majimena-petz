package org.majimena.petical.service;

import org.majimena.petical.domain.Medicine;

import java.util.List;

/**
 * 動物病院医薬品サービス.
 */
public interface ClinicMedicineService {

    /**
     * 動物病院IDをもとに全ての医薬品を取得する.
     *
     * @param clinicId 動物病院ID
     * @return 医薬品
     */
    List<Medicine> getMedicinesByClinicId(String clinicId);

}
