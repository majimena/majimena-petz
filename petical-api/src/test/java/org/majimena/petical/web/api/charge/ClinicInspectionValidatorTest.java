package org.majimena.petical.web.api.charge;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicInspection;
import org.majimena.petical.repository.ClinicInspectionRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.security.ResourceCannotAccessException;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.testdata.ClinicChargeDataProvider;
import org.majimena.petical.web.api.clinics.inspections.ClinicInspectionValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicInspectionValidator
 */
public class ClinicInspectionValidatorTest {

    @Mocked
    SecurityUtils securityUtils;
    @Tested
    private ClinicInspectionValidator sut = new ClinicInspectionValidator();
    @Injectable
    private ClinicRepository clinicRepository;
    @Injectable
    private ClinicInspectionRepository clinicInspectionRepository;

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        ClinicInspection data = ClinicChargeDataProvider.newClinicCharge();
        Errors errors = new BindException(data, "clinicCharge");

        new NonStrictExpectations() {{
            clinicInspectionRepository.findOne("2");
            result = data;
            SecurityUtils.throwIfDoNotHaveClinicRoles("1");
            result = null;
            clinicRepository.findOne("1");
            result = Clinic.builder().id("1").build();
        }};

        data.setId("2");
        sut.validate(data, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void IDが指定されていない場合でもエラーにならないこと() throws Exception {
        ClinicInspection data = ClinicChargeDataProvider.newClinicCharge();
        Errors errors = new BindException(data, "clinicCharge");

        new NonStrictExpectations() {{
            clinicRepository.findOne("1");
            result = Clinic.builder().id("1").build();
        }};

        data.setId(null);
        sut.validate(data, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void IDが存在しない場合はエラーになること() throws Exception {
        ClinicInspection data = ClinicChargeDataProvider.newClinicCharge();
        Errors errors = new BindException(data, "clinicCharge");

        new NonStrictExpectations() {{
            clinicInspectionRepository.findOne("2");
            result = null;
        }};

        data.setId("2");
        sut.validate(data, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("errors.required"));
    }

    @Test(expected = ResourceCannotAccessException.class)
    public void IDが別クリニックのデータである場合は例外になること() throws Exception {
        ClinicInspection data = ClinicChargeDataProvider.newClinicCharge();
        Errors errors = new BindException(data, "clinicCharge");

        new NonStrictExpectations() {{
            clinicInspectionRepository.findOne("2");
            result = data;
            SecurityUtils.throwIfDoNotHaveClinicRoles("1");
            result = new ResourceCannotAccessException();
        }};

        data.setId("2");
        sut.validate(data, errors);
    }

    @Test
    public void クリニックが存在しない場合はエラーになること() throws Exception {
        ClinicInspection data = ClinicChargeDataProvider.newClinicCharge();
        Errors errors = new BindException(data, "clinicCharge");

        new NonStrictExpectations() {{
            clinicRepository.findOne("1");
            result = null;
        }};

        data.setId(null);
        sut.validate(data, errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_001999"));
    }
}
