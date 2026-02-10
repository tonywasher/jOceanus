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

package io.github.tonywasher.joceanus.gordianknot.impl.core.sign.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec.GordianCoreKeyPairType;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Signature Specification.
 */
public class GordianCoreSignatureSpec
        implements GordianNewSignatureSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairType.
     */
    private final GordianCoreKeyPairType theKeyPairType;

    /**
     * SignatureType.
     */
    private final GordianCoreSignatureType theSignatureType;

    /**
     * SignatureSpec.
     */
    private final Object theSignatureSpec;

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
     * @param pSignatureType the signatureType
     * @param pSignatureSpec the signatureSpec
     */
    public GordianCoreSignatureSpec(final GordianNewKeyPairType pKeyPairType,
                                    final GordianNewSignatureType pSignatureType,
                                    final Object pSignatureSpec) {
        /* Store parameters */
        theKeyPairType = GordianCoreKeyPairType.mapCoreType(pKeyPairType);
        theSignatureType = GordianCoreSignatureType.mapCoreType(pSignatureType);
        theSignatureSpec = pSignatureSpec;
        isValid = checkValidity();
    }

    @Override
    public GordianNewKeyPairType getKeyPairType() {
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
    public GordianNewSignatureType getSignatureType() {
        return theSignatureType.getType();
    }

    /**
     * Obtain the core signatureType.
     *
     * @return the type
     */
    public GordianCoreSignatureType getCoreType() {
        return theSignatureType;
    }

    @Override
    public Object getSignatureSpec() {
        return theSignatureSpec;
    }

    /**
     * Obtain the DigestSpec.
     *
     * @return the digestSpec.
     */
    public GordianCoreDigestSpec getDigestSpec() {
        if (theSignatureSpec instanceof GordianCoreDigestSpec mySpec) {
            return mySpec;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Obtain the composite signatureSpec iterator.
     *
     * @return the signatureSpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianNewSignatureSpec> signatureSpecIterator() {
        if (theSignatureSpec instanceof List) {
            return ((List<GordianNewSignatureSpec>) theSignatureSpec).iterator();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Does this signatureSpec support context?
     *
     * @return true/false
     */
    public boolean supportsContext() {
        switch (theKeyPairType.getType()) {
            case MLDSA:
            case SLHDSA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theKeyPairType == null || theSignatureType == null) {
            return false;
        }
        switch (theKeyPairType.getType()) {
            case RSA:
            case DSA:
            case EC:
            case DSTU4145:
            case GOST2012:
                return theSignatureSpec instanceof GordianCoreDigestSpec mySpec
                        && mySpec.isValid()
                        && mySpec.getCoreDigestType().supportsLargeData();
            case EDDSA:
            case SLHDSA:
            case MLDSA:
            case FALCON:
            case MAYO:
            case SNOVA:
            case XMSS:
            case LMS:
                return theSignatureSpec == null;
            case PICNIC:
                return theSignatureSpec == null || checkPICNICDigest();
            case SM2:
                return checkSM2Digest();
            case COMPOSITE:
                return theSignatureSpec instanceof List && checkComposite();
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
        final Iterator<GordianNewSignatureSpec> myIterator = signatureSpecIterator();
        while (myIterator.hasNext()) {
            /* Check that each spec is valid */
            final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) myIterator.next();
            if (mySpec == null || !mySpec.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check picnic spec validity.
     *
     * @return valid true/false
     */
    private boolean checkPICNICDigest() {
        /* Check that signature length is 512 */
        if (!(theSignatureSpec instanceof GordianCoreDigestSpec mySpec)
                || (!GordianLength.LEN_512.equals(mySpec.getDigestLength()))) {
            return false;
        }

        /* Switch on DigestType */
        switch (mySpec.getDigestType()) {
            case SHA2:
            case SHA3:
            case SHAKE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check sm2 spec validity.
     *
     * @return valid true/false
     */
    private boolean checkSM2Digest() {
        /* Switch on DigestType */
        if (!(theSignatureSpec instanceof GordianCoreDigestSpec mySpec)) {
            return false;
        }
        switch (mySpec.getDigestType()) {
            case SM3:
                return true;
            case SHA2:
                return GordianLength.LEN_256.equals(mySpec.getDigestLength())
                        && !mySpec.isSha2Hybrid();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theKeyPairType.toString();
            if (theSignatureType.getType() != GordianNewSignatureType.NATIVE) {
                theName += SEP + theSignatureType;
            }
            if (theSignatureSpec != null) {
                if (theKeyPairType.getType() == GordianNewKeyPairType.COMPOSITE) {
                    final Iterator<GordianNewSignatureSpec> myIterator = signatureSpecIterator();
                    final StringBuilder myBuilder = new StringBuilder(theName);
                    while (myIterator.hasNext()) {
                        myBuilder.append(SEP).append(myIterator.next().toString());
                    }
                    theName = myBuilder.toString();
                } else {
                    theName += SEP + theSignatureSpec.toString();
                }
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

        /* Check KeyPairType, signatureType and signatureSpec */
        return pThat instanceof GordianCoreSignatureSpec myThat
                && Objects.equals(theKeyPairType, myThat.getCoreKeyPairType())
                && Objects.equals(theSignatureType, myThat.getCoreType())
                && Objects.equals(theSignatureSpec, myThat.getSignatureSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairType, theSignatureType, theSignatureSpec);
    }
}
