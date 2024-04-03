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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Factory Lock implementation.
 */
public class GordianKeySetLockImpl
        implements GordianKeySetLock {
    /**
     * The keySet.
     */
    private final GordianCoreKeySet theKeySet;

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
     * @param pKeySetToLock the keySet to lock
     * @param pLockSpec the passwordLockSpec
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianKeySetLockImpl(final GordianCoreFactory pLockingFactory,
                                 final GordianCoreKeySet pKeySetToLock,
                                 final GordianPasswordLockSpec pLockSpec,
                                 final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the KeySet */
            theKeySet = pKeySetToLock;

            /* Create a recipe */
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, pLockSpec);

            /* Generate the hash */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final GordianCoreKeySet myKeySet = myRecipe.processPassword(pLockingFactory, myPassword);
            final byte[] myPayload = myKeySet.secureKeySet(pKeySetToLock);
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
    public GordianKeySetLockImpl(final GordianCoreFactory pLockingFactory,
                                 final byte[] pLockBytes,
                                 final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the LockBytes */
            theLockBytes = pLockBytes;
            theLockASN1 = GordianPasswordLockASN1.getInstance(pLockBytes);

            /* Resolve the recipe */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final GordianPasswordLockRecipe myRecipe = new GordianPasswordLockRecipe(pLockingFactory, myPassword.length, theLockASN1);

            /* Process the password, create parameters and factory */
            final GordianCoreKeySet myKeySet = myRecipe.processPassword(pLockingFactory, myPassword);
            theKeySet = myKeySet.deriveKeySet(myRecipe.getPayload());

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianKeySet getLockedObject() {
        return theKeySet;
    }

    @Override
    public GordianPasswordLockASN1 getLockASN1() {
        return theLockASN1;
    }

    @Override
    public byte[] getLockBytes() {
        return theLockBytes;
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
        if (!(pThat instanceof GordianKeySetLockImpl)) {
            return false;
        }

        /* Access the target field */
        final GordianKeySetLockImpl myThat = (GordianKeySetLockImpl) pThat;

        /* Check differences */
        return theKeySet.equals(myThat.getLockedObject())
                && Arrays.equals(theLockBytes, myThat.getLockBytes());
    }

    @Override
    public int hashCode() {
        return GordianCoreFactory.HASH_PRIME * theKeySet.hashCode()
                + Arrays.hashCode(theLockBytes);
    }
}
