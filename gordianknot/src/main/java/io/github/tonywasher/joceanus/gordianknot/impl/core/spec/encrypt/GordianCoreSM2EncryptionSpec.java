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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;

import java.util.Objects;

/**
 * SM2 EncryptionSpec.
 */
public class GordianCoreSM2EncryptionSpec
        implements GordianSM2EncryptionSpec {
    /**
     * EncryptionType.
     */
    private final GordianSM2EncryptionType theType;

    /**
     * DigestSpec.
     */
    private final GordianCoreDigestSpec theDigest;

    /**
     * The Validity.
     */
    private final boolean isValid;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     *
     * @param pType   the encryptionType
     * @param pDigest the digestSpec
     */
    GordianCoreSM2EncryptionSpec(final GordianSM2EncryptionType pType,
                                 final GordianDigestSpec pDigest) {
        /* Store parameters */
        theType = pType;
        theDigest = (GordianCoreDigestSpec) pDigest;
        isValid = checkValidity();
    }

    @Override
    public GordianSM2EncryptionType getEncryptionType() {
        return theType;
    }

    @Override
    public GordianDigestSpec getDigestSpec() {
        return theDigest;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        return theType != null
                && theDigest != null
                && theDigest.isValid()
                && isDigestSupported();
    }

    /**
     * Is the SM2 digestSpec supported?
     *
     * @return true/false
     */
    private boolean isDigestSupported() {
        switch (theDigest.getDigestType()) {
            case SHA2:
                return !theDigest.isSha2Hybrid();
            case WHIRLPOOL:
            case SM3:
                return true;
            case BLAKE2:
                return theDigest.getDigestLength().equals(theDigest.getCoreDigestState().getLength());
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the encryptionSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theType.toString() + GordianCoreEncryptorSpec.SEP + theDigest.toString();
            } else {
                /* Report invalid spec */
                theName = "InvalidEncryptorSpec: " + theType + ":" + theDigest;
            }
        }

        /* return the name */
        return theName;
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

        /* Make sure that the object is an EncryptorSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target encryptionSpec */
        final GordianCoreSM2EncryptionSpec myThat = (GordianCoreSM2EncryptionSpec) pThat;

        /* Match fields */
        return theType == myThat.getEncryptionType()
                && Objects.equals(theDigest, myThat.getDigestSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theDigest);
    }
}
