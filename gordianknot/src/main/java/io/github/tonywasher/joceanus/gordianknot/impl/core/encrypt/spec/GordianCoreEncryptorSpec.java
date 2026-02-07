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

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpec;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Asymmetric Encryption Specification.
 */
public class GordianCoreEncryptorSpec
        implements GordianNewEncryptorSpec {
    /**
     * The EC-ElGamal name.
     */
    private static final String ECELGAMAL = "ElGamal";

    /**
     * The Separator.
     */
    static final String SEP = "-";

    /**
     * KeyPairType.
     */
    private final GordianKeyPairType theKeyPairType;

    /**
     * EncryptorType.
     */
    private final Object theEncryptorType;

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
     * @param pKeyPairType   the keyPairType
     * @param pEncryptorType the encryptor type
     */
    public GordianCoreEncryptorSpec(final GordianKeyPairType pKeyPairType,
                                    final Object pEncryptorType) {
        theKeyPairType = pKeyPairType;
        theEncryptorType = pEncryptorType;
        isValid = checkValidity();
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
    }

    @Override
    public Object getEncryptorType() {
        return theEncryptorType;
    }

    /**
     * Obtain the digestSpec.
     *
     * @return the digestSpec.
     */
    public GordianCoreDigestSpec getDigestSpec() {
        if (theEncryptorType instanceof GordianCoreDigestSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the SM2 encryption Spec.
     *
     * @return the encryptionSpec.
     */
    public GordianNewSM2EncryptionSpec getSM2EncryptionSpec() {
        if (theEncryptorType instanceof GordianNewSM2EncryptionSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the composite encryptorSpec iterator.
     *
     * @return the encryptorSpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianNewEncryptorSpec> encryptorSpecIterator() {
        if (theEncryptorType instanceof List) {
            return ((List<GordianNewEncryptorSpec>) theEncryptorType).iterator();
        }
        throw new IllegalArgumentException();
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
        if (theKeyPairType == null) {
            return false;
        }
        switch (theKeyPairType) {
            case RSA:
            case ELGAMAL:
                return theEncryptorType instanceof GordianNewDigestSpec s
                        && s.isValid();
            case SM2:
                return theEncryptorType == null
                        || (theEncryptorType instanceof GordianNewSM2EncryptionSpec s
                        && s.isValid());
            case EC:
            case GOST2012:
                return theEncryptorType == null;
            case COMPOSITE:
                return theEncryptorType instanceof List && checkComposite();
            default:
                return false;
        }
    }

    /**
     * Is the Spec supported?
     *
     * @return true/false
     */
    public boolean isSupported() {
        switch (theKeyPairType) {
            case RSA:
            case ELGAMAL:
                final GordianCoreDigestSpec mySpec = getDigestSpec();
                return GordianNewDigestType.SHA2.equals(mySpec.getDigestType()) && !mySpec.isSha2Hybrid();
            case EC:
            case GOST2012:
            case SM2:
            case COMPOSITE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check composite spec validity.
     *
     * @return valid true/false
     */
    private boolean checkComposite() {
        final Iterator<GordianNewEncryptorSpec> myIterator = encryptorSpecIterator();
        while (myIterator.hasNext()) {
            /* Check that each spec is valid */
            final GordianNewEncryptorSpec mySpec = myIterator.next();
            if (mySpec == null || !mySpec.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the encryptorSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyPairType.toString();
                switch (theKeyPairType) {
                    case RSA:
                    case ELGAMAL:
                        theName += SEP + theEncryptorType;
                        break;
                    case EC:
                    case GOST2012:
                        theName += SEP + ECELGAMAL;
                        break;
                    case SM2:
                        theName += SEP + (theEncryptorType == null ? ECELGAMAL : theEncryptorType);
                        break;
                    case COMPOSITE:
                        final Iterator<GordianNewEncryptorSpec> myIterator = encryptorSpecIterator();
                        final StringBuilder myBuilder = new StringBuilder(theName);
                        while (myIterator.hasNext()) {
                            myBuilder.append(SEP).append(myIterator.next().toString());
                        }
                        theName = myBuilder.toString();
                        break;
                    default:
                        break;
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidEncryptorSpec: " + theKeyPairType + ":" + theEncryptorType;
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

        /* Match fields */
        return pThat instanceof GordianCoreEncryptorSpec myThat
                && theKeyPairType == myThat.getKeyPairType()
                && Objects.equals(theEncryptorType, myThat.theEncryptorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairType, theEncryptorType);
    }
}
