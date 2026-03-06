/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.api.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Asymmetric Encryption Specification.
 */
public final class GordianEncryptorSpec {
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
    private final GordianNewKeyPairType theKeyPairType;

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
    public GordianEncryptorSpec(final GordianNewKeyPairType pKeyPairType,
                                final Object pEncryptorType) {
        theKeyPairType = pKeyPairType;
        theEncryptorType = pEncryptorType;
        isValid = checkValidity();
    }

    /**
     * Obtain the keyPairType.
     *
     * @return the keyPairType.
     */
    public GordianNewKeyPairType getKeyPairType() {
        return theKeyPairType;
    }

    /**
     * Obtain the encryptorType.
     *
     * @return the encryptorType.
     */
    public Object getEncryptorType() {
        return theEncryptorType;
    }

    /**
     * Obtain the digestSpec.
     *
     * @return the digestSpec.
     */
    public GordianNewDigestSpec getDigestSpec() {
        if (theEncryptorType instanceof GordianNewDigestSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the SM2 encryption Spec.
     *
     * @return the encryptionSpec.
     */
    public GordianSM2EncryptionSpec getSM2EncryptionSpec() {
        if (theEncryptorType instanceof GordianSM2EncryptionSpec mySpec) {
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
    public Iterator<GordianEncryptorSpec> encryptorSpecIterator() {
        if (theEncryptorType instanceof List) {
            return ((List<GordianEncryptorSpec>) theEncryptorType).iterator();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Is the encryptorSpec valid?
     *
     * @return true/false.
     */
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
                        || (theEncryptorType instanceof GordianSM2EncryptionSpec s
                        && s.isValid());
            case EC:
            case GOST:
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
                final GordianCoreDigestSpec mySpec = (GordianCoreDigestSpec) getDigestSpec();
                return GordianNewDigestType.SHA2.equals(mySpec.getDigestType()) && !mySpec.isSha2Hybrid();
            case EC:
            case GOST:
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
        final Iterator<GordianEncryptorSpec> myIterator = encryptorSpecIterator();
        while (myIterator.hasNext()) {
            /* Check that each spec is valid */
            final GordianEncryptorSpec mySpec = myIterator.next();
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
                    case GOST:
                        theName += SEP + ECELGAMAL;
                        break;
                    case SM2:
                        theName += SEP + (theEncryptorType == null ? ECELGAMAL : theEncryptorType);
                        break;
                    case COMPOSITE:
                        final Iterator<GordianEncryptorSpec> myIterator = encryptorSpecIterator();
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
        return pThat instanceof GordianEncryptorSpec myThat
                && theKeyPairType == myThat.getKeyPairType()
                && Objects.equals(theEncryptorType, myThat.theEncryptorType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairType, theEncryptorType);
    }
}
