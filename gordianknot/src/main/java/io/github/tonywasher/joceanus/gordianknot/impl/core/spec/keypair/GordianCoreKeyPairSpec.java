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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianSpecConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Asymmetric KeyPair Specification.
 */
public class GordianCoreKeyPairSpec
        implements GordianKeyPairSpec {
    /**
     * The keyPairType.
     */
    private final GordianCoreKeyPairType theKeyPairType;

    /**
     * The subSpec.
     */
    private final Object theSubSpec;

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
     * @param pKeyType the keyType
     * @param pSubSpec the subSpec
     */
    GordianCoreKeyPairSpec(final GordianKeyPairType pKeyType,
                           final Object pSubSpec) {
        theKeyPairType = GordianCoreKeyPairType.mapCoreType(pKeyType);
        theSubSpec = wrapSubSpec(pSubSpec);
        isValid = checkValidity();
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType.getType();
    }

    /**
     * Obtain core keyPairType.
     *
     * @return the core type
     */
    public GordianCoreKeyPairType getCoreKeyPairType() {
        return theKeyPairType;
    }

    @Override
    public Object getSubSpec() {
        return unwrapSubSpec();
    }

    /**
     * Obtain the core subSpec.
     *
     * @return the core subSpec
     */
    public Object getCoreSubSpec() {
        return theSubSpec;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the subSpec as a particular class.
     *
     * @param <T>    the required type
     * @param pClazz the required class
     * @return the properly cast value.
     */
    private <T> T castValue(final Class<T> pClazz) {
        if (pClazz.isInstance(theSubSpec)) {
            return pClazz.cast(theSubSpec);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the RSAspec.
     *
     * @return the spec.
     */
    public GordianCoreRSASpec getRSASpec() {
        return castValue(GordianCoreRSASpec.class);
    }

    /**
     * Obtain the DSAspec.
     *
     * @return the spec.
     */
    public GordianCoreDSASpec getDSASpec() {
        return castValue(GordianCoreDSASpec.class);
    }

    /**
     * Obtain the DH Group.
     *
     * @return the dhGroup.
     */
    public GordianCoreDHSpec getDHSpec() {
        return castValue(GordianCoreDHSpec.class);
    }

    /**
     * Obtain the elliptic curve.
     *
     * @return the curve.
     */
    public GordianCoreElliptic getElliptic() {
        return castValue(GordianCoreElliptic.class);
    }

    /**
     * Obtain the ECSpec.
     *
     * @return the keySpec.
     */
    public GordianCoreECSpec getECSpec() {
        return castValue(GordianCoreECSpec.class);
    }

    /**
     * Obtain the SM2Spec.
     *
     * @return the keySpec.
     */
    public GordianCoreSM2Spec getSM2Spec() {
        return castValue(GordianCoreSM2Spec.class);
    }

    /**
     * Obtain the GOSTSpec.
     *
     * @return the keySpec.
     */
    public GordianCoreGOSTSpec getGOSTSpec() {
        return castValue(GordianCoreGOSTSpec.class);
    }

    /**
     * Obtain the DSTUSpec.
     *
     * @return the keySpec.
     */
    public GordianCoreDSTUSpec getDSTUSpec() {
        return castValue(GordianCoreDSTUSpec.class);
    }

    /**
     * Obtain the edwardsSpec.
     *
     * @return the keySpec.
     */
    public GordianCoreEdwardsSpec getEdwardsSpec() {
        return castValue(GordianCoreEdwardsSpec.class);
    }

    /**
     * Obtain the lms keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreLMSSpec getLMSSpec() {
        return castValue(GordianCoreLMSSpec.class);
    }

    /**
     * Obtain the XMSS keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreXMSSSpec getXMSSSpec() {
        return castValue(GordianCoreXMSSSpec.class);
    }

    /**
     * Obtain the SLHDSA keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreSLHDSASpec getSLHDSASpec() {
        return castValue(GordianCoreSLHDSASpec.class);
    }

    /**
     * Obtain the CMCE keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreCMCESpec getCMCESpec() {
        return castValue(GordianCoreCMCESpec.class);
    }

    /**
     * Obtain the FRODO keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreFRODOSpec getFRODOSpec() {
        return castValue(GordianCoreFRODOSpec.class);
    }

    /**
     * Obtain the Saber keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreSABERSpec getSABERSpec() {
        return castValue(GordianCoreSABERSpec.class);
    }

    /**
     * Obtain the MLKEM keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreMLKEMSpec getMLKEMSpec() {
        return castValue(GordianCoreMLKEMSpec.class);
    }

    /**
     * Obtain the MLDSA keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreMLDSASpec getMLDSASpec() {
        return castValue(GordianCoreMLDSASpec.class);
    }

    /**
     * Obtain the HQC keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreHQCSpec getHQCSpec() {
        return castValue(GordianCoreHQCSpec.class);
    }

    /**
     * Obtain the Bike keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreBIKESpec getBIKESpec() {
        return castValue(GordianCoreBIKESpec.class);
    }

    /**
     * Obtain the NTRU keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreNTRUSpec getNTRUSpec() {
        return castValue(GordianCoreNTRUSpec.class);
    }

    /**
     * Obtain the NTRUPlus keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreNTRUPlusSpec getNTRUPlusSpec() {
        return castValue(GordianCoreNTRUPlusSpec.class);
    }

    /**
     * Obtain the NTRUPRIME keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreNTRUPrimeSpec getNTRUPrimeSpec() {
        return castValue(GordianCoreNTRUPrimeSpec.class);
    }

    /**
     * Obtain the Falcon keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreFalconSpec getFalconSpec() {
        return castValue(GordianCoreFalconSpec.class);
    }

    /**
     * Obtain the Mayo keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreMayoSpec getMayoSpec() {
        return castValue(GordianCoreMayoSpec.class);
    }

    /**
     * Obtain the Snova keySpec.
     *
     * @return the keySpec.
     */
    public GordianCoreSnovaSpec getSnovaSpec() {
        return castValue(GordianCoreSnovaSpec.class);
    }

    /**
     * Obtain the Picnic keySpec.
     *
     * @return the keySpec.
     */
    public GordianCorePicnicSpec getPicnicSpec() {
        return castValue(GordianCorePicnicSpec.class);
    }

    /**
     * Obtain the composite keySpec iterator.
     *
     * @return the keySpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianKeyPairSpec> keySpecIterator() {
        if (theSubSpec instanceof List) {
            return ((List<GordianKeyPairSpec>) theSubSpec).iterator();
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
            } else {
                /* Report invalid spec */
                theName = "InvalidKeyPairSpec: " + theKeyPairType + ":" + theSubSpec;
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
        if (theSubSpec != null) {
            switch (theKeyPairType.getType()) {
                case XMSS:
                    theName = theSubSpec.toString();
                    break;
                case EDDSA:
                    theName = "Ed" + ((GordianCoreEdwardsSpec) theSubSpec).getSuffix();
                    break;
                case XDH:
                    theName = "X" + ((GordianCoreEdwardsSpec) theSubSpec).getSuffix();
                    break;
                case COMPOSITE:
                    final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
                    final StringBuilder myBuilder = new StringBuilder(theName);
                    while (myIterator.hasNext()) {
                        myBuilder.append(GordianSpecConstants.SEP).append(myIterator.next().toString());
                    }
                    theName = myBuilder.toString();
                    break;
                default:
                    theName += GordianSpecConstants.SEP + theSubSpec;
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
        return pThat instanceof GordianCoreKeyPairSpec myThat
                && Objects.equals(theKeyPairType, myThat.getCoreKeyPairType())
                && Objects.equals(theSubSpec, myThat.getCoreSubSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairType, theSubSpec);
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null keyPairType */
        if (theKeyPairType == null) {
            return false;
        }

        /* Switch on keyPairType */
        switch (theKeyPairType.getType()) {
            case RSA:
                return theSubSpec instanceof GordianCoreRSASpec;
            case DSA:
                return theSubSpec instanceof GordianCoreDSASpec;
            case DH:
            case ELGAMAL:
                return theSubSpec instanceof GordianCoreDHSpec;
            case EC:
                return theSubSpec instanceof GordianCoreECSpec;
            case SM2:
                return theSubSpec instanceof GordianCoreSM2Spec;
            case GOST:
                return theSubSpec instanceof GordianCoreGOSTSpec;
            case DSTU:
                return theSubSpec instanceof GordianCoreDSTUSpec;
            case XMSS:
                return theSubSpec instanceof GordianCoreXMSSSpec s && s.isValid();
            case SLHDSA:
                return theSubSpec instanceof GordianCoreSLHDSASpec;
            case CMCE:
                return theSubSpec instanceof GordianCoreCMCESpec;
            case FRODO:
                return theSubSpec instanceof GordianCoreFRODOSpec;
            case SABER:
                return theSubSpec instanceof GordianCoreSABERSpec;
            case MLKEM:
                return theSubSpec instanceof GordianCoreMLKEMSpec;
            case MLDSA:
                return theSubSpec instanceof GordianCoreMLDSASpec;
            case HQC:
                return theSubSpec instanceof GordianCoreHQCSpec;
            case BIKE:
                return theSubSpec instanceof GordianCoreBIKESpec;
            case NTRU:
                return theSubSpec instanceof GordianCoreNTRUSpec;
            case NTRUPLUS:
                return theSubSpec instanceof GordianCoreNTRUPlusSpec;
            case NTRUPRIME:
                return theSubSpec instanceof GordianCoreNTRUPrimeSpec s && s.isValid();
            case FALCON:
                return theSubSpec instanceof GordianCoreFalconSpec;
            case MAYO:
                return theSubSpec instanceof GordianCoreMayoSpec;
            case SNOVA:
                return theSubSpec instanceof GordianCoreSnovaSpec;
            case PICNIC:
                return theSubSpec instanceof GordianCorePicnicSpec;
            case NEWHOPE:
                return theSubSpec == null;
            case LMS:
                return theSubSpec instanceof GordianCoreLMSSpec ls && ls.isValid();
            case EDDSA:
            case XDH:
                return theSubSpec instanceof GordianCoreEdwardsSpec;
            case COMPOSITE:
                return theSubSpec instanceof List && checkComposite();
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
        Boolean stateAware = null;
        final List<GordianKeyPairType> myExisting = new ArrayList<>();
        final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
        while (myIterator.hasNext()) {
            /* Check that we have not got a null */
            final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) myIterator.next();
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
     *
     * @return true/false
     */
    public boolean isStateAware() {
        switch (theKeyPairType.getType()) {
            case XMSS:
            case LMS:
                return true;
            case COMPOSITE:
                return ((GordianCoreKeyPairSpec) keySpecIterator().next()).isStateAware();
            default:
                return false;
        }
    }

    /**
     * Wrap the subSpec.
     *
     * @param pSubSpec the raw subSpec
     * @return the wrapped subSpec
     */
    private Object wrapSubSpec(final Object pSubSpec) {
        /* Handle null keyPairType */
        if (theKeyPairType == null) {
            return pSubSpec;
        }

        /* Switch on keyPairType */
        switch (theKeyPairType.getType()) {
            case RSA:
                return GordianCoreRSASpec.mapCoreSpec(pSubSpec);
            case DSA:
                return GordianCoreDSASpec.mapCoreSpec(pSubSpec);
            case DH:
            case ELGAMAL:
                return GordianCoreDHSpec.mapCoreSpec(pSubSpec);
            case EC:
                return GordianCoreECSpec.mapCoreSpec(pSubSpec);
            case SM2:
                return GordianCoreSM2Spec.mapCoreSpec(pSubSpec);
            case GOST:
                return GordianCoreGOSTSpec.mapCoreSpec(pSubSpec);
            case DSTU:
                return GordianCoreDSTUSpec.mapCoreSpec(pSubSpec);
            case SLHDSA:
                return GordianCoreSLHDSASpec.mapCoreSpec(pSubSpec);
            case CMCE:
                return GordianCoreCMCESpec.mapCoreSpec(pSubSpec);
            case FRODO:
                return GordianCoreFRODOSpec.mapCoreSpec(pSubSpec);
            case SABER:
                return GordianCoreSABERSpec.mapCoreSpec(pSubSpec);
            case MLKEM:
                return GordianCoreMLKEMSpec.mapCoreSpec(pSubSpec);
            case MLDSA:
                return GordianCoreMLDSASpec.mapCoreSpec(pSubSpec);
            case HQC:
                return GordianCoreHQCSpec.mapCoreSpec(pSubSpec);
            case BIKE:
                return GordianCoreBIKESpec.mapCoreSpec(pSubSpec);
            case NTRU:
                return GordianCoreNTRUSpec.mapCoreSpec(pSubSpec);
            case NTRUPLUS:
                return GordianCoreNTRUPlusSpec.mapCoreSpec(pSubSpec);
            case FALCON:
                return GordianCoreFalconSpec.mapCoreSpec(pSubSpec);
            case MAYO:
                return GordianCoreMayoSpec.mapCoreSpec(pSubSpec);
            case SNOVA:
                return GordianCoreSnovaSpec.mapCoreSpec(pSubSpec);
            case PICNIC:
                return GordianCorePicnicSpec.mapCoreSpec(pSubSpec);
            case EDDSA:
            case XDH:
                return GordianCoreEdwardsSpec.mapCoreSpec(pSubSpec);
            case NEWHOPE:
            case NTRUPRIME:
            case LMS:
            case XMSS:
            case COMPOSITE:
            default:
                return pSubSpec;
        }
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private Object unwrapSubSpec() {
        /* Switch on keyPairType */
        switch (theKeyPairType.getType()) {
            case RSA:
                return getRSASpec().getSpec();
            case DSA:
                return getDSASpec().getSpec();
            case DH:
            case ELGAMAL:
                return getDHSpec().getSpec();
            case EC:
                return getECSpec().getSpec();
            case SM2:
                return getSM2Spec().getSpec();
            case GOST:
                return getGOSTSpec().getSpec();
            case DSTU:
                return getDSTUSpec().getSpec();
            case SLHDSA:
                return getSLHDSASpec().getSpec();
            case CMCE:
                return getCMCESpec().getSpec();
            case FRODO:
                return getFRODOSpec().getSpec();
            case SABER:
                return getSABERSpec().getSpec();
            case MLKEM:
                return getMLKEMSpec().getSpec();
            case MLDSA:
                return getMLDSASpec().getSpec();
            case HQC:
                return getHQCSpec().getSpec();
            case BIKE:
                return getBIKESpec().getSpec();
            case NTRU:
                return getNTRUSpec().getSpec();
            case NTRUPLUS:
                return getNTRUPlusSpec().getSpec();
            case FALCON:
                return getFalconSpec().getSpec();
            case MAYO:
                return getMayoSpec().getSpec();
            case SNOVA:
                return getSnovaSpec().getSpec();
            case PICNIC:
                return getPicnicSpec().getSpec();
            case EDDSA:
            case XDH:
                return getEdwardsSpec().getSpec();
            case NEWHOPE:
            case NTRUPRIME:
            case LMS:
            case XMSS:
            case COMPOSITE:
            default:
                return theSubSpec;
        }
    }
}
