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

package io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SM2 EncryptionSpec.
 */
public class GordianCoreSM2EncryptionSpec
        implements GordianNewSM2EncryptionSpec {
    /**
     * EncryptionType.
     */
    private final GordianNewSM2EncryptionType theType;

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
    GordianCoreSM2EncryptionSpec(final GordianNewSM2EncryptionType pType,
                                 final GordianNewDigestSpec pDigest) {
        /* Store parameters */
        theType = pType;
        theDigest = (GordianCoreDigestSpec) pDigest;
        isValid = checkValidity();
    }

    @Override
    public GordianNewSM2EncryptionType getEncryptionType() {
        return theType;
    }

    @Override
    public GordianNewDigestSpec getDigestSpec() {
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

    /**
     * Obtain a list of all possible encryptionSpecs.
     *
     * @return the list
     */
    public static List<GordianNewSM2EncryptionSpec> listPossibleSpecs() {
        /* Create list */
        final List<GordianNewSM2EncryptionSpec> mySpecs = new ArrayList<>();
        final GordianCoreDigestSpecBuilder myBuilder = new GordianCoreDigestSpecBuilder();

        /* Loop through the encryptionTypes */
        for (GordianNewSM2EncryptionType myType : GordianNewSM2EncryptionType.values()) {
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sm3()));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_224)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_256)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_384)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.sha2(GordianLength.LEN_512)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.blake2s(GordianLength.LEN_256)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.blake2b(GordianLength.LEN_512)));
            mySpecs.add(new GordianCoreSM2EncryptionSpec(myType, myBuilder.whirlpool()));
        }

        /* Return the list */
        return mySpecs;
    }
}
