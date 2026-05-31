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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.signers.X931Signer;

/**
 * PSS signature base.
 */
abstract class BouncyPSSSignature
        extends GordianCoreSignature {
    /**
     * The RSA Signer.
     */
    private final Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncyPSSSignature(final GordianBaseFactory pFactory,
                       final GordianSignatureSpec pSpec) throws GordianException {
        super(pFactory, pSpec);
        theSigner = getRSASigner(pFactory, (GordianCoreSignatureSpec) pSpec);
    }

    /**
     * Obtain the signer.
     *
     * @return the signer.
     */
    protected Signer getSigner() {
        return theSigner;
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theSigner.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theSigner.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes) {
        theSigner.update(pBytes, 0, pBytes.length);
    }

    @Override
    public void reset() {
        theSigner.reset();
    }

    /**
     * Obtain RSASigner.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec
     * @return the RSASigner
     * @throws GordianException on error
     */
    private static Signer getRSASigner(final GordianBaseFactory pFactory,
                                       final GordianCoreSignatureSpec pSpec) throws GordianException {
        /* Create the digest */
        final GordianDigestSpec myDigestSpec = pSpec.getDigestSpec();
        final GordianDigestFactory myFactory = pFactory.getDigestFactory();
        final BouncyDigest myDigest = (BouncyDigest) myFactory.createDigest(myDigestSpec);
        final GordianDigestSpecBuilder myBuilder = myFactory.newDigestSpecBuilder();
        final int mySaltLength = myDigestSpec.getDigestLength().getByteLength();

        /* Access the signature type */
        return switch (pSpec.getSignatureType()) {
            case ISO9796D2 ->
                    new ISO9796d2Signer(new RSABlindedEngine(), myDigest.getDigest(), ISOTrailers.noTrailerAvailable(myDigest.getDigest()));
            case X931 ->
                    new X931Signer(new RSABlindedEngine(), myDigest.getDigest(), ISOTrailers.noTrailerAvailable(myDigest.getDigest()));
            case PREHASH -> new RSADigestSigner(myDigest.getDigest());
            case PSS128 -> new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(),
                    ((BouncyDigest) pFactory.getDigestFactory().createDigest(myBuilder.shake128())).getDigest(), mySaltLength);
            case PSS256 -> new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(),
                    ((BouncyDigest) pFactory.getDigestFactory().createDigest(myBuilder.shake256())).getDigest(), mySaltLength);
            default -> new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(), mySaltLength);
        };
    }
}
