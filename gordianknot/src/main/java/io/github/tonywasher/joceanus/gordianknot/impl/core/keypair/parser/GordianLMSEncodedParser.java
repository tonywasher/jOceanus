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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreLMSSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;

import java.io.IOException;

/**
 * LMS Encoded parser.
 */
public final class GordianLMSEncodedParser implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new GordianLMSEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse public key */
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            final HSSPublicKeyParameters myPublic = (HSSPublicKeyParameters) PublicKeyFactory.createKey(pInfo);
            final int myDepth = myPublic.getL();
            final LMSPublicKeyParameters myLMSPublicKey = myPublic.getLMSPublicKey();
            final GordianLMSSpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
            return myBuilder.hss(myKeySpec.getHash(), myKeySpec.getHeight(), myKeySpec.getWidth(), myKeySpec.getLength(), myDepth);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse public key */
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            final HSSPrivateKeyParameters myPrivate = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
            final int myDepth = myPrivate.getL();
            final LMSPublicKeyParameters myLMSPublicKey = myPrivate.getPublicKey().getLMSPublicKey();
            final GordianLMSSpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
            return myBuilder.hss(myKeySpec.getHash(), myKeySpec.getHeight(), myKeySpec.getWidth(), myKeySpec.getLength(), myDepth);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    /**
     * Obtain keySpec from public key.
     *
     * @param pPublic the publicKeyParams
     * @return the LMSKeySpec
     */
    static GordianLMSSpec determineKeyPairSpec(final LMSPublicKeyParameters pPublic) {
        return GordianCoreLMSSpec.determineSpec(pPublic.getSigParameters(), pPublic.getOtsParameters());
    }
}
