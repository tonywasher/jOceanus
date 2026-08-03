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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * SM9 Encoded parser.
 */
public class GordianSM9EncodedParser
        implements GordianEncodedParser {
    /**
     * KeySpec.
     */
    private final GordianKeyPairSpec theSpec;

    /**
     * Constructor.
     *
     * @param pKeySpec the keySpec
     */
    private GordianSM9EncodedParser(final GordianKeyPairSpec pKeySpec) {
        theSpec = pKeySpec;
    }

    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        pIdManager.registerParser(GMObjectIdentifiers.sm9encrypt,
                new GordianSM9EncodedParser(myBuilder.sm9(GordianSM9EncryptType.ENCMASTER)));
        pIdManager.registerParser(GMObjectIdentifiers.sm9sign,
                new GordianSM9EncodedParser(myBuilder.sm9(GordianSM9SignType.SIGNMASTER)));
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        return theSpec;
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        return theSpec;
    }
}
