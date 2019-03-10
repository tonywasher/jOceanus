/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import org.bouncycastle.asn1.ASN1OctetString;
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
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Pack;

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
        else if (algOID.equals(PQCObjectIdentifiers.newHope))
        {
            return new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets()));
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_I))
        {
            ASN1OctetString qTESLAPriv = ASN1OctetString.getInstance(keyInfo.parsePrivateKey());

            return new QTESLAPrivateKeyParameters(QTESLASecurityCategory.HEURISTIC_I, qTESLAPriv.getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_III_size))
        {
            ASN1OctetString qTESLAPriv = ASN1OctetString.getInstance(keyInfo.parsePrivateKey());

            return new QTESLAPrivateKeyParameters(QTESLASecurityCategory.HEURISTIC_III_SIZE, qTESLAPriv.getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_III_speed))
        {
            ASN1OctetString qTESLAPriv = ASN1OctetString.getInstance(keyInfo.parsePrivateKey());

            return new QTESLAPrivateKeyParameters(QTESLASecurityCategory.HEURISTIC_III_SPEED, qTESLAPriv.getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_p_I))
        {
            ASN1OctetString qTESLAPriv = ASN1OctetString.getInstance(keyInfo.parsePrivateKey());

            return new QTESLAPrivateKeyParameters(QTESLASecurityCategory.PROVABLY_SECURE_I, qTESLAPriv.getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_p_III))
        {
            ASN1OctetString qTESLAPriv = ASN1OctetString.getInstance(keyInfo.parsePrivateKey());

            return new QTESLAPrivateKeyParameters(QTESLASecurityCategory.PROVABLY_SECURE_III, qTESLAPriv.getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.rainbow))
        {
            RainbowPrivateKey keyStructure = RainbowPrivateKey.getInstance(keyInfo.parsePrivateKey());

            return new RainbowPrivateKeyParameters(keyStructure.getInvA1(), keyStructure.getB1(),
                    keyStructure.getInvA2(), keyStructure.getB2(), keyStructure.getVi(), keyStructure.getLayers());
        }
        else if (algOID.equals(PQCObjectIdentifiers.sphincs256))
        {
            return new SPHINCSPrivateKeyParameters(ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.xmss))
        {
            XMSSKeyParams keyParams = XMSSKeyParams.getInstance(keyInfo.getPrivateKeyAlgorithm().getParameters());
            ASN1ObjectIdentifier treeDigest = keyParams.getTreeDigest().getAlgorithm();
            XMSSPrivateKey xmssPrivateKey = XMSSPrivateKey.getInstance(keyInfo.parsePrivateKey());
            try
            {
                XMSSPrivateKeyParameters.Builder keyBuilder = new XMSSPrivateKeyParameters
                        .Builder(new XMSSParameters(keyParams.getHeight(), getDigest(treeDigest)))
                        .withIndex(xmssPrivateKey.getIndex())
                        .withSecretKeySeed(xmssPrivateKey.getSecretKeySeed())
                        .withSecretKeyPRF(xmssPrivateKey.getSecretKeyPRF())
                        .withPublicSeed(xmssPrivateKey.getPublicSeed())
                        .withRoot(xmssPrivateKey.getRoot());

                if (xmssPrivateKey.getBdsState() != null)
                {
                    BDS bds = (BDS) XMSSUtil.deserialize(xmssPrivateKey.getBdsState(), BDS.class);
                    keyBuilder.withBDSState(bds.withWOTSDigest(treeDigest));
                }

                return keyBuilder.build();
            }
            catch (ClassNotFoundException e)
            {
                throw new IOException("ClassNotFoundException processing BDS state: " + e.getMessage());
            }
        }
        else if (algOID.equals(PQCObjectIdentifiers.xmss_mt))
        {
            XMSSMTKeyParams keyParams = XMSSMTKeyParams.getInstance(keyInfo.getPrivateKeyAlgorithm().getParameters());
            ASN1ObjectIdentifier treeDigest = keyParams.getTreeDigest().getAlgorithm();

            XMSSPrivateKey xmssMtPrivateKey = XMSSPrivateKey.getInstance(keyInfo.parsePrivateKey());

            try
            {
                XMSSMTPrivateKeyParameters.Builder keyBuilder = new XMSSMTPrivateKeyParameters
                        .Builder(new XMSSMTParameters(keyParams.getHeight(), keyParams.getLayers(), getDigest(treeDigest)))
                        .withIndex(xmssMtPrivateKey.getIndex())
                        .withSecretKeySeed(xmssMtPrivateKey.getSecretKeySeed())
                        .withSecretKeyPRF(xmssMtPrivateKey.getSecretKeyPRF())
                        .withPublicSeed(xmssMtPrivateKey.getPublicSeed())
                        .withRoot(xmssMtPrivateKey.getRoot());

                if (xmssMtPrivateKey.getBdsState() != null)
                {
                    BDSStateMap bdsState = (BDSStateMap)XMSSUtil.deserialize(xmssMtPrivateKey.getBdsState(), BDSStateMap.class);
                    keyBuilder.withBDSState(bdsState.withWOTSDigest(treeDigest));
                }

                return keyBuilder.build();
            }
            catch (ClassNotFoundException e)
            {
                throw new IOException("ClassNotFoundException processing BDS state: " + e.getMessage());
            }
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

    private static short[] convert(byte[] octets)
    {
        short[] rv = new short[octets.length / 2];

        for (int i = 0; i != rv.length; i++)
        {
            rv[i] = Pack.littleEndianToShort(octets, i * 2);
        }

        return rv;
    }
}
