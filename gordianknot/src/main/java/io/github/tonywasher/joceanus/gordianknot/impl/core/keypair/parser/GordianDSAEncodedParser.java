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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

/**
 * DSA Encoded parser.
 */
public final class GordianDSAEncodedParser
        implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(X9ObjectIdentifiers.id_dsa, new GordianDSAEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getAlgorithm();
        final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
        final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final DSAParameter pParms) throws GordianException {
        final GordianCoreDSASpec mySpec = GordianCoreDSASpec.getDSASpecForParms(pParms);
        if (mySpec == null) {
            throw new GordianDataException("Unsupported DSA parameters: "
                    + pParms.getP().bitLength() + ":" + pParms.getQ().bitLength());
        }
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.dsa(mySpec.getSpec());
    }
}
