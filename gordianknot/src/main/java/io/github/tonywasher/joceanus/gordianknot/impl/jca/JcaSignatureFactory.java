/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCompositeSigner;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaDSASignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaEdDSASignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaFalconSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaGOSTSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaLMSSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaMLDSASignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaMayoSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaPicnicSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaRSASignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaSLHDSASignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaSnovaSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaSignature.JcaXMSSSignature;

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
    JcaSignatureFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public GordianSignature createSigner(final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Check validity of Signature */
        checkSignatureSpec(pSignatureSpec);

        /* Create the signer */
        return getJcaSigner(pSignatureSpec);
    }

    /**
     * Create the Jca Signer.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws GordianException on error
     */
    private GordianSignature getJcaSigner(final GordianSignatureSpec pSignatureSpec) throws GordianException {
        return switch (pSignatureSpec.getKeyPairType()) {
            case RSA -> new JcaRSASignature(getFactory(), pSignatureSpec);
            case EC, SM2, DSA -> new JcaDSASignature(getFactory(), pSignatureSpec);
            case EDDSA -> new JcaEdDSASignature(getFactory(), pSignatureSpec);
            case GOST, DSTU -> new JcaGOSTSignature(getFactory(), pSignatureSpec);
            case XMSS -> new JcaXMSSSignature(getFactory(), pSignatureSpec);
            case SLHDSA -> new JcaSLHDSASignature(getFactory(), pSignatureSpec);
            case MLDSA -> new JcaMLDSASignature(getFactory(), pSignatureSpec);
            case FALCON -> new JcaFalconSignature(getFactory(), pSignatureSpec);
            case MAYO -> new JcaMayoSignature(getFactory(), pSignatureSpec);
            case SNOVA -> new JcaSnovaSignature(getFactory(), pSignatureSpec);
            case PICNIC -> new JcaPicnicSignature(getFactory(), pSignatureSpec);
            case LMS -> new JcaLMSSignature(getFactory(), pSignatureSpec);
            case COMPOSITE -> new GordianCompositeSigner(getFactory(), pSignatureSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSignatureSpec.getKeyPairType()));
        };
    }

    @Override
    protected boolean validSignatureSpec(final GordianSignatureSpec pSpec) {
        /* validate the signatureSpec */
        if (!super.validSignatureSpec(pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSpec;
        return switch (pSpec.getKeyPairType()) {
            case RSA -> validRSASignature(mySpec);
            case EC -> validECSignature(mySpec);
            case DSA -> validDSASignature(mySpec);
            case DSTU, GOST, SM2, XMSS, SLHDSA, MLDSA, FALCON, MAYO, SNOVA, PICNIC, EDDSA, LMS, COMPOSITE -> true;
            default -> false;
        };
    }

    /**
     * Check RSASignature.
     *
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASignature(final GordianCoreSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        return switch (myDigest.getDigestType()) {
            case SHA1, SHA2 -> true;
            case SHA3 -> pSpec.getCoreType().isPSS()
                    || GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
            case SHAKE -> validRSASHAKESignature(pSpec);
            case WHIRLPOOL -> !pSpec.getCoreType().isPSS();
            case RIPEMD, MD2, MD4, MD5 -> GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
            default -> false;
        };
    }

    /**
     * Check RSASHAKESignature.
     *
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASHAKESignature(final GordianCoreSignatureSpec pSpec) {
        /* Must be pure SHAKE */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        if (!pSpec.getCoreType().isPSS()) {
            return false;
        }

        /* Switch on SignatureType */
        return switch (pSpec.getSignatureType()) {
            case PSS128 -> GordianDigestState.STATE128.equals(myDigest.getDigestState());
            case PSS256 -> GordianDigestState.STATE256.equals(myDigest.getDigestState());
            default -> false;
        };
    }

    /**
     * Check ECSignature.
     *
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validECSignature(final GordianCoreSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianCoreDigestSpec myDigest = pSpec.getDigestSpec();
        return switch (myDigest.getDigestType()) {
            case SHA2 -> !myDigest.isSha2Hybrid();
            case SHA3 -> !GordianSignatureType.NR.equals(pSpec.getSignatureType());
            case SHAKE -> GordianSignatureType.DSA.equals(pSpec.getSignatureType());
            default -> false;
        };
    }

    /**
     * Check ECSignature.
     *
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validDSASignature(final GordianCoreSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianCoreDigestSpec myDigest = pSpec.getDigestSpec();
        return switch (myDigest.getDigestType()) {
            case SHA2 -> !myDigest.isSha2Hybrid();
            case SHA1, SHA3 -> true;
            default -> false;
        };
    }
}
