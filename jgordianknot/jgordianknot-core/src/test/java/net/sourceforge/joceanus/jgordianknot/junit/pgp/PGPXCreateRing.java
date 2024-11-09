package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyPacket;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * PGP Create keyRings.
 */
public class PGPXCreateRing {
    private static final int PRIME_CERTAINTY = 128;
    private static final int RSA_MODULUS = 3072;
    private static final int DSA_MODULUS = 2048;
    private static final int DSA_HASHLEN = 256;
    private static final BigInteger RSA_EXPONENT = new BigInteger("10001", 16);
    private static final String ECCURVE = "secp256r1";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long KEYLIFETIME = 86400L * 366 * 2; /* Two Years */

    /**
     * Main program.
     *
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Loop through the types */
            for (PGPXKeyRing myType : PGPXKeyRing.values()) {
                /* Create all non-GPG keyPairs */
                if (!myType.isStyle(PGPXKeyRingStyle.GPG)) {
                    createKeyRing(myType);
                }
            }

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create keyRing.
     *
     * @param pType the keyRingType
     */
    public static void createKeyRing(final PGPXKeyRing pType) throws IOException, PGPException {
        /* Generate the keyPairs */
        KeyPairSet keyPairs = generateKeyPairs(pType);

        /* Generate SubPackets for signing key */
        final PGPXKeyRingStyle myStyle = pType.getKeyPairStyle();
        PGPSignatureSubpacketVector unhashedPcks = null;
        PGPSignatureSubpacketVector hashedPcks = createMasterPackets(keyPairs.theCertify.getPublicKey(), myStyle, true);

        /* Create the signer builder */
        final PGPPublicKey myPublic = keyPairs.theCertify.getPublicKey();
        final BcPGPContentSignerBuilder mySignerBuilder = new BcPGPContentSignerBuilder(myPublic.getAlgorithm(),
                HashAlgorithmTags.SHA256);
        final int myAlg = myPublic.getAlgorithm();
        if (myAlg != PGPPublicKey.Ed25519 && myAlg != PGPPublicKey.Ed448) {
            mySignerBuilder.setSecureRandom(RANDOM);
        }

        /* Create the initial keyRing */
        final PGPDigestCalculator sha1Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
        final char[] password = pType.obtainPassword4Secret().toCharArray();
        final PBESecretKeyEncryptor myEncryptor = new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256).build(password);
        final String identity = pType.obtainIdentity();
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, keyPairs.theCertify,
                identity, sha1Calc, hashedPcks, unhashedPcks, mySignerBuilder, myEncryptor);

        /* Add the encryptionKeyPair */
        if (myStyle != PGPXKeyRingStyle.SOLO) {
            hashedPcks = createEncryptorSubKeyPackets(keyPairs.theCertify.getPublicKey());
            keyRingGen.addSubKey(keyPairs.theEncrypting, hashedPcks, unhashedPcks);
        }

        /* Add the signingKeyPair */
        if (myStyle == PGPXKeyRingStyle.TRIO) {
            hashedPcks = createSignerSubKeyPackets(keyPairs.theCertify.getPublicKey(), keyPairs.theSigning, mySignerBuilder);
            keyRingGen.addSubKey(keyPairs.theSigning, hashedPcks, unhashedPcks);
        }

        /* Write the keyRings */
        PGPSecretKeyRing secretKeyRing = keyRingGen.generateSecretKeyRing();
        secretKeyRing = addAltUserId(pType, keyPairs.theCertify,
                mySignerBuilder, myEncryptor, sha1Calc, secretKeyRing);
        writeSecret(secretKeyRing, pType);
        writePublic(secretKeyRing, pType);
    }

    /**
     * Add userId to secretKey.
     * @param pType the keyRingType
     * @param pSigner the signing keyPair
     * @param pBuilder the signer builder
     * @param pEncryptor the encryptor
     * @param pCalc the digest calculator
     * @param pRing the existing secretKeyRing
     * @return the new secretKeyRing
     */
    private static PGPSecretKeyRing addAltUserId(final PGPXKeyRing pType,
                                                 final PGPKeyPair pSigner,
                                                 final BcPGPContentSignerBuilder pBuilder,
                                                 final PBESecretKeyEncryptor pEncryptor,
                                                 final PGPDigestCalculator pCalc,
                                                 final PGPSecretKeyRing pRing) throws PGPException {
        /* Build the new secretKey */
        final String myUserId = pType.obtainAltIdentity();
        final PGPSignatureGenerator mySigner = new PGPSignatureGenerator(pBuilder, pRing.getPublicKey());
        mySigner.init(PGPSignature.POSITIVE_CERTIFICATION, pSigner.getPrivateKey());
        final PGPSignatureSubpacketGenerator svg = new PGPSignatureSubpacketGenerator();
        final PGPSignatureSubpacketVector hashedPcks = createMasterPackets(pSigner.getPublicKey(), pType.getKeyPairStyle(), false);
        mySigner.setHashedSubpackets(hashedPcks);
        final PGPSignature additionalUserIdSignature = mySigner.generateCertification(myUserId, pSigner.getPublicKey());
        final PGPPublicKey newPublic = PGPPublicKey.addCertification(pRing.getPublicKey(), myUserId, additionalUserIdSignature);
        final PGPSecretKey newSecret = new PGPSecretKey(pSigner.getPrivateKey(), newPublic, pCalc, true, pEncryptor);

        Iterator<PGPSecretKey> it = pRing.getSecretKeys();
        List<PGPSecretKey> secKeys = new ArrayList<>();
        secKeys.add(newSecret);
        it.next();
        while (it.hasNext()) {
            secKeys.add(it.next());
        }
        return new PGPSecretKeyRing(secKeys);
    }

    /**
     * Generate keyRingPairs.
     * @param pType the keyRing type
     * @return the keyPairs
     */
    public static KeyPairSet generateKeyPairs(final PGPXKeyRing pType) throws PGPException {
        switch (pType.getKeyPairType()) {
            case RSA: return generateRSAKeyPairs(pType);
            case DSAELGAMAL: return generateDSAKeyPairs(pType);
            case EC: return generateECKeyPairs(pType);
            case EDWARDS: return generateEdwardsKeyPairs(pType);
            default:
                throw new IllegalArgumentException("Invalid keyPair type: " + pType);
        }
    }

    /**
     * Generate RSA keyRingPairs.
     * @param pType the keyRing type
     * @return the keyPairs
     */
    private static KeyPairSet generateRSAKeyPairs(final PGPXKeyRing pType) throws PGPException {
        /* Create and configure the generator */
        final AsymmetricCipherKeyPairGenerator kpg = new RSAKeyPairGenerator();
        final RSAKeyGenerationParameters myParams
                = new RSAKeyGenerationParameters(RSA_EXPONENT, RANDOM, RSA_MODULUS, PRIME_CERTAINTY);
        kpg.init(myParams);

        /* Generate the keyPairs */
        final Date date = new Date();
        final PGPXKeyRingStyle myStyle = pType.getKeyPairStyle();
        final AsymmetricCipherKeyPair kpCert = kpg.generateKeyPair();
        final PGPKeyPair certKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.RSA_GENERAL, kpCert, date);
        if (myStyle != PGPXKeyRingStyle.SOLO) {
            final AsymmetricCipherKeyPair kpEnc = kpg.generateKeyPair();
            final PGPKeyPair encKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.RSA_GENERAL, kpEnc, date);
            if (myStyle == PGPXKeyRingStyle.TRIO) {
                final AsymmetricCipherKeyPair kpSgn = kpg.generateKeyPair();
                final PGPKeyPair sgnKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.RSA_GENERAL, kpSgn, date);
                return new KeyPairSet(certKeyPair, encKeyPair, sgnKeyPair);
            } else {
                return new KeyPairSet(certKeyPair, encKeyPair);
            }
        } else {
            return new KeyPairSet(certKeyPair);
        }
    }

    /**
     * Generate DSA/ElGamal keyRingPairs.
     * @param pType the keyRing type
     * @return the keyPairs
     */
    private static KeyPairSet generateDSAKeyPairs(final PGPXKeyRing pType) throws PGPException {
        /* Create and configure the DSA generator */
        final AsymmetricCipherKeyPairGenerator kpgSign = new DSAKeyPairGenerator();
        final DSAParameterGenerationParameters myGenParms = new DSAParameterGenerationParameters(DSA_MODULUS,
                DSA_HASHLEN, PRIME_CERTAINTY, RANDOM);
        final DSAParametersGenerator myParmGenerator = new DSAParametersGenerator(new SHA256Digest());
        myParmGenerator.init(myGenParms);
        final DSAKeyGenerationParameters mySignParams = new DSAKeyGenerationParameters(RANDOM, myParmGenerator.generateParameters());
        kpgSign.init(mySignParams);

        /* Create and configure the ElGamal generator */
        final AsymmetricCipherKeyPairGenerator kpgEnc = new ElGamalKeyPairGenerator();
        final DHParameters myDHParms = DHStandardGroups.rfc7919_ffdhe2048;
        final ElGamalParameters myParms = new ElGamalParameters(myDHParms.getP(), myDHParms.getQ());
        final ElGamalKeyGenerationParameters myEncParams = new ElGamalKeyGenerationParameters(RANDOM, myParms);
        kpgEnc.init(myEncParams);

        /* Generate the keyPairs */
        final Date date = new Date();
        final PGPXKeyRingStyle myStyle = pType.getKeyPairStyle();
        final AsymmetricCipherKeyPair kpCert = kpgSign.generateKeyPair();
        final AsymmetricCipherKeyPair kpEnc = kpgEnc.generateKeyPair();
        final PGPKeyPair certKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.DSA, kpCert, date);
        final PGPKeyPair encKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.ELGAMAL_ENCRYPT, kpEnc, date);
        if (myStyle == PGPXKeyRingStyle.TRIO) {
            final AsymmetricCipherKeyPair kpSgn = kpgSign.generateKeyPair();
            final PGPKeyPair sgnKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.DSA, kpSgn, date);
            return new KeyPairSet(certKeyPair, encKeyPair, sgnKeyPair);
        } else {
            return new KeyPairSet(certKeyPair, encKeyPair);
        }
    }

    /**
     * Generate EC keyRingPairs.
     * @param pType the keyRing type
     * @return the keyPairs
     */
    private static KeyPairSet generateECKeyPairs(final PGPXKeyRing pType) throws PGPException {
        /* Create and configure the generator */
        final AsymmetricCipherKeyPairGenerator kpg = new ECKeyPairGenerator();
        final X9ECParameters x9 = ECUtil.getNamedCurveByName(ECCURVE);
        final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(ECCURVE);
        final ECNamedDomainParameters myDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, RANDOM);
        kpg.init(myParams);

        /* Generate the keyPairs */
        final Date date = new Date();
        final PGPXKeyRingStyle myStyle = pType.getKeyPairStyle();
        final AsymmetricCipherKeyPair kpCert = kpg.generateKeyPair();
        final PGPKeyPair certKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.ECDSA, kpCert, date);
        if (myStyle != PGPXKeyRingStyle.SOLO) {
            final AsymmetricCipherKeyPair kpEnc = kpg.generateKeyPair();
            final PGPKeyPair encKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.ECDH, kpEnc, date);
            if (myStyle == PGPXKeyRingStyle.TRIO) {
                final AsymmetricCipherKeyPair kpSgn = kpg.generateKeyPair();
                final PGPKeyPair sgnKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.ECDSA, kpSgn, date);
                return new KeyPairSet(certKeyPair, encKeyPair, sgnKeyPair);
            } else {
                return new KeyPairSet(certKeyPair, encKeyPair);
            }
        } else {
            return new KeyPairSet(certKeyPair);
        }
    }

    /**
     * Generate Ed keyRingPairs.
     * @param pType the keyRing type
     * @return the keyPairs
     */
    private static KeyPairSet generateEdwardsKeyPairs(final PGPXKeyRing pType) throws PGPException {
        /* Create and configure the generator */
        final AsymmetricCipherKeyPairGenerator kpgSign = new Ed25519KeyPairGenerator();
        final Ed25519KeyGenerationParameters mySignParams = new Ed25519KeyGenerationParameters(RANDOM);
        kpgSign.init(mySignParams);
        final AsymmetricCipherKeyPairGenerator kpgEnc = new X25519KeyPairGenerator();
        final X25519KeyGenerationParameters myEncParams = new X25519KeyGenerationParameters(RANDOM);
        kpgEnc.init(myEncParams);

        /* Generate the keyPairs */
        final Date date = new Date();
        final PGPXKeyRingStyle myStyle = pType.getKeyPairStyle();
        final AsymmetricCipherKeyPair kpCert = kpgSign.generateKeyPair();
        final AsymmetricCipherKeyPair kpEnc = kpgEnc.generateKeyPair();
        final PGPKeyPair certKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.Ed448, kpCert, date);
        final PGPKeyPair encKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.ECDH, kpEnc, date);
        if (myStyle == PGPXKeyRingStyle.TRIO) {
            final AsymmetricCipherKeyPair kpSgn = kpgSign.generateKeyPair();
            final PGPKeyPair sgnKeyPair = new BcPGPKeyPair(PublicKeyPacket.VERSION_4, PGPPublicKey.Ed448, kpSgn, date);
            return new KeyPairSet(certKeyPair, encKeyPair, sgnKeyPair);
        } else {
            return new KeyPairSet(certKeyPair, encKeyPair);
        }
    }

    /**
     * Create masterHashedSubpackets.
     * @param pSigner the signing key
     * @param pStyle the Style
     * @param pPrimary the primary userId flag
     * @return the hashed subPackets
     */
    private static PGPSignatureSubpacketVector createMasterPackets(final PGPPublicKey pSigner,
                                                                   final PGPXKeyRingStyle pStyle,
                                                                   final boolean pPrimary) {
        /* Generate SubPackets for signing key */
        final PGPSignatureSubpacketGenerator svg = new PGPSignatureSubpacketGenerator();
        svg.setKeyExpirationTime(true, KEYLIFETIME);
        svg.setPrimaryUserID(true, true);
        final int[] encAlgs = {SymmetricKeyAlgorithmTags.AES_256,
                SymmetricKeyAlgorithmTags.AES_192,
                SymmetricKeyAlgorithmTags.AES_128};
        svg.setPreferredSymmetricAlgorithms(true, encAlgs);
        final int[] hashAlgs = {HashAlgorithmTags.SHA512,
                HashAlgorithmTags.SHA384,
                HashAlgorithmTags.SHA256,
                HashAlgorithmTags.SHA1};
        svg.setPreferredHashAlgorithms(true, hashAlgs);
        final int[] comprAlgs = {CompressionAlgorithmTags.ZLIB,
                CompressionAlgorithmTags.BZIP2,
                CompressionAlgorithmTags.ZIP};
        svg.setPreferredCompressionAlgorithms(true, comprAlgs);
        svg.setFeature(true, Features.FEATURE_MODIFICATION_DETECTION);
        svg.setKeyFlags(true, pStyle == PGPXKeyRingStyle.TRIO
                ? KeyFlags.CERTIFY_OTHER
                : KeyFlags.CERTIFY_OTHER +  KeyFlags.SIGN_DATA);
        svg.setIssuerFingerprint(false, pSigner);
        svg.setPrimaryUserID(false, pPrimary);
        return svg.generate();
    }

    /**
     * Create encryptor subKeyHashedSubpackets.
     * @param pSigner the signing public key
     * @return the hashed subPackets
     */
    public static PGPSignatureSubpacketVector createEncryptorSubKeyPackets(final PGPPublicKey pSigner) {
        /* Generate SubPackets for signing key */
        final PGPSignatureSubpacketGenerator svg = new PGPSignatureSubpacketGenerator();
        svg.setKeyExpirationTime(true, KEYLIFETIME);
        svg.setKeyFlags(true, KeyFlags.ENCRYPT_COMMS + KeyFlags.ENCRYPT_STORAGE);
        svg.setIssuerFingerprint(false, pSigner);
        return svg.generate();
    }

    /**
     * Create signer subKeyHashedSubpackets.
     * @param pMaster the masterg public key
     * @return the hashed subPackets
     */
    public static PGPSignatureSubpacketVector createSignerSubKeyPackets(final PGPPublicKey pMaster,
                                                                        final PGPKeyPair pSigner,
                                                                        final BcPGPContentSignerBuilder pBuilder) throws PGPException, IOException {
        /* Create embedded signature */
        final PGPSignatureGenerator mySigner = new PGPSignatureGenerator(pBuilder, pMaster);
        mySigner.init(PGPSignature.PRIMARYKEY_BINDING, pSigner.getPrivateKey());
        PGPSignatureSubpacketGenerator svg = new PGPSignatureSubpacketGenerator();
        svg.setIssuerFingerprint(false, pSigner.getPublicKey());
        mySigner.setHashedSubpackets(svg.generate());
        PGPSignature myBinding = mySigner.generateCertification(pMaster, pSigner.getPublicKey());

        /* Generate SubPackets for signing key */
        svg = new PGPSignatureSubpacketGenerator();
        svg.setKeyExpirationTime(true, KEYLIFETIME);
        svg.setKeyFlags(true, KeyFlags.SIGN_DATA);
        svg.setIssuerFingerprint(false, pMaster);
        svg.addEmbeddedSignature(false, myBinding);
        return svg.generate();
    }

    /**
     * Write the secretKeyRing.
     *
     * @param pSecretRing the secretKeyRing
     * @param pType the keyRingType
     */
    public static void writeSecret(final PGPSecretKeyRing pSecretRing,
                                   final PGPXKeyRing pType) throws IOException {
        /* Create the target file */
        try (OutputStream myOutput = new FileOutputStream(PGPXKeyRingUtil.FILEDIR + pType.obtainFilename() + PGPXKeyRingUtil.SECRET_SFX);
             BufferedOutputStream myBufferedOut = new BufferedOutputStream(myOutput);
             ArmoredOutputStream myArmoredOut = new ArmoredOutputStream(myBufferedOut)) {
            pSecretRing.encode(myArmoredOut);
        }
    }

    /**
     * Write the publicKeyRing.
     *
     * @param pSecretRing the secretKeyRing
     * @param pType the keyRingType
     */
    public static void writePublic(final PGPSecretKeyRing pSecretRing,
                                   final PGPXKeyRing pType) throws IOException, PGPException {
        /* Generate the ring from the secret keyRing */
        Iterator<PGPPublicKey> it = pSecretRing.getPublicKeys();
        List<PGPPublicKey> pubKeys = new ArrayList<>();
        while (it.hasNext()) {
            pubKeys.add(it.next());
        }
        final PGPPublicKeyRing myPublic = new PGPPublicKeyRing(pubKeys);

        /* Create the target file */
        try (OutputStream myOutput = new FileOutputStream(PGPXKeyRingUtil.FILEDIR + pType.obtainFilename() + PGPXKeyRingUtil.PUBLIC_SFX);
             BufferedOutputStream myBufferedOut = new BufferedOutputStream(myOutput);
             ArmoredOutputStream myArmoredOut = new ArmoredOutputStream(myBufferedOut)) {
            myPublic.encode(myArmoredOut);
        }
    }

    /**
     * KeyPairSet class.
     */
    private static class KeyPairSet {
        /**
         * The certifying keyPair.
         */
        PGPKeyPair theCertify;

        /**
         * The encrypting keyPair.
         */
        PGPKeyPair theEncrypting;

        /**
         * The signing keyPair.
         */
        PGPKeyPair theSigning;

        /**
         * The style.
         */
        PGPXKeyRingStyle theStyle;

        /**
         * Constructor.
         * @param pCertifier the certifier
         * @param pEncryptor the encryptor
         * @param pSigner the signer
         */
        KeyPairSet(final PGPKeyPair pCertifier,
                   final PGPKeyPair pEncryptor,
                   final PGPKeyPair pSigner) {
            theCertify = pCertifier;
            theEncrypting = pEncryptor;
            theSigning = pSigner;
            theStyle = PGPXKeyRingStyle.TRIO;
        }

        /**
         * Constructor.
         * @param pSigner the certifier
         * @param pEncryptor the encryptor
         */
        KeyPairSet(final PGPKeyPair pSigner,
                   final PGPKeyPair pEncryptor) {
            theCertify = pSigner;
            theEncrypting = pEncryptor;
            theSigning = null;
            theStyle = PGPXKeyRingStyle.DUO;
        }

        /**
         * Constructor.
         * @param pEncryptor the encryptor
         */
        KeyPairSet(final PGPKeyPair pEncryptor) {
            theCertify = pEncryptor;
            theEncrypting = null;
            theSigning = null;
            theStyle = PGPXKeyRingStyle.SOLO;
        }
    }
}
