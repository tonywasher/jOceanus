package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import java.util.Iterator;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;

public class PGPBase {
    /* Source files location */
    private static final String HOME = System.getProperty("user.home");
    static final String FILEDIR = HOME + "/PGPTest/";

    /* Public Key Names */
    static final String PUBRSA = "PGPTest1.pub.txt";
    static final String PUBDSA = "PGPTest2.pub.txt";
    static final String PUBEC = "PGPTest3.pub.txt";

    /* Secret Key Names */
    static final String SECRSA = "PGPTest1.sec.txt";
    static final String SECDSA = "PGPTest2.sec.txt";
    static final String SECEC = "PGPTest3.sec.txt";

    /**
     * Obtain password for secret key
     * @param pSecret the keyRing name
     * @return password
     */
    static String obtainPassword4Secret(final String pSecret) {
        switch (pSecret) {
            case PGPBase.SECRSA: return "pgptest1";
            case PGPBase.SECDSA: return "pgptest2";
            case PGPBase.SECEC:  return "pgptest3";
            default:
                throw new IllegalArgumentException("Unexpected secret key.");
        }
    }

    /**
     * Try to find an encryption key in the keyRing
     * We will use the first one for now
     * @param pKey the keyRing
     * @return first encryption key
     */
    static PGPSignature obtainKeyIdSignature(final PGPPublicKey pKey,
                                             final long pKeyId) {
        /* Loop through signatures */
        for (Iterator<PGPSignature> sit = pKey.getSignatures(); sit.hasNext();) {
            PGPSignature sig = sit.next();

            /* Check that this is the required signature */
            PGPSignatureSubpacketVector v = sig.getUnhashedSubPackets();
            if (v != null && v.getIssuerKeyID() == pKeyId) {
                return sig;
            }
        }

        /* No valid signature */
        throw new IllegalArgumentException("Can't find signature for keyId.");
    }
}
