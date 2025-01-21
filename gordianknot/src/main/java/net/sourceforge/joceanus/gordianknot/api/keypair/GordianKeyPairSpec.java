/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Asymmetric KeyPair Specification.
 */
public class GordianKeyPairSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The keyPairType.
     */
    private final GordianKeyPairType theKeyPairType;

    /**
     * The SubKeyType.
     */
    private final Object theSubKeyType;

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
     * @param pKeyType the keyType
     * @param pSubKeyType the subKeyType
     */
    public GordianKeyPairSpec(final GordianKeyPairType pKeyType,
                              final Object pSubKeyType) {
        theKeyPairType = pKeyType;
        theSubKeyType = pSubKeyType;
        isValid = checkValidity();
    }

    /**
     * Obtain the keyPairType.
     * @return the keyPairType.
     */
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
    }

    /**
     * Obtain the subKeyType.
     * @return the keyType.
     */
    public Object getSubKeyType() {
        return theSubKeyType;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the RSAmodulus.
     * @return the modulus.
     */
    public GordianRSAModulus getRSAModulus() {
        if (!(theSubKeyType instanceof GordianRSAModulus)) {
            throw new IllegalArgumentException();
        }
        return (GordianRSAModulus) theSubKeyType;
    }

    /**
     * Obtain the DSA keyType.
     * @return the keyType.
     */
    public GordianDSAKeyType getDSAKeyType() {
        if (!(theSubKeyType instanceof GordianDSAKeyType)) {
            throw new IllegalArgumentException();
        }
        return (GordianDSAKeyType) theSubKeyType;
    }

    /**
     * Obtain the DH Group.
     * @return the dhGroup.
     */
    public GordianDHGroup getDHGroup() {
        if (!(theSubKeyType instanceof GordianDHGroup)) {
            throw new IllegalArgumentException();
        }
        return (GordianDHGroup) theSubKeyType;
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianElliptic getElliptic() {
        if (!(theSubKeyType instanceof GordianElliptic)) {
            throw new IllegalArgumentException();
        }
        return (GordianElliptic) theSubKeyType;
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianEdwardsElliptic getEdwardsElliptic() {
        if (!(theSubKeyType instanceof GordianEdwardsElliptic)) {
            throw new IllegalArgumentException();
        }
        return (GordianEdwardsElliptic) theSubKeyType;
    }

    /**
     * Obtain the lms keySpec.
     * @return the keySpec.
     */
    public GordianLMSKeySpec getLMSKeySpec() {
        if (!(theSubKeyType instanceof GordianLMSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianLMSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the hss keySpec.
     * @return the keySpec.
     */
    public GordianHSSKeySpec getHSSKeySpec() {
        if (!(theSubKeyType instanceof GordianHSSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianHSSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the XMSS keySpec.
     * @return the keySpec.
     */
    public GordianXMSSKeySpec getXMSSKeySpec() {
        if (!(theSubKeyType instanceof GordianXMSSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianXMSSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the XMSS digestType.
     * @return the digestType.
     */
    public GordianXMSSDigestType getXMSSDigestType() {
        return getXMSSKeySpec().getDigestType();
    }

    /**
     * Obtain the SLHDSA keySpec.
     * @return the keySpec.
     */
    public GordianSLHDSASpec getSLHDSAKeySpec() {
        if (!(theSubKeyType instanceof GordianSLHDSASpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianSLHDSASpec) theSubKeyType;
    }

    /**
     * Obtain the CMCE keySpec.
     * @return the keySpec.
     */
    public GordianCMCESpec getCMCEKeySpec() {
        if (!(theSubKeyType instanceof GordianCMCESpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianCMCESpec) theSubKeyType;
    }

    /**
     * Obtain the FRODO keySpec.
     * @return the keySpec.
     */
    public GordianFRODOSpec getFRODOKeySpec() {
        if (!(theSubKeyType instanceof GordianFRODOSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianFRODOSpec) theSubKeyType;
    }

    /**
     * Obtain the Saber keySpec.
     * @return the keySpec.
     */
    public GordianSABERSpec getSABERKeySpec() {
        if (!(theSubKeyType instanceof GordianSABERSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianSABERSpec) theSubKeyType;
    }

    /**
     * Obtain the MLKEM keySpec.
     * @return the keySpec.
     */
    public GordianMLKEMSpec getMLKEMKeySpec() {
        if (!(theSubKeyType instanceof GordianMLKEMSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianMLKEMSpec) theSubKeyType;
    }

    /**
     * Obtain the MLDSA keySpec.
     * @return the keySpec.
     */
    public GordianMLDSASpec getMLDSAKeySpec() {
        if (!(theSubKeyType instanceof GordianMLDSASpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianMLDSASpec) theSubKeyType;
    }

    /**
     * Obtain the HQC keySpec.
     * @return the keySpec.
     */
    public GordianHQCSpec getHQCKeySpec() {
        if (!(theSubKeyType instanceof GordianHQCSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianHQCSpec) theSubKeyType;
    }

    /**
     * Obtain the Bike keySpec.
     * @return the keySpec.
     */
    public GordianBIKESpec getBIKEKeySpec() {
        if (!(theSubKeyType instanceof GordianBIKESpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianBIKESpec) theSubKeyType;
    }

    /**
     * Obtain the NTRU keySpec.
     * @return the keySpec.
     */
    public GordianNTRUSpec getNTRUKeySpec() {
        if (!(theSubKeyType instanceof GordianNTRUSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianNTRUSpec) theSubKeyType;
    }

    /**
     * Obtain the NTRUPRIME keySpec.
     * @return the keySpec.
     */
    public GordianNTRUPrimeSpec getNTRUPrimeKeySpec() {
        if (!(theSubKeyType instanceof GordianNTRUPrimeSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianNTRUPrimeSpec) theSubKeyType;
    }

    /**
     * Obtain the Falcon keySpec.
     * @return the keySpec.
     */
    public GordianFALCONSpec getFalconKeySpec() {
        if (!(theSubKeyType instanceof GordianFALCONSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianFALCONSpec) theSubKeyType;
    }

    /**
     * Obtain the Picnic keySpec.
     * @return the keySpec.
     */
    public GordianPICNICSpec getPicnicKeySpec() {
        if (!(theSubKeyType instanceof GordianPICNICSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianPICNICSpec) theSubKeyType;
    }

    /**
     * Obtain the Rainbow keySpec.
     * @return the keySpec.
     */
    public GordianRainbowSpec getRainbowKeySpec() {
        if (!(theSubKeyType instanceof GordianRainbowSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianRainbowSpec) theSubKeyType;
    }

    /**
     * Obtain the composite keySpec iterator.
     * @return the keySpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianKeyPairSpec> keySpecIterator() {
        if (!(theSubKeyType instanceof List)) {
            throw new IllegalArgumentException();
        }
        return ((List<GordianKeyPairSpec>) theSubKeyType).iterator();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Derive the name */
                deriveName();
            }  else {
                /* Report invalid spec */
                theName = "InvalidKeyPairSpec: " + theKeyPairType + ":" + theSubKeyType;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Derive name.
     */
    private void deriveName() {
        /* Load the name */
        theName = theKeyPairType.toString();
        if (theSubKeyType != null) {
            switch (theKeyPairType) {
                case XMSS:
                    theName = theSubKeyType.toString();
                    break;
                case EDDSA:
                    theName = "Ed" + ((GordianEdwardsElliptic) theSubKeyType).getSuffix();
                    break;
                case XDH:
                    theName = "X" + ((GordianEdwardsElliptic) theSubKeyType).getSuffix();
                    break;
                case COMPOSITE:
                    final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
                    final StringBuilder myBuilder = new StringBuilder(theName);
                    while (myIterator.hasNext()) {
                        myBuilder.append(SEP).append(myIterator.next().toString());
                    }
                    theName = myBuilder.toString();
                    break;
                default:
                    theName += SEP + theSubKeyType.toString();
                    break;
            }
        }
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

        /* Make sure that the object is a keyPairSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target KeySpec */
        final GordianKeyPairSpec myThat = (GordianKeyPairSpec) pThat;

        /* Check KeyPairType and subKeyType */
        return theKeyPairType == myThat.getKeyPairType()
                && Objects.equals(theSubKeyType, myThat.theSubKeyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairType, theSubKeyType);
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null keyPairType */
        if (theKeyPairType == null) {
            return false;
        }

        /* Switch on keyPairType */
        switch (theKeyPairType) {
            case RSA:
                return theSubKeyType instanceof GordianRSAModulus;
            case DSA:
                return theSubKeyType instanceof GordianDSAKeyType;
            case DH:
            case ELGAMAL:
                return theSubKeyType instanceof GordianDHGroup;
            case EC:
                return theSubKeyType instanceof GordianDSAElliptic;
            case SM2:
                return theSubKeyType instanceof GordianSM2Elliptic;
            case GOST2012:
                return theSubKeyType instanceof GordianGOSTElliptic;
            case DSTU4145:
                return theSubKeyType instanceof GordianDSTU4145Elliptic;
            case XMSS:
                return theSubKeyType instanceof GordianXMSSKeySpec
                        && ((GordianXMSSKeySpec) theSubKeyType).isValid();
            case SLHDSA:
                return theSubKeyType instanceof GordianSLHDSASpec;
            case CMCE:
                return theSubKeyType instanceof GordianCMCESpec;
            case FRODO:
                return theSubKeyType instanceof GordianFRODOSpec;
            case SABER:
                return theSubKeyType instanceof GordianSABERSpec;
            case MLKEM:
                return theSubKeyType instanceof GordianMLKEMSpec;
            case MLDSA:
                return theSubKeyType instanceof GordianMLDSASpec;
            case HQC:
                return theSubKeyType instanceof GordianHQCSpec;
            case BIKE:
                return theSubKeyType instanceof GordianBIKESpec;
            case NTRU:
                return theSubKeyType instanceof GordianNTRUSpec;
            case NTRUPRIME:
                return theSubKeyType instanceof GordianNTRUPrimeSpec;
            case FALCON:
                return theSubKeyType instanceof GordianFALCONSpec;
            case PICNIC:
                return theSubKeyType instanceof GordianPICNICSpec;
            case RAINBOW:
                return theSubKeyType instanceof GordianRainbowSpec;
            case NEWHOPE:
                return theSubKeyType == null;
            case LMS:
                return (theSubKeyType instanceof GordianLMSKeySpec
                         && ((GordianLMSKeySpec) theSubKeyType).isValid())
                        || (theSubKeyType instanceof GordianHSSKeySpec
                            && ((GordianHSSKeySpec) theSubKeyType).isValid());
            case EDDSA:
            case XDH:
                return theSubKeyType instanceof GordianEdwardsElliptic;
            case COMPOSITE:
                return theSubKeyType instanceof List && checkComposite();
            default:
                return false;
        }
    }

    /**
     * Check composite spec validity.
     * @return valid true/false
     */
    private boolean checkComposite() {
        Boolean stateAware = null;
        final List<GordianKeyPairType> myExisting = new ArrayList<>();
        final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
        while (myIterator.hasNext()) {
            /* Check that we have not got a null */
            final GordianKeyPairSpec mySpec = myIterator.next();
            if (mySpec == null) {
                return false;
            }

            /* Check that we have not got a duplicate or COMPOSITE */
            final GordianKeyPairType myType = mySpec.getKeyPairType();
            if (myExisting.contains(myType) || myType == GordianKeyPairType.COMPOSITE) {
                return false;
            }

            /* Check that stateAwareness is identical */
            if (stateAware == null) {
                stateAware = mySpec.isStateAware();
            } else if (mySpec.isStateAware() != stateAware) {
                return false;
            }

            /* Add to list */
            myExisting.add(myType);
        }

        /* Make sure there are at least two */
        return myExisting.size() > 1;
    }

    /**
     * is the use subType for signatures?
     * @return true/false
     */
    public boolean isStateAware() {
        switch (theKeyPairType) {
            case XMSS:
            case LMS:
                return true;
            case COMPOSITE:
                return keySpecIterator().next().isStateAware();
            default:
                return false;
        }
    }
}
