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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreECSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSM2Spec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.ec.CustomNamedCurves;

/**
 * EC Encoded parser.
 */
public final class GordianECEncodedParser implements GordianEncodedParser {
    /**
     * The namedCurve error.
     */
    static final String ERROR_NAMEDCURVE = "Not a Named Curve";

    /**
     * The unsupportedCurve error.
     */
    static final String ERROR_UNSUPCURVE = "Unsupported Curve: ";

    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(X9ObjectIdentifiers.id_ecPublicKey, new GordianECEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getAlgorithm();
        final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
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
    private static GordianKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws GordianException {
        /* Reject if not a named curve */
        if (!pParms.isNamedCurve()) {
            throw new GordianDataException(ERROR_NAMEDCURVE);
        }

        /* Check for EC named curve */
        final ASN1ObjectIdentifier myId = (ASN1ObjectIdentifier) pParms.getParameters();
        String myName = CustomNamedCurves.getName(myId);
        if (myName == null) {
            myName = ECNamedCurveTable.getName(myId);
        }
        if (myName != null) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            final GordianCoreECSpec myECCurve = GordianCoreECSpec.getCurveForName(myName);
            if (myECCurve != null) {
                return myBuilder.ec(myECCurve.getSpec());
            }
            final GordianCoreSM2Spec mySM2Curve = GordianCoreSM2Spec.getCurveForName(myName);
            if (mySM2Curve != null) {
                return myBuilder.sm2(mySM2Curve.getSpec());
            }
            throw new GordianDataException(ERROR_UNSUPCURVE + myName);
        }

        /* Curve is not supported */
        throw new GordianDataException(ERROR_UNSUPCURVE + pParms);
    }
}
