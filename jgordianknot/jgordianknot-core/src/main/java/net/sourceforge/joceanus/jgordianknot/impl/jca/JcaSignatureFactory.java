/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaDSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaEdDSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaGOSTSignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaLMSSignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaRSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaRainbowSignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaSPHINCSPlusSignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaSPHINCSSignature;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaSignature.JcaXMSSSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca Signature Factory.
 */
public class JcaSignatureFactory
        extends GordianCoreSignatureFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaSignatureFactory(final JcaFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public GordianKeyPairSignature createKeyPairSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        checkSignatureSpec(pSignatureSpec);

        /* Create the signer */
        return getJcaSigner(pSignatureSpec);
    }

    /**
     * Create the BouncyCastle Signature via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws OceanusException on error
     */
    static Signature getJavaSignature(final String pAlgorithm,
                                      final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return Signature.getInstance(pAlgorithm, postQuantum
                                                     ? JcaFactory.BCPQPROV
                                                     : JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Signature", e);
        }
    }

    /**
     * Create the Jca Signer.
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianKeyPairSignature getJcaSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getKeyPairType()) {
            case RSA:
                return new JcaRSASignature(getFactory(), pSignatureSpec);
            case EC:
            case SM2:
            case DSA:
                return new JcaDSASignature(getFactory(), pSignatureSpec);
            case EDDSA:
                return new JcaEdDSASignature(getFactory(), pSignatureSpec);
            case GOST2012:
            case DSTU4145:
                return new JcaGOSTSignature(getFactory(), pSignatureSpec);
            case XMSS:
                return new JcaXMSSSignature(getFactory(), pSignatureSpec);
            case SPHINCS:
                return new JcaSPHINCSSignature(getFactory(), pSignatureSpec);
            case SPHINCSPLUS:
                return new JcaSPHINCSPlusSignature(getFactory(), pSignatureSpec);
            case RAINBOW:
                return new JcaRainbowSignature(getFactory(), pSignatureSpec);
            case LMS:
                return new JcaLMSSignature(getFactory(), pSignatureSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSignatureSpec.getKeyPairType()));
        }
    }

    @Override
    protected boolean validSignatureSpec(final GordianSignatureSpec pSpec) {
        /* validate the signatureSpec */
        if (!super.validSignatureSpec(pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        switch (pSpec.getKeyPairType()) {
            case RSA:
                return validRSASignature(pSpec);
            case EC:
                return validECSignature(pSpec);
            case DSTU4145:
            case GOST2012:
            case SM2:
                return true;
            case DSA:
                return validDSASignature(pSpec);
            case RAINBOW:
                return validRainbowSignature(pSpec.getDigestSpec());
            case XMSS:
            case SPHINCS:
            case SPHINCSPLUS:
            case EDDSA:
            case LMS:
                return true;
            case DH:
            case NEWHOPE:
            case MCELIECE:
            case XDH:
            default:
                return false;
        }
    }

    /**
     * Check RSASignature.
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA1:
            case SHA2:
                return true;
            case SHA3:
                return pSpec.getSignatureType().isPSS()
                       || GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
            case SHAKE:
                return validRSASHAKESignature(pSpec);
            case WHIRLPOOL:
                return !pSpec.getSignatureType().isPSS();
            case RIPEMD:
            case MD2:
            case MD4:
            case MD5:
                return GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
            default:
                return false;
        }
    }

    /**
     * Check RSASHAKESignature.
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASHAKESignature(final GordianSignatureSpec pSpec) {
        /* Must be pure SHAKE */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        if (!pSpec.getSignatureType().isPSS() || !myDigest.isPureSHAKE()) {
            return false;
        }

        /* Switch on SignatureType */
        switch (pSpec.getSignatureType()) {
            case PSS128:
                return GordianLength.LEN_128.equals(myDigest.getStateLength());
            case PSS256:
                return GordianLength.LEN_256.equals(myDigest.getStateLength());
            default:
                return false;
        }
    }

    /**
     * Check ECSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validECSignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA2:
                return myDigest.getStateLength() == null;
            case SHA3:
                return !GordianSignatureType.NR.equals(pSpec.getSignatureType());
            case SHAKE:
                return GordianSignatureType.DSA.equals(pSpec.getSignatureType());
            default:
                return false;
        }
    }

    /**
     * Check ECSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validDSASignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA2:
                return myDigest.getStateLength() == null;
            case SHA1:
            case SHA3:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check RainbowSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validRainbowSignature(final GordianDigestSpec pSpec) {
        return pSpec.getDigestType() == GordianDigestType.SHA2
                && pSpec.getStateLength() == null;
    }
}
