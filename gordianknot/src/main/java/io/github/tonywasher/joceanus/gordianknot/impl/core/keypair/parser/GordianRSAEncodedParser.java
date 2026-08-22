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
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreRSASpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.math.BigInteger;

/**
 * RSA Encoded parser.
 */
public final class GordianRSAEncodedParser
        implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PKCSObjectIdentifiers.rsaEncryption, new GordianRSAEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse the publicKey */
            final RSAPublicKey myPublic = RSAPublicKey.getInstance(pInfo.parsePublicKey());
            return determineKeyPairSpec(myPublic.getModulus());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Parse the publicKey */
            final RSAPrivateKey myPrivate = RSAPrivateKey.getInstance(pInfo.parsePrivateKey());
            return determineKeyPairSpec(myPrivate.getModulus());

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    /**
     * Obtain keySpec from Modulus.
     *
     * @param pModulus the modulus
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final BigInteger pModulus) throws GordianException {
        final GordianCoreRSASpec mySpec = GordianCoreRSASpec.getRSASpecForInteger(pModulus);
        if (mySpec == null) {
            throw new GordianDataException("RSA strength not supported: " + pModulus.bitLength());
        }
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.rsa(mySpec.getSpec());
    }
}
