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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.util.Pack;

import java.io.IOException;
import java.util.Objects;

/**
 * XMSS Encoded parser.
 */
public final class GordianXMSSEncodedParser implements GordianEncodedParser {
    /**
     * The treeDigest error.
     */
    private static final String ERROR_TREEDIGEST = "Unsupported treeDigest: ";

    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(PQCObjectIdentifiers.xmss, new GordianXMSSEncodedParser());
        pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmss, new GordianXMSSEncodedParser());
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getAlgorithm();
        final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
        if (myParms != null) {
            return determineKeyPairSpec(myParms);
        }

        /* Protect against exceptions */
        try {
            final byte[] keyEnc = Objects.requireNonNull(ASN1OctetString.getInstance(pInfo.parsePublicKey())).getOctets();
            final int myOID = Pack.bigEndianToInt(keyEnc, 0);
            final XMSSParameters myParams = XMSSParameters.lookupByOID(myOID);
            final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.xmss(determineKeyType(myParams.getTreeDigestOID()), determineHeight(myParams.getHeight()));
        } catch (IOException e) {
            throw new GordianIOException("Failed to resolve key", e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
        final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
        return determineKeyPairSpec(myParms);
    }

    /**
     * Obtain keySpec from Parameters.
     *
     * @param pParms the parameters
     * @return the keySpec
     * @throws GordianException on error
     */
    private static GordianKeyPairSpec determineKeyPairSpec(final XMSSKeyParams pParms) throws GordianException {
        final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
        final GordianXMSSHeight myHeight = determineHeight(pParms.getHeight());
        final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
        return myBuilder.xmss(determineKeyType(myDigest), myHeight);
    }

    /**
     * Obtain keyType from digest.
     *
     * @param pDigest the treeDigest
     * @return the keyType
     * @throws GordianException on error
     */
    static GordianXMSSDigestType determineKeyType(final ASN1ObjectIdentifier pDigest) throws GordianException {
        if (pDigest.equals(NISTObjectIdentifiers.id_sha256)) {
            return GordianXMSSDigestType.SHA256;
        }
        if (pDigest.equals(NISTObjectIdentifiers.id_sha512)) {
            return GordianXMSSDigestType.SHA512;
        }
        if (pDigest.equals(NISTObjectIdentifiers.id_shake128)) {
            return GordianXMSSDigestType.SHAKE128;
        }
        if (pDigest.equals(NISTObjectIdentifiers.id_shake256)) {
            return GordianXMSSDigestType.SHAKE256;
        }

        /* Tree Digest is not supported */
        throw new GordianDataException(ERROR_TREEDIGEST + pDigest);
    }

    /**
     * Obtain height.
     *
     * @param pHeight the height
     * @return the xmssHeight
     * @throws GordianException on error
     */
    static GordianXMSSHeight determineHeight(final int pHeight) throws GordianException {
        /* Loo through the heights */
        for (GordianXMSSHeight myHeight : GordianXMSSHeight.values()) {
            if (myHeight.getHeight() == pHeight) {
                return myHeight;
            }
        }

        /* Height is not supported */
        throw new GordianDataException("Inavlid height: " + pHeight);
    }
}
