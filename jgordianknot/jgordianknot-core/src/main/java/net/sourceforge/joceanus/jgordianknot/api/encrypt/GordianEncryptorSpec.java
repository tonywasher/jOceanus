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
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * @param pKeyPairType the keyPairType
     * @param pEncryptorType the encryptor type
     */
    public GordianEncryptorSpec(final GordianKeyPairType pKeyPairType,
                                final Object pEncryptorType) {
        theKeyPairType = pKeyPairType;
        theEncryptorType = pEncryptorType;
        isValid = checkValidity();
    }

    /**
     * Create RSA Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec rsa(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.RSA, pSpec);
    }

    /**
     * Create ElGamal Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec elGamal(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.ELGAMAL, pSpec);
    }

    /**
     * Create EC Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec ec() {
        return new GordianEncryptorSpec(GordianKeyPairType.EC, null);
    }

    /**
     * Create GOST Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec gost2012() {
        return new GordianEncryptorSpec(GordianKeyPairType.GOST2012, null);
    }

    /**
     * Create DSTU Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec dstu4145() {
        return new GordianEncryptorSpec(GordianKeyPairType.DSTU4145, null);
    }

    /**
     * Create SM2 Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2() {
        return new GordianEncryptorSpec(GordianKeyPairType.SM2, null);
    }

    /**
     * Create SM2 Encryptor.
     * @param pSpec the sm2EncryptionSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2(final GordianSM2EncryptionSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.SM2, pSpec);
    }

    /**
     * Create McEliece Encryptor.
     * @param pType the encryptionType
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec mcEliece(final GordianMcElieceEncryptionType pType) {
        return new GordianEncryptorSpec(GordianKeyPairType.MCELIECE, pType);
    }

    /**
     * Obtain the keyPairType.
     * @return the keyPairType.
     */
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
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
     * Obtain the SM2 encryption Spec.
     * @return the encryptionSpec.
     */
    public GordianSM2EncryptionSpec getSM2EncryptionSpec() {
        return theEncryptorType instanceof GordianSM2EncryptionSpec
               ? (GordianSM2EncryptionSpec) theEncryptorType
               : null;
    }

    /**
     * Is the encryptorSpec valid?
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
        if (theKeyPairType == null) {
            return false;
        }
        switch (theKeyPairType) {
            case RSA:
            case ELGAMAL:
                return theEncryptorType instanceof GordianDigestSpec
                        && ((GordianDigestSpec) theEncryptorType).isValid();
            case SM2:
                return theEncryptorType == null
                        || (theEncryptorType instanceof GordianSM2EncryptionSpec
                            && ((GordianSM2EncryptionSpec) theEncryptorType).isValid());
            case EC:
            case GOST2012:
                return theEncryptorType == null;
            case MCELIECE:
                return theEncryptorType instanceof GordianMcElieceEncryptionType;
            default:
                return false;
        }
    }

    /**
     * Is the Spec supported?
     * @return true/false
     */
    public boolean isSupported() {
        final GordianDigestSpec mySpec = getDigestSpec();
        switch (theKeyPairType) {
            case RSA:
            case ELGAMAL:
                return mySpec != null && GordianDigestType.SHA2.equals(mySpec.getDigestType()) && mySpec.getStateLength() == null;
            case EC:
            case GOST2012:
            case MCELIECE:
            case SM2:
                return true;
            default:
                return false;
        }
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
                    case MCELIECE:
                        theName += SEP + theEncryptorType;
                        break;
                    case EC:
                    case GOST2012:
                        theName += SEP + ECELGAMAL;
                        break;
                    case SM2:
                        theName += SEP + (theEncryptorType == null ? ECELGAMAL : theEncryptorType);
                        break;
                    default:
                        break;
                }
            }  else {
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

        /* Make sure that the object is an EncryptorSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target encryptorSpec */
        final GordianEncryptorSpec myThat = (GordianEncryptorSpec) pThat;

        /* Match fields */
        return theKeyPairType == myThat.getKeyPairType()
                && Objects.equals(theEncryptorType, myThat.theEncryptorType);
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        if (theEncryptorType != null) {
            hashCode += theEncryptorType.hashCode();
        }
        return hashCode;
    }

    /**
     * Obtain a list of all possible encryptors for the keyType.
     * @param pKeyPairType the keyPairType
     * @return the list
     */
    public static List<GordianEncryptorSpec> listPossibleEncryptors(final GordianKeyPairType pKeyPairType) {
        /* Create list */
        final List<GordianEncryptorSpec> myEncryptors = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairType) {
            case RSA:
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_224)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_256)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_384)));
                myEncryptors.add(GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_512)));
                break;
            case ELGAMAL:
                myEncryptors.add(GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_224)));
                myEncryptors.add(GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_256)));
                myEncryptors.add(GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_384)));
                myEncryptors.add(GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_512)));
                break;
            case EC:
            case SM2:
            case GOST2012:
                /* Add EC-ElGamal */
                myEncryptors.add(GordianEncryptorSpec.ec());

                /* Loop through the encryptionSpecs */
                for (GordianSM2EncryptionSpec mySpec : GordianSM2EncryptionSpec.listPossibleSpecs()) {
                    myEncryptors.add(GordianEncryptorSpec.sm2(mySpec));
                }
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
