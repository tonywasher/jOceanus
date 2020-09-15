/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementClientHelloASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ZipLock implementation.
 */
public class GordianCoreZipLock
    implements GordianZipLock {
    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Lock.
     */
    private final GordianZipLockASN1 theZipLock;

    /**
     * The Locked ZipFile.
     */
    private final GordianCoreZipReadFile theLockedZipFile;

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
     * @param pZipFile the locked zip file
     * @param pZipLock the zipLock message.
     * @throws OceanusException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianCoreZipReadFile pZipFile,
                       final byte[] pZipLock) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theLockedZipFile = pZipFile;
        theZipLock = GordianZipLockASN1.getInstance(pZipLock);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeySetHashSpec the KeySetHashSpec
     * @param pPassword the password.
     * @throws OceanusException on error
     */
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeySetHashSpec pKeySetHashSpec,
                       final char[] pPassword) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theLockedZipFile = null;

        /* create the keySetHash */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        theKeySetHash = myFactory.generateKeySetHash(pKeySetHashSpec, pPassword);
        final GordianKeySetHashASN1 myHashASN = GordianKeySetHashASN1.getInstance(theKeySetHash.getHash());
        theZipLock = new GordianZipLockASN1(myHashASN);

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
    GordianCoreZipLock(final GordianFactory pFactory,
                       final GordianKeyPair pKeyPair,
                       final GordianKeySetHashSpec pKeySetHashSpec,
                       final char[] pPassword) throws OceanusException {
        /* Store parameters */
        theFactory = pFactory;
        theLockedZipFile = null;

        /* Create the agreement */
        final GordianKeyPairFactory myKeyPairFactory = theFactory.getKeyPairFactory();
        final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
        final GordianAgreementSpec mySpec = getAgreementSpec(pKeyPair.getKeyPairSpec());
        final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(mySpec);
        myAgreement.setResultType(GordianFactoryType.BC);
        final byte[] myClientHello = myAgreement.createClientHello(pKeyPair);
        final GordianAgreementClientHelloASN1 myHelloASN = GordianAgreementClientHelloASN1.getInstance(myClientHello);
        final GordianFactory myFactory = (GordianFactory) myAgreement.getResult();

        /* create the keySetHash */
        final GordianKeySetFactory myKeySetFactory = myFactory.getKeySetFactory();
        theKeySetHash = myKeySetFactory.generateKeySetHash(pKeySetHashSpec, pPassword);
        final GordianKeySetHashASN1 myHashASN = GordianKeySetHashASN1.getInstance(theKeySetHash.getHash());
        theZipLock = new GordianZipLockASN1(myHashASN, myHelloASN);

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
    public GordianZipLockType getLockType() {
        return theZipLock.getLockType();
    }

    @Override
    public void unlock(final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.PASSWORD);

        /* derive the keySetHash */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final byte[] myHashBytes = theZipLock.getKeySetHash().getEncodedBytes();
        theKeySetHash = myFactory.deriveKeySetHash(myHashBytes, pPassword);

        /* unlock the zipFile */
        theLockedZipFile.unlockFile();
    }

    @Override
    public void unlock(final GordianKeyPair pKeyPair,
                       final char[] pPassword) throws OceanusException {
        /* Check that the state is correct */
        checkState(GordianZipLockType.KEYPAIR_PASSWORD);

        /* Resolve the agreement */
        final GordianKeyPairFactory myKeyPairFactory = theFactory.getKeyPairFactory();
        final GordianAgreementFactory myAgreeFactory = myKeyPairFactory.getAgreementFactory();
        final byte[] myClientHello = theZipLock.getClientHello().getEncodedBytes();
        final GordianAnonymousAgreement myAgreement = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(myClientHello);
        myAgreement.acceptClientHello(pKeyPair, myClientHello);
        final GordianFactory myFactory = (GordianFactory) myAgreement.getResult();

        /* derive the keySetHash */
        final GordianKeySetFactory myKeySetFactory = myFactory.getKeySetFactory();
        final byte[] myHashBytes = theZipLock.getKeySetHash().getEncodedBytes();
        theKeySetHash = myKeySetFactory.deriveKeySetHash(myHashBytes, pPassword);

        /* unlock the zipFile */
        theLockedZipFile.unlockFile();
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

    /**
     * Obtain AgreementSpec for asymKeySpec.
     * @param pKeySpec the keySpec
     * @return the agreementSpec
     */
    private static GordianAgreementSpec getAgreementSpec(final GordianKeyPairSpec pKeySpec) {
        final GordianKeyPairType myKeyType = pKeySpec.getKeyPairType();
        final GordianEdwardsElliptic myEdwards = pKeySpec.getEdwardsElliptic();
        final GordianKDFType myKDFType = GordianEdwardsElliptic.CURVE25519.equals(myEdwards)
                    ? GordianKDFType.SHA256KDF
                    : GordianKDFType.SHA512KDF;
        return new GordianAgreementSpec(myKeyType, GordianAgreementType.ANON, myKDFType);
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
