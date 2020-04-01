/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;

/**
 * Additional AsymmetricKeys that are missing from PrivateKeyFactory.
 */
public final class PqcPrivateKeyFactory {
    /**
     * Private constructor.
     */
    private PqcPrivateKeyFactory() {
    }

    /**
     * Create a private key parameter from the passed in PKCS8 PrivateKeyInfo object.
     *
     * @param keyInfo the PrivateKeyInfo object containing the key material
     * @return a suitable private key parameter
     * @throws IOException on an error decoding the key
     */
    public static AsymmetricKeyParameter createKey(PrivateKeyInfo keyInfo) throws IOException
    {
        AlgorithmIdentifier algId = keyInfo.getPrivateKeyAlgorithm();
        ASN1ObjectIdentifier algOID = algId.getAlgorithm();

        if (algOID.equals(PQCObjectIdentifiers.mcEliece))
        {
            McEliecePrivateKey myKey = McEliecePrivateKey.getInstance(keyInfo.parsePrivateKey());

            return new McEliecePrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                            myKey.getP1(), myKey.getP2(), myKey.getSInv());
        }
        else if (algOID.equals(PQCObjectIdentifiers.mcElieceCca2))
        {
            McElieceCCA2PrivateKey myKey = McElieceCCA2PrivateKey.getInstance(keyInfo.parsePrivateKey());

            return new McElieceCCA2PrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                            myKey.getP(), getDigest(myKey.getDigest().getAlgorithm()).getAlgorithmName());
        }
        else if (algOID.equals(PQCObjectIdentifiers.rainbow))
        {
            RainbowPrivateKey keyStructure = RainbowPrivateKey.getInstance(keyInfo.parsePrivateKey());

            return new RainbowPrivateKeyParameters(keyStructure.getInvA1(), keyStructure.getB1(),
                    keyStructure.getInvA2(), keyStructure.getB2(), keyStructure.getVi(), keyStructure.getLayers());
        }
        else
        {
            throw new RuntimeException("algorithm identifier in private key not recognised");
        }
    }

    static Digest getDigest(ASN1ObjectIdentifier oid)
    {
        if (oid.equals(OIWObjectIdentifiers.idSHA1))
        {
            return new SHA1Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_sha224))
        {
            return new SHA224Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_sha256))
        {
            return new SHA256Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_sha384))
        {
            return new SHA384Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_sha512))
        {
            return new SHA512Digest();
        }
        if (oid.equals(NISTObjectIdentifiers.id_shake128))
        {
            return new SHAKEDigest(128);
        }
        if (oid.equals(NISTObjectIdentifiers.id_shake256))
        {
            return new SHAKEDigest(256);
        }

        throw new IllegalArgumentException("unrecognized digest OID: " + oid);
    }
}
