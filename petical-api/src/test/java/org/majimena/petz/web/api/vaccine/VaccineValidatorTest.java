package org.majimena.petz.web.api.vaccine;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.Vaccine;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ProductRepository;
import org.majimena.petz.repository.VaccineRepository;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.web.api.product.ProductValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see VaccineValidator
 */
public class VaccineValidatorTest {

    @Tested
    private VaccineValidator sut = new VaccineValidator();
    @Injectable
    private ClinicRepository clinicRepository;
    @Injectable
    private VaccineRepository vaccineRepository;
    @Mocked
    private SecurityUtils securityUtils;

    protected static Vaccine newVaccine() {
        return Vaccine.builder()
            .id("vaccine1")
            .clinic(Clinic.builder().id("1").build())
            .name("12345678901234567890123456789012345678901234567890")
            .memo("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
            .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        new NonStrictExpectations() {{
            clinicRepository.findOne("1");
            result = Clinic.builder().id("1").build();
            vaccineRepository.findOne("vaccine1");
            result = Vaccine.builder().id("vaccine1").clinic(Clinic.builder().id("1").build()).build();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void IDが指定されていない場合でもエラーにならないこと() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        new NonStrictExpectations() {{
            clinicRepository.findOne("1");
            result = Clinic.builder().id("1").build();
        }};

        data.setId(null);
        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void IDが存在しない場合はエラーになること() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        new NonStrictExpectations() {{
            vaccineRepository.findOne("vaccine1");
            result = null;
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_999998"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void IDが別クリニックのデータである場合は例外になること() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        new NonStrictExpectations() {{
            vaccineRepository.findOne("vaccine1");
            result = Vaccine.builder().id("vaccine1").clinic(Clinic.builder().id("999").build()).build();
        }};

        sut.validate(Optional.of(data), errors);
    }

    @Test
    public void クリニックが存在しない場合はエラーになること() throws Exception {
        Vaccine data = newVaccine();
        Errors errors = new BindException(data, "vaccine");

        new NonStrictExpectations() {{
            clinicRepository.findOne("1");
            result = null;
        }};

        data.setId(null);
        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_001999"));
    }
}
