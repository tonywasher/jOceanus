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
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

import java.io.IOException;

/**
 * XMSS Encoded parser.
 */
public final class GordianXMSSEncodedParser implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PQCObjectIdentifiers.xmss, new GordianXMSSEncodedParser());
        pIdManager.registerParser(IANAObjectIdentifiers.id_alg_xmss_hashsig, new GordianXMSSEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            final XMSSPublicKeyParameters myParams = (XMSSPublicKeyParameters) PublicKeyFactory.createKey(pInfo);
            final GordianCoreXMSSSpec mySpec = GordianCoreXMSSSpec.determineSpecFromParameters(myParams.getParameters());
            return myBuilder.xmss(mySpec);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            final XMSSPrivateKeyParameters myParams = (XMSSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
            final GordianCoreXMSSSpec mySpec = GordianCoreXMSSSpec.determineSpecFromParameters(myParams.getParameters());
            return myBuilder.xmss(mySpec);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }
}
