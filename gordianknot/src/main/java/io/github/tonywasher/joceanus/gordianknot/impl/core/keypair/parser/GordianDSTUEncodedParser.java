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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.crypto.params.ECDomainParameters;

/**
 * DSTU Encoded parser.
 */
public final class GordianDSTUEncodedParser implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(UAObjectIdentifiers.dstu4145be, new GordianDSTUEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getAlgorithm();
        final DSTU4145Params myParms = DSTU4145Params.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
        final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final DSTU4145Params pParms) throws GordianException {
        /* Reject if not a named curve */
        if (!pParms.isNamedCurve()) {
            throw new GordianDataException(GordianECEncodedParser.ERROR_NAMEDCURVE);
        }
        return determineKeyPairSpec(pParms.getNamedCurve());
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws GordianException {
        /* Reject if not a named curve */
        if (!pParms.isNamedCurve()) {
            throw new GordianDataException(GordianECEncodedParser.ERROR_NAMEDCURVE);
        }
        return determineKeyPairSpec((ASN1ObjectIdentifier) pParms.getParameters());
    }

    /**
     * Obtain keySpec from curveId.
     *
     * @param pId the curveId
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final ASN1ObjectIdentifier pId) throws GordianException {
        /* Check for EC named surve */
        final String myName = pId.toString();
        final ECDomainParameters myParms = DSTU4145NamedCurves.getByOID(pId);
        final GordianCoreDSTUSpec myCurve = GordianCoreDSTUSpec.getCurveForName(myName);
        if (myParms == null || myCurve == null) {
            throw new GordianDataException(GordianECEncodedParser.ERROR_UNSUPCURVE + myName);
        }
        final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.dstu4145(myCurve.getSpec());
    }
}
