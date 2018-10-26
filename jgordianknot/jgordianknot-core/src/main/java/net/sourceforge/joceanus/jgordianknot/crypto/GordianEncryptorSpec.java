/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianMcElieceKeySpec.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Asymmetric Encryption Specification.
 */
public class GordianEncryptorSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * AsymKeyType.
     */
    private final GordianAsymKeyType theAsymKeyType;

    /**
     * EncryptorType.
     */
    private final Object theEncryptorType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pEncryptorType the encryptor type
     */
    GordianEncryptorSpec(final GordianAsymKeyType pAsymKeyType,
                         final Object pEncryptorType) {
        theAsymKeyType = pAsymKeyType;
        theEncryptorType = pEncryptorType;
    }

    /**
     * Create RSA Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec rsa(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianAsymKeyType.RSA, pSpec);
    }

    /**
     * Create EC Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec ec() {
        return new GordianEncryptorSpec(GordianAsymKeyType.EC, null);
    }

    /**
     * Create GOST Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec gost2012() {
        return new GordianEncryptorSpec(GordianAsymKeyType.GOST2012, null);
    }

    /**
     * Create DSTU Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec dstu4145() {
        return new GordianEncryptorSpec(GordianAsymKeyType.DSTU4145, null);
    }

    /**
     * Create SM2 Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2() {
        return new GordianEncryptorSpec(GordianAsymKeyType.SM2, null);
    }

    /**
     * Create SM2 Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianAsymKeyType.SM2, pSpec);
    }

    /**
     * Create McEliece Encryptor.
     * @param pType the encryptionType
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec mcEliece(final GordianMcElieceEncryptionType pType) {
        return new GordianEncryptorSpec(GordianAsymKeyType.MCELIECE, pType);
    }

    /**
     * Obtain the keyType.
     * @return the keyType.
     */
    public GordianAsymKeyType getKeyType() {
        return theAsymKeyType;
    }

    /**
     * Obtain the encryptorType.
     * @return the keyType.
     */
    public Object getEncryptorType() {
        return theEncryptorType;
    }

    /**
     * Obtain the digestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theEncryptorType instanceof GordianDigestSpec
               ? (GordianDigestSpec) theEncryptorType
               : null;
    }

    /**
     * Obtain the mcEliece encryption type.
     * @return the encryptionType.
     */
    public GordianMcElieceEncryptionType getMcElieceType() {
        return theEncryptorType instanceof GordianMcElieceEncryptionType
               ? (GordianMcElieceEncryptionType) theEncryptorType
               : null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theAsymKeyType.toString();
            if (theEncryptorType != null) {
                theName += SEP + theEncryptorType.toString();
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

        /* Access the target encryptorSpec */
        final GordianEncryptorSpec myThat = (GordianEncryptorSpec) pThat;

        /* Check KeyType */
        if (theAsymKeyType != myThat.getKeyType()) {
            return false;
        }

        /* Match subfields */
        return theEncryptorType == null
               ? myThat.theEncryptorType == null
               : theEncryptorType.equals(myThat.theEncryptorType);
    }

    @Override
    public int hashCode() {
        int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        if (theEncryptorType != null) {
            hashCode += theEncryptorType.hashCode();
        }
        return hashCode;
    }
}
