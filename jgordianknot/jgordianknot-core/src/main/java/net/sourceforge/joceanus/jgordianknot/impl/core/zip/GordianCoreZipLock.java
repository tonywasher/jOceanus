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
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeyPairLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBuilder;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @throws OceanusException on error
     */
    public GordianCoreZipLock(final GordianFactory pFactory,
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
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianUnlockNotify pNotify,
                       final Object pZipLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = pNotify;
        theZipLock = GordianZipLockASN1.getInstance(pZipLock);
        theLockBytes = theZipLock.getEncodedBytes();
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the keySetLock
     * @throws OceanusException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeySetLock pLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the factoryLock
     * @throws OceanusException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianFactoryLock pLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();

        /* Available for locking */
        isFresh = true;
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLock the keyPairLock
     * @throws OceanusException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeyPairLock pLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theNotify = null;
        theZipLock = new GordianZipLockASN1(pLock);
        theLockBytes = theZipLock.getEncodedBytes();

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
    public void unlock(final char[] pPassword) throws OceanusException {
        /* Split out factoryLock */
        if (getLockType().equals(GordianZipLockType.FACTORY_PASSWORD)) {
            unlockFactory(pPassword);
            return;
        }

        /* Check that the state is correct */
        checkState(GordianZipLockType.KEYSET_PASSWORD);

        /* derive the keySet */
        final GordianKeySetLock myLock = GordianBuilder.resolveKeySetLock(theFactory, theLockBytes, pPassword);
        theKeySet = myLock.getKeySet();

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    /**
     * unlock a factory lock.
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void unlockFactory(final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.FACTORY_PASSWORD);

        /* derive the keySet */
        final GordianFactoryLock myLock = GordianBuilder.resolveFactoryLock(theLockBytes, pPassword);
        theKeySet = myLock.getFactory().getEmbeddedKeySet();

        /* notify if required */
        if (theNotify != null) {
            theNotify.notifyUnlock();
        }
    }

    @Override
    public void unlock(final GordianKeyPair pKeyPair,
                       final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.KEYPAIR_PASSWORD);

        /* derive the keySet */
        final GordianKeyPairLock myLock = GordianBuilder.resolveKeyPairLock(theFactory, theLockBytes, pKeyPair, pPassword);
        theKeySet = myLock.getKeySet();

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
     * @throws OceanusException on error
     */
    private void checkState(final GordianZipLockType pLockType) throws OceanusException {
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
