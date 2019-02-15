package org.bouncycastle.crypto.newutils;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

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
     * Create a public key from the passed in SubjectPublicKeyInfo
     *
     * @param keyInfo the SubjectPublicKeyInfo containing the key data
     * @return the appropriate key parameter
     * @throws IOException on an error decoding the key
     */
    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo keyInfo)
            throws IOException
    {
        AlgorithmIdentifier algId = keyInfo.getAlgorithm();
        ASN1ObjectIdentifier algOID = algId.getAlgorithm();

        if (algOID.equals(PQCObjectIdentifiers.mcEliece))
        {
            McEliecePublicKey key = McEliecePublicKey.getInstance(keyInfo.parsePublicKey());
            return new McEliecePublicKeyParameters(key.getN(), key.getT(), key.getG());
        }
        else if (algOID.equals(PQCObjectIdentifiers.mcElieceCca2))
        {
            McElieceCCA2PublicKey myKey = McElieceCCA2PublicKey.getInstance(keyInfo.parsePublicKey());
            return new McElieceCCA2PublicKeyParameters(myKey.getN(), myKey.getT(), myKey.getG(), PqcPrivateKeyFactory.getDigest(myKey.getDigest().getAlgorithm()).getAlgorithmName());
        }
        else if (algOID.equals(PQCObjectIdentifiers.newHope))
        {
            return new NHPublicKeyParameters(keyInfo.getPublicKeyData().getBytes());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_I))
        {
            return new QTESLAPublicKeyParameters(QTESLASecurityCategory.HEURISTIC_I, keyInfo.getPublicKeyData().getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_III_size))
        {
            return new QTESLAPublicKeyParameters(QTESLASecurityCategory.HEURISTIC_III_SIZE, keyInfo.getPublicKeyData().getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_III_speed))
        {
            return new QTESLAPublicKeyParameters(QTESLASecurityCategory.HEURISTIC_III_SPEED, keyInfo.getPublicKeyData().getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_p_I))
        {
            return new QTESLAPublicKeyParameters(QTESLASecurityCategory.PROVABLY_SECURE_I, keyInfo.getPublicKeyData().getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.qTESLA_p_III))
        {
            return new QTESLAPublicKeyParameters(QTESLASecurityCategory.PROVABLY_SECURE_III, keyInfo.getPublicKeyData().getOctets());
        }
        else if (algOID.equals(PQCObjectIdentifiers.rainbow))
        {
            RainbowPublicKey key = RainbowPublicKey.getInstance(keyInfo.parsePublicKey());
            return new RainbowPublicKeyParameters(key.getDocLength(), key.getCoeffQuadratic(), key.getCoeffSingular(), key.getCoeffScalar());
        }
        else if (algOID.equals(PQCObjectIdentifiers.sphincs256))
        {
            return new SPHINCSPublicKeyParameters(keyInfo.getPublicKeyData().getBytes());
        }
        else if (algOID.equals(PQCObjectIdentifiers.xmss))
        {
            XMSSKeyParams keyParams = XMSSKeyParams.getInstance(keyInfo.getAlgorithm().getParameters());
            ASN1ObjectIdentifier treeDigest = keyParams.getTreeDigest().getAlgorithm();

            XMSSPublicKey xmssPublicKey = XMSSPublicKey.getInstance(keyInfo.parsePublicKey());

            return new XMSSPublicKeyParameters
                    .Builder(new XMSSParameters(keyParams.getHeight(), PqcPrivateKeyFactory.getDigest(treeDigest)))
                    .withPublicSeed(xmssPublicKey.getPublicSeed())
                    .withRoot(xmssPublicKey.getRoot()).build();
        }
        else if (algOID.equals(PQCObjectIdentifiers.xmss_mt))
        {
            XMSSMTKeyParams keyParams = XMSSMTKeyParams.getInstance(keyInfo.getAlgorithm().getParameters());
            ASN1ObjectIdentifier treeDigest = keyParams.getTreeDigest().getAlgorithm();

            XMSSPublicKey xmssMtPublicKey = XMSSPublicKey.getInstance(keyInfo.parsePublicKey());

            return new XMSSMTPublicKeyParameters
                    .Builder(new XMSSMTParameters(keyParams.getHeight(), keyParams.getLayers(), PqcPrivateKeyFactory.getDigest(treeDigest)))
                    .withPublicSeed(xmssMtPublicKey.getPublicSeed())
                    .withRoot(xmssMtPublicKey.getRoot()).build();
        }
        else
        {
            throw new RuntimeException("algorithm identifier in public key not recognised");
        }
    }
}
