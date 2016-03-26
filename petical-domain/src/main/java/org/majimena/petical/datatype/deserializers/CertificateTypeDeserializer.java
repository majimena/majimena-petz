package org.majimena.petical.datatype.deserializers;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.datatype.CertificateType;

/**
 * 証明書タイプのデシリアライザ.
 */
public class CertificateTypeDeserializer extends EnumDataTypeDeserializer<CertificateType> {
    @Override
    protected CertificateType newEnumDataType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return CertificateType.valueOf(name);
    }
}
