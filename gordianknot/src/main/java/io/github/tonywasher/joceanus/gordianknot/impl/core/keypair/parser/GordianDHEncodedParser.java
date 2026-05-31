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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHParameters;

/**
 * DH Encoded parser.
 */
public class GordianDHEncodedParser implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PKCSObjectIdentifiers.dhKeyAgreement, new GordianDHEncodedParser());
        pIdManager.registerParser(X9ObjectIdentifiers.dhpublicnumber, new GordianDHEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
        return determineKeyPairSpec(myParms);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
        return determineKeyPairSpec(myParms);
    }

    /**
     * Obtain parameters from encoded sequence.
     *
     * @param pId the algorithm Identifier
     * @return the parameters
     */
    public static DHParameters determineParameters(final AlgorithmIdentifier pId) {
        /* Access algorithmId */
        final ASN1ObjectIdentifier myId = pId.getAlgorithm();

        /* If this is key agreement */
        if (PKCSObjectIdentifiers.dhKeyAgreement.equals(myId)) {
            /* Access the DHParameter */
            final DHParameter myParams = DHParameter.getInstance(pId.getParameters());

            /* If we have an L value */
            return myParams.getL() != null
                    ? new DHParameters(myParams.getP(), myParams.getG(), null, myParams.getL().intValue())
                    : new DHParameters(myParams.getP(), myParams.getG());

        } else if (X9ObjectIdentifiers.dhpublicnumber.equals(myId)) {
            /* Access Domain Parameters */
            final DomainParameters myParams = DomainParameters.getInstance(pId.getParameters());

            return new DHParameters(myParams.getP(), myParams.getG(), myParams.getQ(), myParams.getJ(), null);
        } else {
            throw new IllegalArgumentException("unknown algorithm type: " + myId);
        }
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final DHParameters pParms) throws GordianException {
        final GordianCoreDHSpec myGroup = GordianCoreDHSpec.getSpecForParams(pParms);
        if (myGroup == null) {
            throw new GordianDataException("Unsupported DH parameters: "
                    + pParms.getP().bitLength());
        }
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.dh(myGroup.getSpec());
    }
}
