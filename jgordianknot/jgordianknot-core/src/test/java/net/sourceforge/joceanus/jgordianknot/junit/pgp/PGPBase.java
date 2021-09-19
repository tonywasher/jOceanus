package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;

/**
 * Base PGP methods/constants.
 */
public class PGPBase {
    /* Source files location */
    private static final String HOME = System.getProperty("user.home");
    static final String FILEDIR = HOME + "/PGPTest/";

    /* Public Key Names */
    static final String PUBRSA = "PGPTest1.pub.txt";
    static final String PUBDSA = "PGPTest2.pub.txt";
    static final String PUBED = "PGPTest3.pub.txt";
    static final String PUBEC = "PGPTest4.pub.txt";

    /* Secret Key Names */
    static final String SECRSA = "PGPTest1.sec.txt";
    static final String SECDSA = "PGPTest2.sec.txt";
    static final String SECED = "PGPTest3.sec.txt";
    static final String SECEC = "PGPTest4.sec.txt";

    /* Buffer Size */
    static final int BUFFER_SIZE = 1 << 16;

    /**
     * Obtain password for secret key
     * @param pSecret the keyRing name
     * @return password
     */
    static String obtainPassword4Secret(final String pSecret) {
        switch (pSecret) {
            case PGPBase.SECRSA: return "pgptest1";
            case PGPBase.SECDSA: return "pgptest2";
            case PGPBase.SECED:  return "pgptest3";
            case PGPBase.SECEC:  return "pgptest4";
            default:
                throw new IllegalArgumentException("Unexpected secret key.");
        }
    }

    /**
     * Obtain the signature signed by the specified key.
     * @param pKey the keyRing
     * @param pKeyId the keyId
     * @return the signature
     */
    static PGPSignature obtainKeyIdSignature(final PGPPublicKey pKey,
                                             final long pKeyId) {
        /* Loop through signatures */
        for (Iterator<PGPSignature> sit = pKey.getSignatures(); sit.hasNext();) {
            final PGPSignature sig = sit.next();

            /* Check that this is the required signature */
            final PGPSignatureSubpacketVector v = sig.getUnhashedSubPackets();
            if (v != null && v.getIssuerKeyID() == pKeyId
                    && checkSigValidity(sig)) {
                return sig;
            }
        }

        /* No valid signature */
        throw new IllegalArgumentException("Can't find signature for keyId.");
    }

    /**
     * Check key validity.
     * @param pSig the signature
     * @return valid true/false
     */
    static boolean checkKeyValidity(final PGPSignature pSig) {
        /* Access detail */
        final PGPSignatureSubpacketVector v = pSig.getHashedSubPackets();
        final Date myCreate = v.getSignatureCreationTime();
        final LocalDateTime myExpireTime = myCreate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        final long myExpire = v.getKeyExpirationTime();
        return myExpire == 0 || myExpireTime.plusSeconds(myExpire).isAfter(LocalDateTime.now());
    }

    /**
     * Check signature validity.
     * @param pSig the signature
     * @return valid true/false
     */
    private static boolean checkSigValidity(final PGPSignature pSig) {
        /* Access detail */
        final PGPSignatureSubpacketVector v = pSig.getHashedSubPackets();
        final Date myCreate = v.getSignatureCreationTime();
        final LocalDateTime myExpireTime = myCreate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        final long myExpire = v.getSignatureExpirationTime();
        return myExpire == 0 || myExpireTime.plusSeconds(myExpire).isAfter(LocalDateTime.now());
    }
}
