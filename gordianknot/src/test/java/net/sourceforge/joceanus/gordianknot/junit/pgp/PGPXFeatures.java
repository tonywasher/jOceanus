package net.sourceforge.joceanus.gordianknot.junit.pgp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;

/**
 * PGP Features.
 */
public class PGPXFeatures {
    /**
     * The list of available hash algorithms.
     */
    private final List<Integer> theHashes;

    /**
     * The list of available symmetric algorithms.
     */
    private final List<Integer> theSyms;

    /**
     * The list of available compression algorithms.
     */
    private final List<Integer> theCompressions;

    /**
     * Do we use an integrity packet?.
     */
    private boolean withIntegrity;

    /**
     * Have we initialised?.
     */
    private boolean isInit;

    /**
     * Constructor.
     */
    private PGPXFeatures() {
        theHashes = new ArrayList<>();
        theSyms = new ArrayList<>();
        theCompressions = new ArrayList<>();
    }

    /**
     * Obtain the preferred hashAlgorithm.
     * @return the algorithm
     */
    int getHashAlgorithm() {
        return theHashes.get(0);
    }

    /**
     * Obtain the preferred symAlgorithm.
     * @return the algorithm
     */
    int getSymAlgorithm() {
        return theSyms.get(0);
    }

    /**
     * Obtain the preferred compressAlgorithm.
     * @return the algorithm
     */
    int getCompAlgorithm() {
        return theCompressions.get(0);
    }

    /**
     * Should we include an integrity packet?
     * @return true/false
     */
    boolean withIntegrity() {
        return withIntegrity;
    }

    /**
     * Determine the preferences.
     * @param pRings the keyRings
     */
    static PGPXFeatures determinePreferences(final List<BcPGPPublicKeyRing> pRings) {
        final PGPXFeatures myAlgs = new PGPXFeatures();
        for (PGPPublicKeyRing myRing : pRings) {
            myAlgs.updatePreferences(myRing);
        }
        return myAlgs;
    }

    /**
     * Update the preferences.
     * @param pRing the keyRing
     */
    private void updatePreferences(final PGPPublicKeyRing pRing) {
        /* Access the master key */
        final PGPPublicKey myMaster = pRing.getPublicKey();

        /* Access the binding signature */
        final PGPSignature mySig = PGPXKeyRingUtil.obtainKeyIdSignature(myMaster, myMaster.getKeyID());

        /* Update the preferences */
        final PGPSignatureSubpacketVector v = mySig.getHashedSubPackets();
        if (!isInit) {
            initPreferences(theHashes, v.getPreferredHashAlgorithms());
            initPreferences(theSyms, v.getPreferredSymmetricAlgorithms());
            initPreferences(theCompressions, v.getPreferredCompressionAlgorithms());
            withIntegrity = v.getFeatures().supportsFeature(Features.FEATURE_MODIFICATION_DETECTION);
            isInit = true;
        } else {
            adjustPreferences(theHashes, v.getPreferredHashAlgorithms());
            adjustPreferences(theSyms, v.getPreferredSymmetricAlgorithms());
            adjustPreferences(theCompressions, v.getPreferredCompressionAlgorithms());
            withIntegrity &= v.getFeatures().supportsFeature(Features.FEATURE_MODIFICATION_DETECTION);
        }
    }

    /**
     * Initialise the preferences.
     * @param pList the list
     * @param pAdjust the allowed values.
     */
    private void initPreferences(final List<Integer> pList,
                                 final int[] pAdjust) {
        if (pAdjust == null) {
            throw new IllegalStateException("No defined options");
        }
        for (int i : pAdjust) { pList.add(i); }
        if (pList.isEmpty()) {
            throw new IllegalStateException("No valid options");
        }
    }

    /**
     * Adjust preferences.
     * @param pList the list
     * @param pAdjust the allowed values.
     */
    private void adjustPreferences(final List<Integer> pList,
                                   final int[] pAdjust) {
        if (pAdjust == null) {
            throw new IllegalStateException("No defined options");
        }
        final List<Integer> myKeep = new ArrayList<>();
        for (int i : pAdjust) { myKeep.add(i); }
        final Iterator<Integer> myIterator = myKeep.iterator();
        while (myIterator.hasNext()) {
            final Integer myValue = myIterator.next();
            if (!myKeep.contains(myValue)) {
                myIterator.remove();
            }
        }
        if (pList.isEmpty()) {
            throw new IllegalStateException("No valid options");
        }
    }
}
