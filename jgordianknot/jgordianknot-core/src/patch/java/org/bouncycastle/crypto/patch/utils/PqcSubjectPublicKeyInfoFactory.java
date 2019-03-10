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
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.patch.utils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

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
    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter publicKey) throws IOException
    {
        if (publicKey instanceof McEliecePublicKeyParameters)
        {
            McEliecePublicKeyParameters pub = (McEliecePublicKeyParameters)publicKey;

            McEliecePublicKey key = new McEliecePublicKey(pub.getN(), pub.getT(), pub.getG());
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcEliece);
            return new SubjectPublicKeyInfo(algorithmIdentifier, key);
        }
        else if (publicKey instanceof McElieceCCA2PublicKeyParameters)
        {
            McElieceCCA2PublicKeyParameters pub = (McElieceCCA2PublicKeyParameters)publicKey;

            McElieceCCA2PublicKey key = new McElieceCCA2PublicKey(pub.getN(), pub.getT(), pub.getG(), PqcPrivateKeyInfoFactory.getDigAlgId(pub.getDigest()));
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.mcElieceCca2);
            return new SubjectPublicKeyInfo(algorithmIdentifier, key);
        }
        else if (publicKey instanceof NHPublicKeyParameters)
        {
            NHPublicKeyParameters pub = (NHPublicKeyParameters)publicKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            return new SubjectPublicKeyInfo(algorithmIdentifier, pub.getPubData());
        }
        else if (publicKey instanceof QTESLAPublicKeyParameters)
        {
            QTESLAPublicKeyParameters pub = (QTESLAPublicKeyParameters)publicKey;

            AlgorithmIdentifier algorithmIdentifier = PqcPrivateKeyInfoFactory.lookupQTESLAAlgID(pub.getSecurityCategory());
            return new SubjectPublicKeyInfo(algorithmIdentifier, pub.getPublicData());
        }
        else if (publicKey instanceof RainbowPublicKeyParameters)
        {
            RainbowPublicKeyParameters pub = (RainbowPublicKeyParameters)publicKey;

            RainbowPublicKey key = new RainbowPublicKey(pub.getDocLength(), pub.getCoeffQuadratic(), pub.getCoeffSingular(), pub.getCoeffScalar());
            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.rainbow, DERNull.INSTANCE);

            return new SubjectPublicKeyInfo(algorithmIdentifier, key);
        }
        else
        {
            throw new IOException("key parameters not recognised.");
        }
    }

    /**
     * Create a SubjectPublicKeyInfo representation of a public key.
     *
     * @param publicKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate SubjectPublicKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createSPHINCSPublicKeyInfo(SPHINCSPublicKeyParameters publicKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(new AlgorithmIdentifier(treeDigest)));
        return new SubjectPublicKeyInfo(algorithmIdentifier, publicKey.getKeyData());
    }

    /**
     * Create a SubjectPublicKeyInfo representation of a public key.
     *
     * @param publicKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate SubjectPublicKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createXMSSPublicKeyInfo(XMSSPublicKeyParameters publicKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss, new XMSSKeyParams(publicKey.getParameters().getHeight(), new AlgorithmIdentifier(treeDigest)));
        return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSPublicKey(publicKey.getPublicSeed(), publicKey.getRoot()));
    }

    /**
     * Create a SubjectPublicKeyInfo representation of a public key.
     *
     * @param publicKey the key to be encoded into the info object.
     * @param treeDigest the treeDigest id.
     * @return the appropriate SubjectPublicKeyInfo
     * @throws java.io.IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createXMSSMTPublicKeyInfo(XMSSMTPublicKeyParameters publicKey, ASN1ObjectIdentifier treeDigest) throws IOException
    {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt,
                new XMSSMTKeyParams(publicKey.getParameters().getHeight(), publicKey.getParameters().getLayers(), new AlgorithmIdentifier(treeDigest)));
        return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSPublicKey(publicKey.getPublicSeed(), publicKey.getRoot()));
    }
}
