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
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Asymmetric Encryption Specification.
 */
public final class GordianEncryptorSpec {
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

    /**
     * Is the Spec supported?
     * @return true/false
     */
    public boolean isSupported() {
        switch (theAsymKeyType) {
            case RSA:
                final GordianDigestSpec mySpec = getDigestSpec();
                return mySpec != null && GordianDigestType.SHA2.equals(mySpec.getDigestType()) && mySpec.getStateLength() == null;
            case EC:
            case SM2:
            case MCELIECE:
                return true;
            default:
                return false;
        }
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

    /**
     * Obtain a list of all possible agreements for the keyPair.
     * @param pKeyPair the keyPair
     * @return the list
     */
    public static List<GordianEncryptorSpec> listPossibleEncryptors(final GordianKeyPair pKeyPair) {
        /* Create list */
        final List<GordianEncryptorSpec> myEncryptors = new ArrayList<>();

        /* Switch on AsymKeyType */
        final GordianAsymKeyType myType = pKeyPair.getKeySpec().getKeyType();
        switch (myType) {
            case RSA:
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_224)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_256)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_384)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_512)));
                break;
            case SM2:
                myEncryptors.add(GordianEncryptorSpec.sm2());
                break;
            case EC:
                myEncryptors.add(GordianEncryptorSpec.ec());
                break;
            case GOST2012:
                myEncryptors.add(GordianEncryptorSpec.gost2012());
                break;
            case DSTU4145:
                myEncryptors.add(GordianEncryptorSpec.dstu4145());
                break;
            case MCELIECE:
                myEncryptors.add(GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.STANDARD));
                myEncryptors.add(GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.FUJISAKI));
                myEncryptors.add(GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.KOBARAIMAI));
                myEncryptors.add(GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.POINTCHEVAL));
                break;
            default:
                break;
        }

        /* Return the list */
        return myEncryptors;
    }
}
