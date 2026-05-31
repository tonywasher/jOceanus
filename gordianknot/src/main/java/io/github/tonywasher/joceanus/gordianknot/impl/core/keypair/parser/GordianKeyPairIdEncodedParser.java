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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairIdSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * KeyPairId Encoded parser.
 */
public final class GordianKeyPairIdEncodedParser implements GordianEncodedParser {
    /**
     * AsymKeySpec.
     */
    private final GordianKeyPairSpec theKeySpec;

    /**
     * Constructor.
     *
     * @param pKeySpec the keySpec
     */
    GordianKeyPairIdEncodedParser(final GordianKeyPairSpec pKeySpec) {
        theKeySpec = pKeySpec;
    }

    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     * @param pKeySpecs  the keyPair specs
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager,
                                final GordianCoreKeyPairIdSpec<?>[] pKeySpecs) {
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        for (GordianCoreKeyPairIdSpec<?> mySubSpec : pKeySpecs) {
            final GordianKeyPairSpec mySpec = myBuilder.withKeyPairType(mySubSpec.getKeyPairType())
                    .withEnumSubSpec(mySubSpec.getSpec()).build();
            pIdManager.registerParser(mySubSpec.getIdentifier(), new GordianKeyPairIdEncodedParser(mySpec));
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        return theKeySpec;
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        return theKeySpec;
    }
}
