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

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

/**
 * Additional AsymmetricKeys that are missing from PrivateKeyInfoFactory.
 */
public final class PqcPrivateKeyInfoFactory {
    /**
     * Private constructor.
     */
    private PqcPrivateKeyInfoFactory() {
    }

    /**
     * Create a PrivateKeyInfo representation of a private key with attributes.
     *
     * @param privateKey the key to be encoded into the info object.
     * @param attributes the set of attributes to be included.
     * @return the appropriate PrivateKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static PrivateKeyInfo createPrivateKeyInfo(final AsymmetricKeyParameter privateKey,
                                                      final ASN1Set attributes) throws IOException {
        if (privateKey instanceof McEliecePrivateKeyParameters) {
            final McEliecePrivateKeyParameters priv = (McEliecePrivateKeyParameters) privateKey;

            final McEliecePrivateKey privKey = new McEliecePrivateKey(priv.getN(), priv.getK(), priv.getField(), priv.getGoppaPoly(), priv.getP1(), priv.getP2(), priv.getSInv());
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
            return new PrivateKeyInfo(algorithmIdentifier, privKey);

        } else if (privateKey instanceof McElieceCCA2PrivateKeyParameters) {
            final McElieceCCA2PrivateKeyParameters priv = (McElieceCCA2PrivateKeyParameters) privateKey;

            final McElieceCCA2PrivateKey privKey = new McElieceCCA2PrivateKey(priv.getN(), priv.getK(), priv.getField(), priv.getGoppaPoly(), priv.getP(), getDigAlgId(priv.getDigest()));
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
            return new PrivateKeyInfo(algorithmIdentifier, privKey);

        } else if (privateKey instanceof RainbowPrivateKeyParameters) {
            final RainbowPrivateKeyParameters priv = (RainbowPrivateKeyParameters) privateKey;
            final RainbowPrivateKey key = new RainbowPrivateKey(priv.getInvA1(), priv.getB1(), priv.getInvA2(), priv.getB2(), priv.getVi(), priv.getLayers());

            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE);
            return new PrivateKeyInfo(algorithmIdentifier, key);

        } else {
            throw new IOException("key parameters not recognised.");
        }
    }

    static AlgorithmIdentifier getDigAlgId(final String digestName) {
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA1)) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA224)) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA256)) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA384)) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA512)) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE);
        }

        throw new IllegalArgumentException("unrecognised digest algorithm: " + digestName);
    }
}
