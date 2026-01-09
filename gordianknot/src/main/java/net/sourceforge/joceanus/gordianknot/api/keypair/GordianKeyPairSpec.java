/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
        if (theSubKeyType instanceof GordianRSAModulus myMod) {
            return myMod;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the DSA keyType.
     * @return the keyType.
     */
    public GordianDSAKeyType getDSAKeyType() {
        if (theSubKeyType instanceof GordianDSAKeyType myType) {
            return myType;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the DH Group.
     * @return the dhGroup.
     */
    public GordianDHGroup getDHGroup() {
        if (theSubKeyType instanceof GordianDHGroup myGroup) {
            return myGroup;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianElliptic getElliptic() {
        if (theSubKeyType instanceof GordianElliptic myElliptic) {
            return myElliptic;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianEdwardsElliptic getEdwardsElliptic() {
        if (theSubKeyType instanceof GordianEdwardsElliptic myElliptic) {
            return myElliptic;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the lms keySpec.
     * @return the keySpec.
     */
    public GordianLMSKeySpec getLMSKeySpec() {
        if (theSubKeyType instanceof GordianLMSKeySpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the hss keySpec.
     * @return the keySpec.
     */
    public GordianHSSKeySpec getHSSKeySpec() {
        if (theSubKeyType instanceof GordianHSSKeySpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the XMSS keySpec.
     * @return the keySpec.
     */
    public GordianXMSSKeySpec getXMSSKeySpec() {
        if (theSubKeyType instanceof GordianXMSSKeySpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
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
        if (theSubKeyType instanceof GordianSLHDSASpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the CMCE keySpec.
     * @return the keySpec.
     */
    public GordianCMCESpec getCMCEKeySpec() {
        if (theSubKeyType instanceof GordianCMCESpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the FRODO keySpec.
     * @return the keySpec.
     */
    public GordianFRODOSpec getFRODOKeySpec() {
        if (theSubKeyType instanceof GordianFRODOSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Saber keySpec.
     * @return the keySpec.
     */
    public GordianSABERSpec getSABERKeySpec() {
        if (theSubKeyType instanceof GordianSABERSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the MLKEM keySpec.
     * @return the keySpec.
     */
    public GordianMLKEMSpec getMLKEMKeySpec() {
        if (theSubKeyType instanceof GordianMLKEMSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the MLDSA keySpec.
     * @return the keySpec.
     */
    public GordianMLDSASpec getMLDSAKeySpec() {
        if (theSubKeyType instanceof GordianMLDSASpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the HQC keySpec.
     * @return the keySpec.
     */
    public GordianHQCSpec getHQCKeySpec() {
        if (theSubKeyType instanceof GordianHQCSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Bike keySpec.
     * @return the keySpec.
     */
    public GordianBIKESpec getBIKEKeySpec() {
        if (theSubKeyType instanceof GordianBIKESpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the NTRU keySpec.
     * @return the keySpec.
     */
    public GordianNTRUSpec getNTRUKeySpec() {
        if (theSubKeyType instanceof GordianNTRUSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the NTRUPRIME keySpec.
     * @return the keySpec.
     */
    public GordianNTRUPrimeSpec getNTRUPrimeKeySpec() {
        if (theSubKeyType instanceof GordianNTRUPrimeSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Falcon keySpec.
     * @return the keySpec.
     */
    public GordianFalconSpec getFalconKeySpec() {
        if (theSubKeyType instanceof GordianFalconSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Mayo keySpec.
     * @return the keySpec.
     */
    public GordianMayoSpec getMayoKeySpec() {
        if (theSubKeyType instanceof GordianMayoSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Snova keySpec.
     * @return the keySpec.
     */
    public GordianSnovaSpec getSnovaKeySpec() {
        if (theSubKeyType instanceof GordianSnovaSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the Picnic keySpec.
     * @return the keySpec.
     */
    public GordianPicnicSpec getPicnicKeySpec() {
        if (theSubKeyType instanceof GordianPicnicSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the composite keySpec iterator.
     * @return the keySpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianKeyPairSpec> keySpecIterator() {
        if (theSubKeyType instanceof List) {
            return ((List<GordianKeyPairSpec>) theSubKeyType).iterator();
        }
        throw new IllegalArgumentException();
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
                    theName += SEP + theSubKeyType;
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

        /* Check KeyPairType and subKeyType */
        return pThat instanceof GordianKeyPairSpec myThat
                && theKeyPairType == myThat.getKeyPairType()
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
                return theSubKeyType instanceof GordianXMSSKeySpec s && s.isValid();
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
                return theSubKeyType instanceof GordianFalconSpec;
            case MAYO:
                return theSubKeyType instanceof GordianMayoSpec;
            case SNOVA:
                return theSubKeyType instanceof GordianSnovaSpec;
            case PICNIC:
                return theSubKeyType instanceof GordianPicnicSpec;
            case NEWHOPE:
                return theSubKeyType == null;
            case LMS:
                return (theSubKeyType instanceof GordianLMSKeySpec ls && ls.isValid())
                        || (theSubKeyType instanceof GordianHSSKeySpec hs && hs.isValid());
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
