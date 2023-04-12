/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.sign;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Signature Specification.
 */
public final class GordianSignatureSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairType.
     */
    private final GordianKeyPairType theKeyPairType;

    /**
     * SignatureType.
     */
    private final GordianSignatureType theSignatureType;

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
     * @param pKeyPairType the keyPairType
     * @param pSignatureSpec the signatureSpec
     */
    public GordianSignatureSpec(final GordianKeyPairType pKeyPairType,
                                final Object pSignatureSpec) {
        /* Store parameters */
        this(pKeyPairType, GordianSignatureType.NATIVE, pSignatureSpec);
    }

    /**
     * Constructor.
     * @param pKeyPairType the keyPairType
     * @param pSignatureType the signatureType
     */
    public GordianSignatureSpec(final GordianKeyPairType pKeyPairType,
                                final GordianSignatureType pSignatureType) {
        /* Store parameters */
        this(pKeyPairType, pSignatureType, null);
    }

    /**
     * Constructor.
     * @param pKeyPairType the keyPairType
     * @param pSignatureType the signatureType
     * @param pSignatureSpec the signatureSpec
     */
    public GordianSignatureSpec(final GordianKeyPairType pKeyPairType,
                                final GordianSignatureType pSignatureType,
                                final Object pSignatureSpec) {
        /* Store parameters */
        theKeyPairType = pKeyPairType;
        theSignatureType = pSignatureType;
        theSignatureSpec = pSignatureSpec;
        isValid = checkValidity();
    }

    /**
     * Create RSASpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec rsa(final GordianSignatureType pSignatureType,
                                           final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianKeyPairType.RSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create DSASpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dsa(final GordianSignatureType pSignatureType,
                                           final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianKeyPairType.DSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create ECSpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec ec(final GordianSignatureType pSignatureType,
                                          final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianKeyPairType.EC, pSignatureType, pDigestSpec);
    }

    /**
     * Create SM2Spec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sm2() {
        return new GordianSignatureSpec(GordianKeyPairType.SM2, GordianDigestSpec.sm3());
    }

    /**
     * Create DSTU4145Spec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dstu4145() {
        return new GordianSignatureSpec(GordianKeyPairType.DSTU4145, GordianDigestSpec.gost());
    }

    /**
     * Create GOST2012Spec.
     * @param pLength the length
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec gost2012(final GordianLength pLength) {
        return new GordianSignatureSpec(GordianKeyPairType.GOST2012, GordianDigestSpec.streebog(pLength));
    }

    /**
     * Create EdDSASpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec edDSA() {
        return new GordianSignatureSpec(GordianKeyPairType.EDDSA, GordianSignatureType.NATIVE);
    }

    /**
     * Create SPHINCSPlusSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sphincsPlus() {
        return new GordianSignatureSpec(GordianKeyPairType.SPHINCSPLUS, GordianSignatureType.NATIVE);
    }

    /**
     * Create DilithiumSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dilithium() {
        return new GordianSignatureSpec(GordianKeyPairType.DILITHIUM, GordianSignatureType.NATIVE);
    }

    /**
     * Create falconSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec falcon() {
        return new GordianSignatureSpec(GordianKeyPairType.FALCON, GordianSignatureType.NATIVE);
    }

    /**
     * Create picnicSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec picnic() {
        return new GordianSignatureSpec(GordianKeyPairType.PICNIC, GordianSignatureType.NATIVE);
    }

    /**
     * Create picnicSpec.
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec picnic(final GordianDigestSpec pDigest) {
        return new GordianSignatureSpec(GordianKeyPairType.PICNIC, GordianSignatureType.NATIVE, pDigest);
    }

    /**
     * Create rainbowSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec rainbow() {
        return new GordianSignatureSpec(GordianKeyPairType.RAINBOW, GordianSignatureType.NATIVE);
    }

    /**
     * Create xmssSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmss() {
        return new GordianSignatureSpec(GordianKeyPairType.XMSS, GordianSignatureType.NATIVE);
    }

    /**
     * Create xmssPHSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmssph() {
        return new GordianSignatureSpec(GordianKeyPairType.XMSS, GordianSignatureType.PREHASH);
    }

    /**
     * Create lmsSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec lms() {
        return new GordianSignatureSpec(GordianKeyPairType.LMS, GordianSignatureType.NATIVE);
    }

    /**
     * Create CompositeSpec.
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianSignatureSpec composite(final GordianSignatureSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianSignatureSpec composite(final List<GordianSignatureSpec> pSpecs) {
        return new GordianSignatureSpec(GordianKeyPairType.COMPOSITE, pSpecs);
    }
    /**
     * Obtain the keyPairType.
     * @return the keyPairType.
     */
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
    }

    /**
     * Obtain the SignatureType.
     * @return the signatureType.
     */
    public GordianSignatureType getSignatureType() {
        return theSignatureType;
    }

    /**
     * Obtain the signatureSpec.
     * @return the signatureSpec.
     */
    public Object getSignatureSpec() {
        return theSignatureSpec;
    }

    /**
     * Obtain the DigestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        if (!(theSignatureSpec instanceof GordianDigestSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianDigestSpec) theSignatureSpec;
    }

    /**
     * Obtain the composite signatureSpec iterator.
     * @return the signatureeSpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianSignatureSpec> signatureSpecIterator() {
        if (!(theSignatureSpec instanceof List)) {
            throw new IllegalArgumentException();
        }
        return ((List<GordianSignatureSpec>) theSignatureSpec).iterator();
    }

    /**
     * Is the signatureSpec valid?
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
        if (theKeyPairType == null || theSignatureType == null) {
            return false;
        }
        switch (theKeyPairType) {
            case RSA:
            case DSA:
            case EC:
            case DSTU4145:
            case GOST2012:
            case SM2:
                if (!(theSignatureSpec instanceof GordianDigestSpec)) {
                    return false;
                }
                final GordianDigestSpec mySpec = getDigestSpec();
                return mySpec.isValid() && mySpec.getDigestType().supportsLargeData();
            case EDDSA:
            case SPHINCSPLUS:
            case DILITHIUM:
            case FALCON:
            case RAINBOW:
            case XMSS:
            case LMS:
                return theSignatureSpec == null;
            case PICNIC:
                return theSignatureSpec == null || checkPICNICDigest();
            case COMPOSITE:
                return theSignatureSpec instanceof List && checkComposite();
            default:
                return false;
        }
    }

    /**
     * Check composite spec validity.
     * @return valid true/false
     */
    private boolean checkComposite() {
        final Iterator<GordianSignatureSpec> myIterator = signatureSpecIterator();
        while (myIterator.hasNext()) {
            /* Check that each spec is valid */
            final GordianSignatureSpec mySpec = myIterator.next();
            if (mySpec == null || !mySpec.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check picnic spec validity.
     * @return valid true/false
     */
    private boolean checkPICNICDigest() {
        /* Check that signature length is 512 */
        final GordianDigestSpec myDigest = getDigestSpec();
        if (!GordianLength.LEN_512.equals(myDigest.getDigestLength())) {
            return false;
        }

        /* Switch on DigestType */
        switch (myDigest.getDigestType()) {
            case SHA2:
            case SHA3:
            case SHAKE:
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
            theName = theKeyPairType.toString();
            if (theSignatureType != GordianSignatureType.NATIVE) {
                theName += SEP + theSignatureType.toString();
            }
            if (theSignatureSpec != null) {
                if (theKeyPairType == GordianKeyPairType.COMPOSITE) {
                    final Iterator<GordianSignatureSpec> myIterator = signatureSpecIterator();
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

        /* Make sure that the object is a SignatureSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target SignatureSpec */
        final GordianSignatureSpec myThat = (GordianSignatureSpec) pThat;

        /* Check KeyPairType and signatureType */
        if (theKeyPairType != myThat.getKeyPairType()
                || theSignatureType != myThat.getSignatureType()) {
            return false;
        }

        /* Match signatureSpec */
        return Objects.equals(theSignatureSpec, myThat.theSignatureSpec);
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theSignatureType.hashCode();
        hashCode <<= TethysDataConverter.BYTE_SHIFT;
        if (theSignatureSpec != null) {
            hashCode += theSignatureSpec.hashCode();
        }
        return hashCode;
    }
}
