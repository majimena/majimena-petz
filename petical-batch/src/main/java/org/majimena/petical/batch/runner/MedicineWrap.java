package org.majimena.petical.batch.runner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majimena.petical.domain.Medicine;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 動物用医薬品ドメイン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineWrap implements Serializable {
    private String id;
    private String nvalId;
    private String name;
    private String categoryName;
    private Boolean sideEffect;
    private String medicinalEffectCategory;
    private String packingUnit;
    private String target;
    private String banningPeriod;
    private String effect;
    private String dosage;
    private String attention;
    private String storageCondition;
    private String note;
    private Timestamp modifiedDate;
    private Timestamp approvedDate;
    private String approvedType;
    private Timestamp approvedDate1;
    private Timestamp approvedDate2;
    private Timestamp approvedDate3;
    private Timestamp notifiedDate;
    private Timestamp reExamineResultNoticeDate;
    private String makerOrDealerName;
    private String selectedMakerOrDealerName;
    private String preparationType;
    private String formType;
    private String regulationType;
    private String availablePeriod;
    private String ruminantByProducts;

    private String createdBy;
    private Timestamp createdDate;
    private String lastModifiedBy;
    private Timestamp lastModifiedDate;

    public MedicineWrap(Medicine medicine) {
        this.id = medicine.getId();
        this.nvalId = medicine.getNvalId();
        this.name = medicine.getName();
        this.categoryName = medicine.getCategoryName();
        this.sideEffect = medicine.getSideEffect();
        this.medicinalEffectCategory = medicine.getMedicinalEffectCategory();
        this.packingUnit = medicine.getPackingUnit();
        this.target = medicine.getTarget();
        this.banningPeriod = medicine.getBanningPeriod();
        this.effect = medicine.getEffect();
        this.dosage = medicine.getDosage();
        this.attention = medicine.getAttention();
        this.storageCondition = medicine.getStorageCondition();
        this.note = medicine.getNote();
        this.modifiedDate = of(medicine.getModifiedDate());
        this.approvedDate = of(medicine.getApprovedDate());
        this.approvedType = medicine.getApprovedType();
        this.approvedDate1 = of(medicine.getApprovedDate1());
        this.approvedDate2 = of(medicine.getApprovedDate2());
        this.approvedDate3 = of(medicine.getApprovedDate3());
        this.notifiedDate = of(medicine.getNotifiedDate());
        this.reExamineResultNoticeDate = of(medicine.getReExamineResultNoticeDate());
        this.makerOrDealerName = medicine.getMakerOrDealerName();
        this.selectedMakerOrDealerName = medicine.getSelectedMakerOrDealerName();
        this.preparationType = medicine.getPreparationType();
        this.formType = medicine.getFormType();
        this.regulationType = medicine.getRegulationType();
        this.availablePeriod = medicine.getAvailablePeriod();
        this.ruminantByProducts = medicine.getRuminantByProducts();
        this.createdBy = "batch";
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.lastModifiedBy = "batch";
        this.lastModifiedDate = new Timestamp(System.currentTimeMillis());
    }

    private static Timestamp of(LocalDateTime time) {
        if (time != null) {
            return Timestamp.valueOf(time);
        }
        return null;
    }
}
