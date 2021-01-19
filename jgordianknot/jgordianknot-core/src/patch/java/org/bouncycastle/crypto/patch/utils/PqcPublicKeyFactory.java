/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package org.bouncycastle.crypto.patch.utils;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;

/**
 * Additional AsymmetricKeys that are missing from PublicKeyFactory.
 */
public final class PqcPublicKeyFactory {
    /**
     * Private constructor.
     */
    private PqcPublicKeyFactory() {
    }

    /**
     * Create a public key from the passed in SubjectPublicKeyInfo.
     *
     * @param keyInfo the SubjectPublicKeyInfo containing the key data
     * @return the appropriate key parameter
     * @throws IOException on an error decoding the key
     */
    public static AsymmetricKeyParameter createKey(final SubjectPublicKeyInfo keyInfo)
            throws IOException {
        final AlgorithmIdentifier algId = keyInfo.getAlgorithm();
        final ASN1ObjectIdentifier algOID = algId.getAlgorithm();

        if (algOID.equals(PQCObjectIdentifiers.mcEliece)) {
            final McEliecePublicKey key = McEliecePublicKey.getInstance(keyInfo.parsePublicKey());
            return new McEliecePublicKeyParameters(key.getN(), key.getT(), key.getG());

        } else if (algOID.equals(PQCObjectIdentifiers.mcElieceCca2)) {
            final McElieceCCA2PublicKey myKey = McElieceCCA2PublicKey.getInstance(keyInfo.parsePublicKey());
            return new McElieceCCA2PublicKeyParameters(myKey.getN(), myKey.getT(), myKey.getG(), PqcPrivateKeyFactory.getDigest(myKey.getDigest().getAlgorithm()).getAlgorithmName());

        } else if (algOID.equals(PQCObjectIdentifiers.rainbow)) {
            final RainbowPublicKey key = RainbowPublicKey.getInstance(keyInfo.parsePublicKey());
            return new RainbowPublicKeyParameters(key.getDocLength(), key.getCoeffQuadratic(), key.getCoeffSingular(), key.getCoeffScalar());

        } else {
            throw new RuntimeException("algorithm identifier in public key not recognised");
        }
    }
}
