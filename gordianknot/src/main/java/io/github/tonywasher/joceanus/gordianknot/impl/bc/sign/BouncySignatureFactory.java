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
package io.github.tonywasher.joceanus.gordianknot.impl.bc.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCompositeSigner;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;

import java.util.function.Predicate;

/**
 * Bouncy Signature Factory.
 */
public class BouncySignatureFactory
        extends GordianCoreSignatureFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public BouncySignatureFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public Predicate<GordianSignatureSpec> supportedKeyPairSignatures() {
        return this::validSignatureSpec;
    }

    @Override
    public GordianSignature createSigner(final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Check validity of Signature */
        checkSignatureSpec(pSignatureSpec);

        /* Create the signer */
        return getBCSigner(pSignatureSpec);
    }

    /**
     * Create the BouncyCastle Signer.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws GordianException on error
     */
    private GordianSignature getBCSigner(final GordianSignatureSpec pSignatureSpec) throws GordianException {
        return switch (pSignatureSpec.getKeyPairType()) {
            case RSA -> new BouncyRSASignature(getFactory(), pSignatureSpec);
            case EC -> new BouncyECSignature(getFactory(), pSignatureSpec);
            case DSTU -> new BouncyDSTUSignature(getFactory(), pSignatureSpec);
            case GOST -> new BouncyGOSTSignature(getFactory(), pSignatureSpec);
            case SM2 -> new BouncySM2Signature(getFactory(), pSignatureSpec);
            case EDDSA -> new BouncyEdDSASignature(getFactory(), pSignatureSpec);
            case DSA -> new BouncyDSASignature(getFactory(), pSignatureSpec);
            case SLHDSA -> new BouncySLHDSASignature(getFactory(), pSignatureSpec);
            case MLDSA -> new BouncyMLDSASignature(getFactory(), pSignatureSpec);
            case FALCON -> new BouncyFalconSignature(getFactory(), pSignatureSpec);
            case AIMER -> new BouncyAIMerSignature(getFactory(), pSignatureSpec);
            case FAEST -> new BouncyFaestSignature(getFactory(), pSignatureSpec);
            case HAETAE -> new BouncyHAETAESignature(getFactory(), pSignatureSpec);
            case HAWK -> new BouncyHawkSignature(getFactory(), pSignatureSpec);
            case MAYO -> new BouncyMayoSignature(getFactory(), pSignatureSpec);
            case MQOM -> new BouncyMQOMSignature(getFactory(), pSignatureSpec);
            case QRUOV -> new BouncyQRUOVSignature(getFactory(), pSignatureSpec);
            case SDITH -> new BouncySDitHSignature(getFactory(), pSignatureSpec);
            case SNOVA -> new BouncySnovaSignature(getFactory(), pSignatureSpec);
            case SQISIGN -> new BouncySQIsignSignature(getFactory(), pSignatureSpec);
            case UOV -> new BouncyUOVSignature(getFactory(), pSignatureSpec);
            case PICNIC -> new BouncyPicnicSignature(getFactory(), pSignatureSpec);
            case XMSS -> new BouncyXMSSSignature(getFactory(), pSignatureSpec);
            case LMS -> new BouncyLMSSignature(getFactory(), pSignatureSpec);
            case COMPOSITE -> new GordianCompositeSigner(getFactory(), pSignatureSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSignatureSpec.getKeyPairType()));
        };
    }
}
