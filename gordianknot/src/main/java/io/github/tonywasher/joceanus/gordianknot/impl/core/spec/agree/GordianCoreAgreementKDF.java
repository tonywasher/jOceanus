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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;

import java.util.EnumMap;
import java.util.Map;

/**
 * KDF types.
 */
public final class GordianCoreAgreementKDF {
    /**
     * The agreementKDFMap.
     */
    private static final Map<GordianAgreementKDF, GordianCoreAgreementKDF> KDFMAP = newKDFMap();

    /**
     * The kdfTypeArray.
     */
    private static final GordianCoreAgreementKDF[] VALUES = KDFMAP.values().toArray(new GordianCoreAgreementKDF[0]);

    /**
     * The KDF.
     */
    private final GordianAgreementKDF theKDF;

    /**
     * Constructor.
     *
     * @param pKDF the agreementKDF
     */
    private GordianCoreAgreementKDF(final GordianAgreementKDF pKDF) {
        theKDF = pKDF;
    }

    /**
     * Obtain the KDF.
     *
     * @return the KDF
     */
    public GordianAgreementKDF getKDF() {
        return theKDF;
    }

    /**
     * Determine whether this is a supported kdfType.
     *
     * @param pKeyType   pKeyType
     * @param pAgreeType the agreement type
     * @return true/false
     */
    public boolean isSupported(final GordianKeyPairType pKeyType,
                               final GordianAgreementType pAgreeType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case RSA:
                return !isCKDF();
            case EC:
            case SM2:
            case DSTU:
            case GOST:
                return isSupported4EC(pAgreeType);
            case DH:
                return isSupported4DH(pAgreeType);
            case XDH:
                return isSupported4XDH(pAgreeType);
            case CMCE:
            case FRODO:
            case SABER:
            case NEWHOPE:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPLUS:
            case NTRUPRIME:
                return pAgreeType == GordianAgreementType.KEM
                        && theKDF == GordianAgreementKDF.NONE;
            default:
                return true;
        }
    }

    /**
     * Determine whether this is a supported kdfType for RSA.
     *
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4DH(final GordianAgreementType pAgreeType) {
        /* Switch on kdfType */
        switch (theKDF) {
            case SHA256KDF:
            case SHA512KDF:
                return true;
            case SHA256CKDF:
            case SHA512CKDF:
                return pAgreeType == GordianAgreementType.UNIFIED || pAgreeType == GordianAgreementType.MQV;
            case NONE:
                return pAgreeType == GordianAgreementType.BASIC || pAgreeType == GordianAgreementType.KEM;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a supported kdfType for XDH.
     *
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4XDH(final GordianAgreementType pAgreeType) {
        /* Switch on kdfType */
        switch (theKDF) {
            case SHA512KDF:
            case SHA256KDF:
                return true;
            case SHA512CKDF:
            case SHA256CKDF:
            case SHA512HKDF:
            case SHA256HKDF:
            case NONE:
                return pAgreeType != GordianAgreementType.UNIFIED;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a supported kdfType for EC.
     *
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4EC(final GordianAgreementType pAgreeType) {
        /* Switch on kdfType */
        switch (theKDF) {
            case SHA512KDF:
            case SHA256KDF:
            case NONE:
                return true;
            case SHA512CKDF:
            case SHA256CKDF:
                return pAgreeType != GordianAgreementType.KEM;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a CKDF.
     *
     * @return true/false
     */
    public boolean isCKDF() {
        return theKDF == GordianAgreementKDF.SHA256CKDF || theKDF == GordianAgreementKDF.SHA512CKDF;
    }

    @Override
    public String toString() {
        return theKDF.toString();
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
        return pThat instanceof GordianCoreAgreementKDF myThat
                && theKDF == myThat.getKDF();
    }

    @Override
    public int hashCode() {
        return theKDF.hashCode();
    }

    /**
     * Obtain the core KDF.
     *
     * @param pKDF the base KDF
     * @return the core KDF
     */
    public static GordianCoreAgreementKDF mapCoreKDF(final Object pKDF) {
        return pKDF instanceof GordianAgreementKDF myKDF ? KDFMAP.get(myKDF) : null;
    }

    /**
     * Build the KDF map.
     *
     * @return the KDF map
     */
    private static Map<GordianAgreementKDF, GordianCoreAgreementKDF> newKDFMap() {
        final Map<GordianAgreementKDF, GordianCoreAgreementKDF> myMap = new EnumMap<>(GordianAgreementKDF.class);
        for (GordianAgreementKDF myKDF : GordianAgreementKDF.values()) {
            myMap.put(myKDF, new GordianCoreAgreementKDF(myKDF));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreAgreementKDF[] values() {
        return VALUES;
    }
}
