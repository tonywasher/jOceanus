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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9SignMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9SignMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9SignUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9SignUserPublicKey;

/**
 * SM9 signature.
 */
public class JcaSM9Signature
        extends JcaSignature {
    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pSignatureSpec the signatureSpec
     * @throws GordianException on error
     */
    JcaSM9Signature(final GordianBaseFactory pFactory,
                    final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Initialise class */
        super(pFactory, pSignatureSpec);

        /* Create the signature class */
        setSigner(getJavaSignature("SM9", false));
    }

    @Override
    JcaSM9SignUserPublicKey getPublicKey(final GordianSignParams pParams) throws GordianException {
        final JcaKeyPair myKeyPair = checkKeyPair();
        final GordianSM9SignType myKeyType = (GordianSM9SignType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case SIGNMASTER -> {
                final JcaSM9SignMasterPublicKey myPublic = (JcaSM9SignMasterPublicKey) myKeyPair.getPublicKey();
                yield myPublic.deriveUserPublicKey(GordianSM9SignType.SIGN, pParams.getIdentity());
            }
            case SIGN -> (JcaSM9SignUserPublicKey) myKeyPair.getPublicKey();
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    @Override
    JcaSM9SignUserPrivateKey getPrivateKey(final GordianSignParams pParams) throws GordianException {
        final JcaKeyPair myKeyPair = checkKeyPair();
        final GordianSM9SignType myKeyType = (GordianSM9SignType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case SIGNMASTER -> {
                final JcaSM9SignMasterPrivateKey myPrivate = (JcaSM9SignMasterPrivateKey) myKeyPair.getPrivateKey();
                yield myPrivate.newUserPrivateKey(GordianSM9EncryptType.ENCRYPT, pParams.getIdentity());
            }
            case SIGN -> (JcaSM9SignUserPrivateKey) myKeyPair.getPrivateKey();
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }
}
