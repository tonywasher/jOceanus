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
package net.sourceforge.joceanus.jgordianknot.api.sign;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
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
     * AsymKeyType.
     */
    private final GordianAsymKeyType theAsymKeyType;

    /**
     * SignatureType.
     */
    private final GordianSignatureType theSignatureType;

    /**
     * DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

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
     * @param pAsymKeyType the asymKeyType
     * @param pDigestSpec the digestSpec
     */
    public GordianSignatureSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        this(pAsymKeyType, GordianSignatureType.NATIVE, pDigestSpec);
    }

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pSignatureType the signatureType
     */
    public GordianSignatureSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianSignatureType pSignatureType) {
        /* Store parameters */
        this(pAsymKeyType, pSignatureType, null);
    }

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     */
    public GordianSignatureSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianSignatureType pSignatureType,
                                final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        theAsymKeyType = pAsymKeyType;
        theSignatureType = pSignatureType;
        theDigestSpec = pDigestSpec;
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
        return new GordianSignatureSpec(GordianAsymKeyType.RSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create DSASpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dsa(final GordianSignatureType pSignatureType,
                                           final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.DSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create ECSpec.
     * @param pSignatureType the signatureType
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec ec(final GordianSignatureType pSignatureType,
                                          final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.EC, pSignatureType, pDigestSpec);
    }

    /**
     * Create SM2Spec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sm2() {
        return new GordianSignatureSpec(GordianAsymKeyType.SM2, GordianDigestSpec.sm3());
    }

    /**
     * Create DSTU4145Spec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dstu4145() {
        return new GordianSignatureSpec(GordianAsymKeyType.DSTU4145, GordianDigestSpec.gost());
    }

    /**
     * Create GOST2012Spec.
     * @param pLength the length
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec gost2012(final GordianLength pLength) {
        return new GordianSignatureSpec(GordianAsymKeyType.GOST2012, GordianDigestSpec.streebog(pLength));
    }

    /**
     * Create EdDSActxSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec edDSActx() {
        return new GordianSignatureSpec(GordianAsymKeyType.EDDSA, GordianSignatureType.NATIVE);
    }

    /**
     * Create EdDSASpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec edDSA() {
        return new GordianSignatureSpec(GordianAsymKeyType.EDDSA, GordianSignatureType.PURE);
    }

    /**
     * Create EdDSAphSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec edDSAph() {
        return new GordianSignatureSpec(GordianAsymKeyType.EDDSA, GordianSignatureType.PREHASH);
    }

    /**
     * Create SPHINCSSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sphincs() {
        return new GordianSignatureSpec(GordianAsymKeyType.SPHINCS, GordianSignatureType.PREHASH);
    }

    /**
     * Create RainbowSpec.
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec rainbow(final GordianDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianAsymKeyType.RAINBOW, pDigestSpec);
    }

    /**
     * Create xmssSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmss() {
        return new GordianSignatureSpec(GordianAsymKeyType.XMSS, GordianSignatureType.PURE);
    }

    /**
     * Create xmssPHSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmssph() {
        return new GordianSignatureSpec(GordianAsymKeyType.XMSS, GordianSignatureType.PREHASH);
    }

    /**
     * Create qTESLASpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec qTESLA() {
        return new GordianSignatureSpec(GordianAsymKeyType.QTESLA, GordianSignatureType.PURE);
    }

    /**
     * Create lmsSpec.
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec lms() {
        return new GordianSignatureSpec(GordianAsymKeyType.LMS, GordianSignatureType.PURE);
    }

    /**
     * Create default signatureSpec for key.
     * @param pKeySpec the keySpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec defaultForKey(final GordianAsymKeySpec pKeySpec) {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return rsa(GordianSignatureType.PSS, GordianDigestSpec.sha3(GordianLength.LEN_512));
            case DSA:
                return dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_512));
            case EC:
                return ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_512));
            case SM2:
                return sm2();
            case DSTU4145:
                return dstu4145();
            case GOST2012:
                return gost2012(GordianLength.LEN_512);
            case EDDSA:
                return edDSA();
            case RAINBOW:
                return rainbow(GordianDigestSpec.sha2(GordianLength.LEN_512));
            case SPHINCS:
                return sphincs();
            case XMSS:
                return xmss();
            case QTESLA:
                return qTESLA();
            case LMS:
                return lms();
            default:
                return null;
        }
    }

    /**
     * Obtain the AsymKeyType.
     * @return the asymKeyType.
     */
    public GordianAsymKeyType getAsymKeyType() {
        return theAsymKeyType;
    }

    /**
     * Obtain the SignatureType.
     * @return the signatureType.
     */
    public GordianSignatureType getSignatureType() {
        return theSignatureType;
    }

    /**
     * Obtain the DigestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
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
        if (theAsymKeyType == null || theSignatureType == null) {
            return false;
        }
        switch (theAsymKeyType) {
            case RSA:
            case DSA:
            case EC:
            case DSTU4145:
            case GOST2012:
            case SM2:
            case RAINBOW:
                return theDigestSpec != null && theDigestSpec.isValid() && theDigestSpec.getDigestType().supportsLargeData();
            case EDDSA:
            case SPHINCS:
            case QTESLA:
            case XMSS:
            case LMS:
                return theDigestSpec == null;
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
            if (theSignatureType != GordianSignatureType.NATIVE) {
                theName += SEP + theSignatureType.toString();
            }
            if (theDigestSpec != null) {
                theName += SEP + theDigestSpec.toString();
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

        /* Check AsymKeyType and signatureType */
        if (theAsymKeyType != myThat.getAsymKeyType()
                || theSignatureType != myThat.getSignatureType()) {
            return false;
        }

        /* Match digestSpec */
        final GordianDigestSpec myDigest = myThat.getDigestSpec();
        return theDigestSpec == null
               ? myDigest == null
               : theDigestSpec.equals(myThat.getDigestSpec());
    }

    @Override
    public int hashCode() {
        int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theSignatureType.hashCode();
        hashCode <<= TethysDataConverter.BYTE_SHIFT;
        if (theDigestSpec != null) {
            hashCode += theDigestSpec.hashCode();
        }
        return hashCode;
    }

    /**
     * Obtain a list of all possible signatures for the keyType.
     * @param pKeyType the keyType
     * @return the list
     */
    public static List<GordianSignatureSpec> listPossibleSignatures(final GordianAsymKeyType pKeyType) {
        /* Access the list of possible digests */
        final List<GordianSignatureSpec> mySignatures = new ArrayList<>();
        final List<GordianDigestSpec> myDigests = GordianDigestSpec.listAll();

        /* For each supported signature */
        for (GordianSignatureType mySignType : GordianSignatureType.values()) {
            /* Skip if the signatureType is not valid */
            if (mySignType.isSupported(pKeyType)) {
                /* If we need null-digestSpec */
                if (pKeyType.nullDigestForSignatures()) {
                    /* Add the signature */
                    mySignatures.add(new GordianSignatureSpec(pKeyType, mySignType));
                    continue;
                }

                /* For each possible digestSpec */
                for (GordianDigestSpec mySpec : myDigests) {
                    /* Add the signature */
                    mySignatures.add(new GordianSignatureSpec(pKeyType, mySignType, mySpec));
                }
            }
        }

        /* Return the list */
        return mySignatures;
    }
}
