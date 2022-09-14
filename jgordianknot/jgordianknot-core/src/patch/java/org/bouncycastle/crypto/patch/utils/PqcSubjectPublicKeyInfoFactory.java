/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.legacy.crypto.rainbow.RainbowPublicKeyParameters;

/**
 * Additional AsymmetricKeys that are missing from SublicPublicKeyInfoFactory.
 */
public final class PqcSubjectPublicKeyInfoFactory {
    /**
     * Private constructor.
     */
    private PqcSubjectPublicKeyInfoFactory() {
    }

    /**
     * Create a SubjectPublicKeyInfo public key.
     *
     * @param publicKey the key to be encoded into the info object.
     * @return a SubjectPublicKeyInfo representing the key.
     * @throws java.io.IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(final AsymmetricKeyParameter publicKey) throws IOException {
        if (publicKey instanceof McEliecePublicKeyParameters) {
            final McEliecePublicKeyParameters pub = (McEliecePublicKeyParameters) publicKey;

            final McEliecePublicKey key = new McEliecePublicKey(pub.getN(), pub.getT(), pub.getG());
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
            return new SubjectPublicKeyInfo(algorithmIdentifier, key);

        } else if (publicKey instanceof McElieceCCA2PublicKeyParameters) {
            final McElieceCCA2PublicKeyParameters pub = (McElieceCCA2PublicKeyParameters) publicKey;

            final McElieceCCA2PublicKey key = new McElieceCCA2PublicKey(pub.getN(), pub.getT(), pub.getG(), PqcPrivateKeyInfoFactory.getDigAlgId(pub.getDigest()));
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
            return new SubjectPublicKeyInfo(algorithmIdentifier, key);

        } else if (publicKey instanceof RainbowPublicKeyParameters) {
            final RainbowPublicKeyParameters pub = (RainbowPublicKeyParameters) publicKey;

            final RainbowPublicKey key = new RainbowPublicKey(pub.getDocLength(), pub.getCoeffQuadratic(), pub.getCoeffSingular(), pub.getCoeffScalar());
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE);

            return new SubjectPublicKeyInfo(algorithmIdentifier, key);

        } else {
            throw new IOException("key parameters not recognised.");
        }
    }
}
