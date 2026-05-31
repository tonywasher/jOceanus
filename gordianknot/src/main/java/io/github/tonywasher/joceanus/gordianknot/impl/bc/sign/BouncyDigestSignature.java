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
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;

/**
 * Digest signature base.
 */
public abstract class BouncyDigestSignature
        extends GordianCoreSignature {
    /**
     * The Digest.
     */
    private BouncyDigest theDigest;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncyDigestSignature(final GordianBaseFactory pFactory,
                          final GordianSignatureSpec pSpec) throws GordianException {
        super(pFactory, pSpec);
        theDigest = pSpec.getSignatureSpec() == null
                ? new BouncyDigest(null, new NullDigest())
                : (BouncyDigest) getDigestFactory().createDigest(((GordianCoreSignatureSpec) pSpec).getDigestSpec());
    }

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @param pDigest  the digest
     */
    BouncyDigestSignature(final GordianBaseFactory pFactory,
                          final GordianSignatureSpec pSpec,
                          final Digest pDigest) {
        super(pFactory, pSpec);
        theDigest = new BouncyDigest(((GordianCoreSignatureSpec) pSpec).getDigestSpec(), pDigest);
    }

    /**
     * Set the digest.
     *
     * @param pSpec the digestSpec.
     * @throws GordianException on error
     */
    protected void setDigest(final GordianDigestSpec pSpec) throws GordianException {
        theDigest = pSpec == null
                ? new BouncyDigest(null, new NullDigest())
                : (BouncyDigest) getDigestFactory().createDigest(pSpec);
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theDigest.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theDigest.update(pByte);
    }

    @Override
    public void reset() {
        theDigest.reset();
    }

    /**
     * Obtain the calculated digest.
     *
     * @return the digest.
     */
    protected byte[] getDigest() {
        return theDigest.finish();
    }

    @Override
    protected BouncyKeyPair getKeyPair() {
        return (BouncyKeyPair) super.getKeyPair();
    }
}
