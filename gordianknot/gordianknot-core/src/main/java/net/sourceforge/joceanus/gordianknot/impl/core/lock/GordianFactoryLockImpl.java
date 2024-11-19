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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import java.util.Arrays;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.TethysDataConverter;

/**
 * Factory Lock implementation.
 */
public class GordianFactoryLockImpl
    implements GordianFactoryLock {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The lockASN1.
     */
    private final GordianPasswordLockASN1 theLockASN1;

    /**
     * The lockBytes.
     */
    private final byte[] theLockBytes;

    /**
     * Locking constructor.
     * @param pLockingFactory the locking factory
     * @param pFactoryToLock the factory to lock
     * @param pLockSpec the passwordLockSpec
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianFactoryLockImpl(final GordianCoreFactory pLockingFactory,
                                  final GordianCoreFactory pFactoryToLock,
                                  final GordianPasswordLockSpec pLockSpec,
                                  final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Factory */
            theFactory = pFactoryToLock;

            /* Reject the operation if not a random factory */
            if (!pFactoryToLock.isRandom()) {
                throw new GordianDataException("attempt to lock non-Random factory");
            }

            /* Create a recipe */
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, pLockSpec);

            /* Generate the lockBytes */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final GordianCoreKeySet myKeySet = myRecipe.processPassword(pLockingFactory, myPassword);
            final byte[] myPayload = myKeySet.secureFactory(pFactoryToLock);
            theLockASN1 = myRecipe.buildLockASN1(myPassword.length, myPayload);
            theLockBytes = theLockASN1.getEncodedBytes();

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * UnLocking constructor.
     * @param pLockingFactory the locking factory
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianFactoryLockImpl(final GordianCoreFactory pLockingFactory,
                                  final byte[] pLockBytes,
                                  final char[] pPassword) throws OceanusException {
        this(pLockingFactory, GordianPasswordLockASN1.getInstance(pLockBytes), pPassword);
    }

    /**
     * UnLocking constructor.
     * @param pLockingFactory the locking factory
     * @param pLockASN1 the lockASN1
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianFactoryLockImpl(final GordianCoreFactory pLockingFactory,
                                  final GordianPasswordLockASN1 pLockASN1,
                                  final char[] pPassword) throws OceanusException {
        this(pLockingFactory, pLockASN1, pLockASN1.getEncodedBytes(), pPassword);
    }

    /**
     * UnLocking constructor.
     * @param pLockingFactory the locking factory
     * @param pLockASN1 the lockASN1
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianFactoryLockImpl(final GordianCoreFactory pLockingFactory,
                                  final GordianPasswordLockASN1 pLockASN1,
                                  final byte[] pLockBytes,
                                  final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Lock */
            theLockBytes = pLockBytes;
            theLockASN1 = pLockASN1;

            /* Resolve the recipe */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, myPassword.length, theLockASN1);

            /* Process the password, create parameters and factory */
            final GordianCoreKeySet myKeySet = myRecipe.processPassword(pLockingFactory, myPassword);
            theFactory = (GordianCoreFactory) myKeySet.deriveFactory(myRecipe.getPayload());

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianFactory getLockedObject() {
        return theFactory;
    }

    @Override
    public GordianPasswordLockASN1 getLockASN1() {
        return theLockASN1;
    }

    @Override
    public byte[] getLockBytes() {
        return theLockBytes;
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    public static int getEncodedLength() {
        return GordianPasswordLockASN1.getEncodedLength(GordianCoreKeySet.getEncryptionLength(GordianParameters.SECRET_LEN.getByteLength() << 1));
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianFactoryLockImpl)) {
            return false;
        }

        /* Access the target field */
        final GordianFactoryLockImpl myThat = (GordianFactoryLockImpl) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getLockedObject())
                && Arrays.equals(theLockBytes, myThat.getLockBytes());
    }

    @Override
    public int hashCode() {
        return GordianCoreFactory.HASH_PRIME * theFactory.hashCode()
                + Arrays.hashCode(theLockBytes);
    }
}
