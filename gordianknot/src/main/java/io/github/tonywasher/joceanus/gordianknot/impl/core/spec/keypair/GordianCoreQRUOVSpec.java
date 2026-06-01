/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianQRUOVSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.qruov.QRUOVParameters;
import org.bouncycastle.pqc.jcajce.spec.QRUOVParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * QRUOV KeySpec.
 */
public final class GordianCoreQRUOVSpec
        implements GordianCoreKeyPairIdSpec<GordianQRUOVSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianQRUOVSpec, GordianCoreQRUOVSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreQRUOVSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreQRUOVSpec[0]);

    /**
     * The Spec.
     */
    private final GordianQRUOVSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreQRUOVSpec(final GordianQRUOVSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.QRUOV;
    }

    @Override
    public GordianQRUOVSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain QRUOV Parameters.
     *
     * @return the parameters.
     */
    public QRUOVParameters getParameters() {
        return switch (theSpec) {
            case QROUV1Q127L3V156M54SHAKE -> QRUOVParameters.qruov_1_q127_L3_v156_m54_shake;
            case QROUV1Q31L3V165M60SHAKE -> QRUOVParameters.qruov_1_q31_L3_v165_m60_shake;
            case QROUV1Q31L10V600M70SHAKE -> QRUOVParameters.qruov_1_q31_L10_v600_m70_shake;
            case QROUV1Q7L10V740M100SHAKE -> QRUOVParameters.qruov_1_q7_L10_v740_m100_shake;
            case QROUV3Q127L3V228M78SHAKE -> QRUOVParameters.qruov_3_q127_L3_v228_m78_shake;
            case QROUV3Q31L3V246M87SHAKE -> QRUOVParameters.qruov_3_q31_L3_v246_m87_shake;
            case QROUV3Q31L10V890M100SHAKE -> QRUOVParameters.qruov_3_q31_L10_v890_m100_shake;
            case QROUV3Q7L10V1100M140SHAKE -> QRUOVParameters.qruov_3_q7_L10_v1100_m140_shake;
            case QROUV5Q127L3V306M105SHAKE -> QRUOVParameters.qruov_5_q127_L3_v306_m105_shake;
            case QROUV5Q31L3V324M114SHAKE -> QRUOVParameters.qruov_5_q31_L3_v324_m114_shake;
            case QROUV5Q7L10V1490M190SHAKE -> QRUOVParameters.qruov_5_q31_L10_v1120_m120_shake;
            case QROUV5Q31L10V1120M120SHAKE -> QRUOVParameters.qruov_5_q7_L10_v1490_m190_shake;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain QRUOV ParameterSpec.
     *
     * @return the parameters.
     */
    public QRUOVParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case QROUV1Q127L3V156M54SHAKE -> QRUOVParameterSpec.qruov1q127L3v156m54;
            case QROUV1Q31L3V165M60SHAKE -> QRUOVParameterSpec.qruov1q31L3v165m60;
            case QROUV1Q31L10V600M70SHAKE -> QRUOVParameterSpec.qruov1q31L10v600m70;
            case QROUV1Q7L10V740M100SHAKE -> QRUOVParameterSpec.qruov1q7L10v740m100;
            case QROUV3Q127L3V228M78SHAKE -> QRUOVParameterSpec.qruov3q127L3v228m78;
            case QROUV3Q31L3V246M87SHAKE -> QRUOVParameterSpec.qruov3q31L3v246m87;
            case QROUV3Q31L10V890M100SHAKE -> QRUOVParameterSpec.qruov3q31L10v890m100;
            case QROUV3Q7L10V1100M140SHAKE -> QRUOVParameterSpec.qruov3q7L10v1100m140;
            case QROUV5Q127L3V306M105SHAKE -> QRUOVParameterSpec.qruov5q127L3v306m105;
            case QROUV5Q31L3V324M114SHAKE -> QRUOVParameterSpec.qruov5q31L3v324m114;
            case QROUV5Q7L10V1490M190SHAKE -> QRUOVParameterSpec.qruov5q31L10v1120m120;
            case QROUV5Q31L10V1120M120SHAKE -> QRUOVParameterSpec.qruov5q7L10v1490m190;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case QROUV1Q127L3V156M54SHAKE -> BCObjectIdentifiers.qruov1q127L3v156m54;
            case QROUV1Q31L3V165M60SHAKE -> BCObjectIdentifiers.qruov1q31L3v165m60;
            case QROUV1Q31L10V600M70SHAKE -> BCObjectIdentifiers.qruov1q31L10v600m70;
            case QROUV1Q7L10V740M100SHAKE -> BCObjectIdentifiers.qruov1q7L10v740m100;
            case QROUV3Q127L3V228M78SHAKE -> BCObjectIdentifiers.qruov3q127L3v228m78;
            case QROUV3Q31L3V246M87SHAKE -> BCObjectIdentifiers.qruov3q31L3v246m87;
            case QROUV3Q31L10V890M100SHAKE -> BCObjectIdentifiers.qruov3q31L10v890m100;
            case QROUV3Q7L10V1100M140SHAKE -> BCObjectIdentifiers.qruov3q7L10v1100m140;
            case QROUV5Q127L3V306M105SHAKE -> BCObjectIdentifiers.qruov5q127L3v306m105;
            case QROUV5Q31L3V324M114SHAKE -> BCObjectIdentifiers.qruov5q31L3v324m114;
            case QROUV5Q7L10V1490M190SHAKE -> BCObjectIdentifiers.qruov5q31L10v1120m120;
            case QROUV5Q31L10V1120M120SHAKE -> BCObjectIdentifiers.qruov5q7L10v1490m190;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public String toString() {
        return theSpec.toString();
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreQRUOVSpec myThat
                && theSpec == myThat.getSpec();
    }

    @Override
    public int hashCode() {
        return theSpec.hashCode();
    }

    /**
     * Obtain the core spec.
     *
     * @param pSpec the base spec
     * @return the core spec
     */
    public static GordianCoreQRUOVSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianQRUOVSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianQRUOVSpec, GordianCoreQRUOVSpec> newSpecMap() {
        final Map<GordianQRUOVSpec, GordianCoreQRUOVSpec> myMap = new EnumMap<>(GordianQRUOVSpec.class);
        for (GordianQRUOVSpec mySpec : GordianQRUOVSpec.values()) {
            myMap.put(mySpec, new GordianCoreQRUOVSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreQRUOVSpec[] values() {
        return VALUES;
    }
}
