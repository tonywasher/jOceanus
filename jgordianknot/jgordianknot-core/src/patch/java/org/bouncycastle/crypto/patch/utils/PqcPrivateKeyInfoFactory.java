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
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.util.Pack;

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
    public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter privateKey, ASN1Set attributes) throws IOException
    {
        if (privateKey instanceof McEliecePrivateKeyParameters)
        {
            McEliecePrivateKeyParameters priv = (McEliecePrivateKeyParameters) privateKey;

            McEliecePrivateKey privKey = new McEliecePrivateKey(priv.getN(), priv.getK(), priv.getField(), priv.getGoppaPoly(), priv.getP1(), priv.getP2(), priv.getSInv());
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
            return new PrivateKeyInfo(algorithmIdentifier, privKey);
        }
        else if (privateKey instanceof McElieceCCA2PrivateKeyParameters)
        {
            McElieceCCA2PrivateKeyParameters priv = (McElieceCCA2PrivateKeyParameters) privateKey;

            McElieceCCA2PrivateKey privKey = new McElieceCCA2PrivateKey(priv.getN(), priv.getK(), priv.getField(), priv.getGoppaPoly(), priv.getP(), getDigAlgId(priv.getDigest()));
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
            return new PrivateKeyInfo(algorithmIdentifier, privKey);
        }
        else if (privateKey instanceof NHPrivateKeyParameters)
        {
            NHPrivateKeyParameters priv = (NHPrivateKeyParameters) privateKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            short[] privateKeyData = priv.getSecData();

            byte[] octets = new byte[privateKeyData.length * 2];
            for (int i = 0; i != privateKeyData.length; i++)
            {
                Pack.shortToLittleEndian(privateKeyData[i], octets, i * 2);
            }

            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(octets));
        }
        else if (privateKey instanceof QTESLAPrivateKeyParameters)
        {
            QTESLAPrivateKeyParameters priv = (QTESLAPrivateKeyParameters) privateKey;

            AlgorithmIdentifier algorithmIdentifier = lookupQTESLAAlgID(priv.getSecurityCategory());
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(priv.getSecret()));
        }
        else if (privateKey instanceof RainbowPrivateKeyParameters)
        {
            RainbowPrivateKeyParameters priv = (RainbowPrivateKeyParameters) privateKey;
            RainbowPrivateKey key = new RainbowPrivateKey(priv.getInvA1(), priv.getB1(), priv.getInvA2(), priv.getB2(), priv.getVi(), priv.getLayers());

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE);
            return new PrivateKeyInfo(algorithmIdentifier, key);
        }
        else
        {
            throw new IOException("key parameters not recognised.");
        }
    }

    /**
     * Create a PrivateKeyInfo representation of a private key.
     *
     * @param privateKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate PrivateKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static PrivateKeyInfo createSPHINCSPrivateKeyInfo(SPHINCSPrivateKeyParameters privateKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(treeDigest)));
        return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(privateKey.getKeyData()));
    }

    /**
     * Create a PrivateKeyInfo representation of a private key.
     *
     * @param privateKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate PrivateKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static PrivateKeyInfo createXMSSPrivateKeyInfo(XMSSPrivateKeyParameters privateKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss,
                new XMSSKeyParams(privateKey.getParameters().getHeight(), new AlgorithmIdentifier(treeDigest)));
        return new PrivateKeyInfo(algorithmIdentifier, createXMSSKeyStructure(privateKey));
    }

    /**
     * Create a PrivateKeyInfo representation of a private key.
     *
     * @param privateKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate PrivateKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static PrivateKeyInfo createXMSSMTPrivateKeyInfo(XMSSMTPrivateKeyParameters privateKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt,
                new XMSSMTKeyParams(privateKey.getParameters().getHeight(), privateKey.getParameters().getLayers(), new AlgorithmIdentifier(treeDigest)));
        return new PrivateKeyInfo(algorithmIdentifier, createXMSSMTKeyStructure(privateKey));
    }

    static AlgorithmIdentifier lookupQTESLAAlgID(int securityCategory)
    {
        switch (securityCategory)
        {
            case QTESLASecurityCategory.HEURISTIC_I:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_I);
            case QTESLASecurityCategory.HEURISTIC_II:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_II);
            case QTESLASecurityCategory.HEURISTIC_III:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_III);
            case QTESLASecurityCategory.HEURISTIC_P_I:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_I);
            case QTESLASecurityCategory.HEURISTIC_P_III:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_III);
            case QTESLASecurityCategory.HEURISTIC_V:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_V);
            case QTESLASecurityCategory.HEURISTIC_V_SIZE:
                return new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_V_SIZE);
            default:
                throw new IllegalArgumentException("unknown security category: " + securityCategory);
        }
    }

    private static XMSSPrivateKey createXMSSKeyStructure(XMSSPrivateKeyParameters privateKey)
    {
        byte[] keyData = privateKey.toByteArray();

        int n = privateKey.getParameters().getDigestSize();
        int totalHeight = privateKey.getParameters().getHeight();
        int indexSize = 4;
        int secretKeySize = n;
        int secretKeyPRFSize = n;
        int publicSeedSize = n;
        int rootSize = n;

        int position = 0;
        int index = (int) XMSSUtil.bytesToXBigEndian(keyData, position, indexSize);
        if (!XMSSUtil.isIndexValid(totalHeight, index))
        {
            throw new IllegalArgumentException("index out of bounds");
        }
        position += indexSize;
        byte[] secretKeySeed = XMSSUtil.extractBytesAtOffset(keyData, position, secretKeySize);
        position += secretKeySize;
        byte[] secretKeyPRF = XMSSUtil.extractBytesAtOffset(keyData, position, secretKeyPRFSize);
        position += secretKeyPRFSize;
        byte[] publicSeed = XMSSUtil.extractBytesAtOffset(keyData, position, publicSeedSize);
        position += publicSeedSize;
        byte[] root = XMSSUtil.extractBytesAtOffset(keyData, position, rootSize);
        position += rootSize;
        /* import BDS state */
        byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(keyData, position, keyData.length - position);

        return new XMSSPrivateKey(index, secretKeySeed, secretKeyPRF, publicSeed, root, bdsStateBinary);
    }

    private static XMSSMTPrivateKey createXMSSMTKeyStructure(XMSSMTPrivateKeyParameters privateKey)
    {
        byte[] keyData = privateKey.toByteArray();

        int n = privateKey.getParameters().getDigestSize();
        int totalHeight = privateKey.getParameters().getHeight();
        int indexSize = (totalHeight + 7) / 8;
        int secretKeySize = n;
        int secretKeyPRFSize = n;
        int publicSeedSize = n;
        int rootSize = n;

        int position = 0;
        int index = (int)XMSSUtil.bytesToXBigEndian(keyData, position, indexSize);
        if (!XMSSUtil.isIndexValid(totalHeight, index))
        {
            throw new IllegalArgumentException("index out of bounds");
        }
        position += indexSize;
        byte[] secretKeySeed = XMSSUtil.extractBytesAtOffset(keyData, position, secretKeySize);
        position += secretKeySize;
        byte[] secretKeyPRF = XMSSUtil.extractBytesAtOffset(keyData, position, secretKeyPRFSize);
        position += secretKeyPRFSize;
        byte[] publicSeed = XMSSUtil.extractBytesAtOffset(keyData, position, publicSeedSize);
        position += publicSeedSize;
        byte[] root = XMSSUtil.extractBytesAtOffset(keyData, position, rootSize);
        position += rootSize;
        /* import BDS state */
        byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(keyData, position, keyData.length - position);

        return new XMSSMTPrivateKey(index, secretKeySeed, secretKeyPRF, publicSeed, root, bdsStateBinary);
    }

    static AlgorithmIdentifier getDigAlgId(String digestName)
    {
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA1))
        {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA224))
        {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA256))
        {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA384))
        {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE);
        }
        if (digestName.equals(McElieceCCA2KeyGenParameterSpec.SHA512))
        {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE);
        }

        throw new IllegalArgumentException("unrecognised digest algorithm: " + digestName);
    }
}
