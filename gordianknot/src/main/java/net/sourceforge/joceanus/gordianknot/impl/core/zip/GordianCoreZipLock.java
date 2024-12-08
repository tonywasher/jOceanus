/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.zip;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianCoreLockFactory;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.Arrays;
import java.util.Objects;

/**
 * Lock implementation.
 */
public class GordianCoreZipLock
    implements GordianZipLock {
    /**
     * UnLock notification.
     */
    interface GordianUnlockNotify {
        /**
         * Notify successful unlock.
         * @throws GordianException on error
         */
        void notifyUnlock() throws GordianException;
    }

    /**
     * The lock factory.
     */
    private final GordianCoreLockFactory theLockFactory;

    /**
     * The Lock.
     */
    private final GordianZipLockASN1 theZipLock;

    /**
     * The lockBytes.
     */
    private final byte[] theLockBytes;

    /**
     * The Unlock notification.
     */
    private final GordianUnlockNotify theNotify;

    /**
     * The keySet.
     */
    private GordianKeySet theKeySet;

    /**
     * is the lock available to create a zipFile?
     */
    private boolean isFresh;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pZipLock the zipLock message.
     * @throws GordianException on error
     */
    public GordianCoreZipLock(final GordianFactory pFactory,
                              final byte[] pZipLock) throws GordianException {
        this(pFactory, null, pZipLock);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pNotify the unlock notification (if any)
     * @param pZipLock the zipLock message.
     * @throws GordianException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianUnlockNotify pNotify,
                       final byte[] pZipLock) throws GordianException {
        /* Store parameters */
        theLockFactory = (GordianCoreLockFactory) pFactory.getLockFactory();
        theNotify = pNotify;
        theZipLock = GordianZipLockASN1.getInstance(pZipLock);
        theLockBytes = theZipLock.getEncodedBytes();
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pZipLock the zipLock message.
     * @throws GordianException on error
     */
    public GordianCoreZipLock(final GordianFactory pFactory,
                              final ASN1Encodable pZipLock) throws GordianException {
        this(pFactory, null, pZipLock);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pNotify the unlock notification (if any)
     * @param pZipLock the zipLock message.
     * @throws GordianException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianUnlockNotify pNotify,
                       final ASN1Encodable pZipLock) throws GordianException {
        /* Store parameters */
        theLockFactory = (GordianCoreLockFactory) pFactory.getLockFactory();
        theNotify = pNotify;
        theZipLock = GordianZipLockASN1.getInstance(pZipLock);
        theLockBytes = theZipLock.getEncodedBytes();
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the keySetLock
     * @throws GordianException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeySetLock pLock) throws GordianException {
        /* Store parameters */
        theLockFactory = (GordianCoreLockFactory) pFactory.getLockFactory();
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();
        theKeySet = pLock.getKeySet();

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the factoryLock
     * @throws GordianException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianFactoryLock pLock) throws GordianException {
        /* Store parameters */
        theLockFactory = (GordianCoreLockFactory) pFactory.getLockFactory();
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();
        theKeySet = pLock.getFactory().getEmbeddedKeySet();

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the keyPairLock
     * @throws GordianException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeyPairLock pLock) throws GordianException {
        /* Store parameters */
        theLockFactory = (GordianCoreLockFactory) pFactory.getLockFactory();
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();
        theKeySet = pLock.getKeySet();

        /* Available for locking */
        isFresh = true;
    }

    @Override
    public boolean isLocked() {
        return theKeySet == null;
    }

    @Override
    public boolean isFresh() {
        return isFresh;
    }

    @Override
    public GordianZipLockType getLockType() {
        return theZipLock.getLockType();
    }

    @Override
    public byte[] getLockBytes() throws GordianException {
        return theZipLock.getPasswordLockASN1().getEncodedBytes();
    }

    /**
     * Obtain LockASN1.
     * @return the lockASN1
     */
    public GordianZipLockASN1 getZipLockASN1() {
        return theZipLock;
    }

    @Override
    public void unlock(final GordianLock<?> pLock) throws GordianException {
        /* Check that this is the correct lock */
        if (!Arrays.equals(pLock.getLockBytes(), getLockBytes())) {
            throw new GordianDataException("Lock doesn't match");
        }

        /* Store the relevant keySet */
        if (pLock instanceof GordianKeySetLock) {
            theKeySet = ((GordianKeySetLock) pLock).getKeySet();
        } else if (pLock instanceof GordianFactoryLock) {
            theKeySet = ((GordianFactoryLock) pLock).getFactory().getEmbeddedKeySet();
        } else if (pLock instanceof GordianKeyPairLock) {
            theKeySet = ((GordianKeyPairLock) pLock).getKeySet();
        } else {
            throw new GordianDataException("Unsupported lockType");
        }

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    @Override
    public void unlock(final char[] pPassword) throws GordianException {
        /* Split out factoryLock */
        if (getLockType().equals(GordianZipLockType.FACTORY_PASSWORD)) {
            unlockFactory(pPassword);
            return;
        }

        /* Check that the state is correct */
        checkState(GordianZipLockType.KEYSET_PASSWORD);

        /* derive the keySet */
        final GordianKeySetLock myLock = theLockFactory.resolveKeySetLock(theZipLock.getPasswordLockASN1(), pPassword);
        theKeySet = myLock.getKeySet();

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * unlock a factory lock.
     * @param pPassword the password
     * @throws GordianException on error
     */
    private void unlockFactory(final char[] pPassword) throws GordianException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.FACTORY_PASSWORD);

        /* derive the keySet */
        final GordianFactoryLock myLock = theLockFactory.resolveFactoryLock(theZipLock.getPasswordLockASN1(), pPassword);
        theKeySet = myLock.getFactory().getEmbeddedKeySet();

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    @Override
    public void unlock(final GordianKeyPair pKeyPair,
                       final char[] pPassword) throws GordianException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.KEYPAIR_PASSWORD);

        /* derive the keySet */
        final GordianKeyPairLock myLock = theLockFactory.resolveKeyPairLock(theZipLock.getKeyPairLockASN1(), pKeyPair, pPassword);
        theKeySet = myLock.getKeySet();

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * unlock with keySet.
     * @param pKeySet the keySet
     * @throws GordianException on error
     */
    public void unlock(final GordianKeySet pKeySet) throws GordianException {
        /* store the keySet */
        theKeySet = pKeySet;

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * Obtain the keySetHash.
     * @return the keySetHash
     */
    public GordianKeySet getKeySet() {
        return theKeySet;
    }

    /**
     * Obtain the encoded bytes.
     * @return the encoded bytes
     */
    public byte[] getEncodedBytes() {
        return theLockBytes;
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
     * @throws GordianException on error
     */
    public void checkState(final GordianZipLockType pLockType) throws GordianException {
        /* Must be locked */
        if (!isLocked()) {
            throw new GordianLogicException("Already unlocked");
        }

        /* Check state */
        if (!getLockType().equals(pLockType)) {
            throw new GordianLogicException("Incorrect lockType");
        }
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
        if (!(pThat instanceof GordianCoreZipLock)) {
            return false;
        }
        final GordianCoreZipLock myThat = (GordianCoreZipLock) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theZipLock, myThat.theZipLock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theZipLock);
    }
}
