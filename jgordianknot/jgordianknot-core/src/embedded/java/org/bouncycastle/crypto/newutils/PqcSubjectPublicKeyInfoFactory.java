package org.bouncycastle.crypto.newutils;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
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
}
