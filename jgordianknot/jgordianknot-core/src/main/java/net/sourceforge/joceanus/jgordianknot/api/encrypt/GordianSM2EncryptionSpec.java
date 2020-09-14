/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;

/**
 * SM2 EncryptionSpec.
 */
public class GordianSM2EncryptionSpec {
    /**
     * EncryptionType.
     */
    private final GordianSM2EncryptionType theType;

    /**
     * DigestSpec.
     */
    private final GordianDigestSpec theDigest;

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
     * @param pType the encryptionType
     * @param pDigest the digestSpec
     */
    GordianSM2EncryptionSpec(final GordianSM2EncryptionType pType,
                             final GordianDigestSpec pDigest) {
        /* Store parameters */
        theType = pType;
        theDigest = pDigest;
        isValid = checkValidity();
    }

    /**
     * Create SM2 C1C2C3 Spec.
     * @param pSpec the digestSpec
     * @return the encryptionSpec
     */
    public static GordianSM2EncryptionSpec c1c2c3(final GordianDigestSpec pSpec) {
        return new GordianSM2EncryptionSpec(GordianSM2EncryptionType.C1C2C3, pSpec);
    }

    /**
     * Create SM2 C1C2C3 Spec.
     * @param pSpec the digestSpec
     * @return the encryptionSpec
     */
    public static GordianSM2EncryptionSpec c1c3c2(final GordianDigestSpec pSpec) {
        return new GordianSM2EncryptionSpec(GordianSM2EncryptionType.C1C3C2, pSpec);
    }

    /**
     * Obtain the encryptionType.
     * @return the encryptionType
     */
    public GordianSM2EncryptionType getEncryptionType() {
        return theType;
    }

    /**
     * Obtain the digestSpec.
     * @return the digestSpec
     */
    public GordianDigestSpec getDigestSpec() {
        return theDigest;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        return theType != null
                && theDigest != null
                && isDigestSupported();
    }

    /**
     * Is the SM2 digestSpec supported?
     * @return true/false
     */
    private boolean isDigestSupported() {
        switch (theDigest.getDigestType()) {
            case SHA2:
                return theDigest.getStateLength() == null;
            case WHIRLPOOL:
            case SM3:
                return true;
            case BLAKE:
                return theDigest.getDigestLength().equals(theDigest.getStateLength());
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
                theName = theType.toString() + GordianEncryptorSpec.SEP + theDigest.toString();
            }  else {
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
        final GordianSM2EncryptionSpec myThat = (GordianSM2EncryptionSpec) pThat;

        /* Match fields */
        return theType == myThat.getEncryptionType()
                && Objects.equals(theDigest, myThat.getDigestSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theDigest);
    }

    /**
     * Obtain a list of all possible encryptionSpecs.
     * @return the list
     */
    public static List<GordianSM2EncryptionSpec> listPossibleSpecs() {
        /* Create list */
        final List<GordianSM2EncryptionSpec> mySpecs = new ArrayList<>();

        /* Loop through the encryptionTypes */
        for (GordianSM2EncryptionType myType : GordianSM2EncryptionType.values()) {
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.sm3()));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.sha2(GordianLength.LEN_224)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.sha2(GordianLength.LEN_256)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.sha2(GordianLength.LEN_384)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.sha2(GordianLength.LEN_512)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.blakeAlt(GordianLength.LEN_256)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.blake(GordianLength.LEN_512)));
            mySpecs.add(new GordianSM2EncryptionSpec(myType, GordianDigestSpec.whirlpool()));
        }

        /* Return the list */
        return mySpecs;
    }

    /**
     * SM2 EncryptionType.
     */
    public enum GordianSM2EncryptionType {
        /**
         * C1C2C3.
         */
        C1C2C3,

        /**
         * C1C3C2.
         */
        C1C3C2;
    }
}
