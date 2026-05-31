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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSMTLayers;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.util.Pack;

import java.io.IOException;
import java.util.Objects;

/**
 * XMSSMT Encoded parser.
 */
public final class GordianXMSSMTEncodedParser implements GordianEncodedParser {
    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PQCObjectIdentifiers.xmss_mt, new GordianXMSSMTEncodedParser());
        pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmssmt, new GordianXMSSMTEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getAlgorithm();
        final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
        if (myParms != null) {
            return determineKeyPairSpec(myParms);
        }

        /* Protect against exceptions */
        try {
            final byte[] keyEnc = Objects.requireNonNull(ASN1OctetString.getInstance(pInfo.parsePublicKey())).getOctets();
            final int myOID = Pack.bigEndianToInt(keyEnc, 0);
            final XMSSMTParameters myParams = XMSSMTParameters.lookupByOID(myOID);
            final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.xmssmt(GordianXMSSEncodedParser.determineKeyType(myParams.getTreeDigestOID()),
                    GordianXMSSEncodedParser.determineHeight(myParams.getHeight()), determineLayers(myParams.getLayers()));
        } catch (IOException e) {
            throw new GordianIOException("Failed to resolve key", e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
        final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final XMSSMTKeyParams pParms) throws GordianException {
        final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
        final GordianXMSSHeight myHeight = GordianXMSSEncodedParser.determineHeight(pParms.getHeight());
        final GordianXMSSMTLayers myLayers = determineLayers(pParms.getLayers());
        final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.xmssmt(GordianXMSSEncodedParser.determineKeyType(myDigest), myHeight, myLayers);
    }

    /**
     * Obtain layers.
     *
     * @param pLayers the layers
     * @return the xmssMTLayers
     * @throws GordianException on error
     */
    static GordianXMSSMTLayers determineLayers(final int pLayers) throws GordianException {
        /* Loo through the heights */
        for (GordianXMSSMTLayers myLayers : GordianXMSSMTLayers.values()) {
            if (myLayers.getLayers() == pLayers) {
                return myLayers;
            }
        }

        /* Layers is not supported */
        throw new GordianDataException("Invalid layers: " + pLayers);
    }
}
