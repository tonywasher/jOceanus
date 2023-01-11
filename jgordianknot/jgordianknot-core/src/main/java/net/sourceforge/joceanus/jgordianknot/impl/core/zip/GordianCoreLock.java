/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.zip;

import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Lock implementation.
 */
public class GordianCoreLock
    implements GordianLock {
    /**
     * UnLock notification.
     */
    interface GordianUnlockNotify {
        /**
         * Notify successful unlock.
         * @throws OceanusException on error
         */
        void notifyUnlock() throws OceanusException;
    }

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Lock.
     */
    private final GordianLockASN1 theZipLock;

    /**
     * The Unlock notification.
     */
    private final GordianUnlockNotify theNotify;

    /**
     * The keySetHash.
     */
    private GordianKeySetHash theKeySetHash;

    /**
     * is the lock available to create a zipFile?
     */
    private boolean isFresh;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pZipLock the zipLock message.
     * @throws OceanusException on error
     */
    public GordianCoreLock(final GordianFactory pFactory,
                           final Object pZipLock) throws OceanusException {
        this(pFactory, null, pZipLock);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pNotify the unlock notification (if any)
     * @param pZipLock the zipLock message.
     * @throws OceanusException on error
     */
    GordianCoreLock(final GordianFactory pFactory,
                    final GordianUnlockNotify pNotify,
                    final Object pZipLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = pNotify;
        theZipLock = GordianLockASN1.getInstance(pZipLock);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeySetHashSpec the KeySetHashSpec
     * @param pPassword the password.
     * @throws OceanusException on error
     */
    GordianCoreLock(final GordianFactory pFactory,
                    final GordianKeySetHashSpec pKeySetHashSpec,
                    final char[] pPassword) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = null;

        /* create the keySetHash */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        theKeySetHash = myFactory.generateKeySetHash(pKeySetHashSpec, pPassword);
        final GordianKeySetHashASN1 myHashASN = GordianKeySetHashASN1.getInstance(theKeySetHash.getHash());
        theZipLock = new GordianLockASN1(myHashASN);

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pPassword the password.
     * @throws OceanusException on error
     */
    GordianCoreLock(final GordianFactory pFactory,
                    final char[] pPassword) throws OceanusException {
        /* No Notification */
        theNotify = null;

        /* Create the key */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pFactory;
        final byte[] myKey = new byte[GordianLength.LEN_256.getByteLength()];
        myFactory.getRandomSource().getRandom().nextBytes(myKey);
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        myParams.setInternal();
        myParams.setSecurityPhrase(myKey);
        theFactory = myFactory.newFactory(myParams);

        /* create the keySetHash */
        final GordianKeySetFactory myKSFactory = theFactory.getKeySetFactory();
        theKeySetHash = myKSFactory.generateKeySetHash(new GordianKeySetHashSpec(), pPassword);
        final GordianKeySetHashASN1 myHashASN = GordianKeySetHashASN1.getInstance(theKeySetHash.getHash());
        theZipLock = new GordianLockASN1(myHashASN, myKey);

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the KeySetHashSpec
     * @param pPassword the password.
     * @throws OceanusException on error
     */
    GordianCoreLock(final GordianFactory pFactory,
                    final GordianKeyPair pKeyPair,
                    final GordianKeySetHashSpec pKeySetHashSpec,
                    final char[] pPassword) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = null;

        /* Create the agreement */
        final GordianKeyPairFactory myKeyPairFactory = theFactory.getKeyPairFactory();
        final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
        final GordianAgreementSpec mySpec = getAgreementSpec(pKeyPair.getKeyPairSpec());
        final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(mySpec);
        myAgreement.setResultType(GordianFactoryType.BC);
        final byte[] myClientHello = myAgreement.createClientHello(pKeyPair);
        final GordianAgreementMessageASN1 myHelloASN = GordianAgreementMessageASN1.getInstance(myClientHello);
        myHelloASN.checkMessageType(GordianMessageType.CLIENTHELLO);
        final GordianFactory myFactory = (GordianFactory) myAgreement.getResult();

        /* create the keySetHash */
        final GordianKeySetFactory myKeySetFactory = myFactory.getKeySetFactory();
        theKeySetHash = myKeySetFactory.generateKeySetHash(pKeySetHashSpec, pPassword);
        final GordianKeySetHashASN1 myHashASN = GordianKeySetHashASN1.getInstance(theKeySetHash.getHash());
        theZipLock = new GordianLockASN1(myHashASN, myHelloASN);

        /* Available for locking */
        isFresh = true;
    }

    @Override
    public boolean isLocked() {
        return theKeySetHash == null;
    }

    @Override
    public boolean isFresh() {
        return isFresh;
    }

    @Override
    public GordianLockType getLockType() {
        return theZipLock.getLockType();
    }

    @Override
    public void unlock(final char[] pPassword) throws OceanusException {
        /* Split out keyLock */
        if (getLockType().equals(GordianLockType.KEY_PASSWORD)) {
            unlockKeyed(pPassword);
            return;
        }

        /* Check that the state is correct */
        checkState(GordianLockType.PASSWORD);

        /* derive the keySetHash */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final byte[] myHashBytes = theZipLock.getKeySetHash().getEncodedBytes();
        theKeySetHash = myFactory.deriveKeySetHash(myHashBytes, pPassword);

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * unlock a keyed lock.
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void unlockKeyed(final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianLockType.KEY_PASSWORD);

        /* Access the shared factory */
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        myParams.setInternal();
        myParams.setSecurityPhrase(theZipLock.getKey());
        final GordianFactory myFactory = ((GordianCoreFactory) theFactory).newFactory(myParams);

        /* derive the keySetHash */
        final GordianKeySetFactory myKSFactory = myFactory.getKeySetFactory();
        final byte[] myHashBytes = theZipLock.getKeySetHash().getEncodedBytes();
        theKeySetHash = myKSFactory.deriveKeySetHash(myHashBytes, pPassword);

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    @Override
    public void unlock(final GordianKeyPair pKeyPair,
                       final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianLockType.KEYPAIR_PASSWORD);

        /* Resolve the agreement */
        final GordianKeyPairFactory myKeyPairFactory = theFactory.getKeyPairFactory();
        final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
        final byte[] myClientHello = theZipLock.getKeyPairHello().getEncodedBytes();
        final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(myClientHello);
        myAgreement.acceptClientHello(pKeyPair, myClientHello);
        final GordianFactory myFactory = (GordianFactory) myAgreement.getResult();

        /* derive the keySetHash */
        final GordianKeySetFactory myKeySetFactory = myFactory.getKeySetFactory();
        final byte[] myHashBytes = theZipLock.getKeySetHash().getEncodedBytes();
        theKeySetHash = myKeySetFactory.deriveKeySetHash(myHashBytes, pPassword);

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * Obtain the keySetHash.
     * @return the keySetHash
     */
    public GordianKeySetHash getKeySetHash() {
        return theKeySetHash;
    }

    /**
     * Obtain the encoded bytes.
     * @return the encode bytes
     * @throws OceanusException on error
     */
    public byte[] getEncodedBytes() throws OceanusException {
        return theZipLock.getEncodedBytes();
    }

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return theZipLock.getAlgorithmId();
    }

    /**
     * Mark as used.
     */
    public void markAsUsed() {
        isFresh = false;
    }

    /**
     * Check the status.
     * @param pLockType the expected lockType
     * @throws OceanusException on error
     */
    private void checkState(final GordianLockType pLockType) throws OceanusException {
        /* Must be locked */
        if (!isLocked()) {
            throw new GordianLogicException("Already unlocked");
        }

        /* Check state */
        if (!getLockType().equals(pLockType)) {
            throw new GordianLogicException("Incorrect lockType");
        }
    }

    /**
     * Obtain AgreementSpec for asymKeySpec.
     * @param pKeySpec the keySpec
     * @return the agreementSpec
     * @throws OceanusException on error
     */
    private static GordianAgreementSpec getAgreementSpec(final GordianKeyPairSpec pKeySpec) throws OceanusException {
        /* Determine KDF type */
        final GordianKDFType myKDFType = GordianEdwardsElliptic.CURVE25519.equals(pKeySpec.getSubKeyType())
                    ? GordianKDFType.SHA256KDF
                    : GordianKDFType.SHA512KDF;

        /* Determine AgreementType - either ANON or KEM */
        if (GordianAgreementType.ANON.isSupported(pKeySpec.getKeyPairType())) {
            return new GordianAgreementSpec(pKeySpec, GordianAgreementType.ANON, myKDFType);
        }
        if (GordianAgreementType.KEM.isSupported(pKeySpec.getKeyPairType())) {
            return new GordianAgreementSpec(pKeySpec, GordianAgreementType.KEM, GordianKDFType.NONE);
        }
        throw new GordianLogicException("Invalid KeyPair type");
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the classes are the same */
        if (!(pThat instanceof GordianCoreLock)) {
            return false;
        }
        final GordianCoreLock myThat = (GordianCoreLock) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theZipLock, myThat.theZipLock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theZipLock);
    }
}
