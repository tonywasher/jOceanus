/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.core.lock;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory.GordianFactoryLock;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import io.github.tonywasher.joceanus.gordianknot.api.lock.GordianKeySetLock;
import io.github.tonywasher.joceanus.gordianknot.api.lock.GordianLockFactory;
import io.github.tonywasher.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Lock factory implementation.
 */
public class GordianCoreLockFactory
        implements GordianLockFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Locking factory.
     */
    private GordianBaseFactory lockFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public GordianCoreLockFactory(final GordianFactory pFactory) {
        theFactory = (GordianBaseFactory) pFactory;
    }

    /**
     * Create a new factoryLock for an existing factory.
     *
     * @param pFactoryToLock the factory to lock
     * @param pLockSpec      the lockSpec
     * @param pPassword      the password
     * @return the new factoryLock
     * @throws GordianException on error
     */
    public GordianFactoryLock newFactoryLock(final GordianFactory pFactoryToLock,
                                             final GordianPasswordLockSpec pLockSpec,
                                             final char[] pPassword) throws GordianException {
        /* Check the passwordLockSpec */
        checkPasswordLockSpec(pLockSpec);

        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), (GordianBaseFactory) pFactoryToLock, pLockSpec, pPassword);
    }

    /**
     * Create a new factoryLock for a new factory.
     *
     * @param pLockSpec    the lockSpec
     * @param pFactoryType the factoryType
     * @param pPassword    the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    public GordianFactoryLock newFactoryLock(final GordianPasswordLockSpec pLockSpec,
                                             final GordianFactoryType pFactoryType,
                                             final char[] pPassword) throws GordianException {
        /* Create the lockFactory */
        final GordianParameters myParams = GordianParameters.randomParams(pFactoryType);
        final GordianFactory myFactory = theFactory.newFactory(myParams);

        /* Create the factoryLock */
        return newFactoryLock(myFactory, pLockSpec, pPassword);
    }

    /**
     * Resolve the factoryLock.
     *
     * @param pLockBytes the lockBytes
     * @param pPassword  the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    public GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                 final char[] pPassword) throws GordianException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), pLockBytes, pPassword);
    }

    /**
     * Resolve factory lock.
     *
     * @param pLockASN1 th lock ASN1
     * @param pPassword the password
     * @return the resolved lock
     * @throws GordianException on error
     */
    public GordianFactoryLock resolveFactoryLock(final GordianPasswordLockASN1 pLockASN1,
                                                 final char[] pPassword) throws GordianException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), pLockASN1, pPassword);
    }

    @Override
    public GordianKeySetLock newKeySetLock(final GordianKeySet pKeySetToLock,
                                           final GordianPasswordLockSpec pLockSpec,
                                           final char[] pPassword) throws GordianException {
        /* Check the passwordLockSpec */
        checkPasswordLockSpec(pLockSpec);

        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, (GordianCoreKeySet) pKeySetToLock, pLockSpec, pPassword);
    }

    @Override
    public GordianKeySetLock newKeySetLock(final GordianPasswordLockSpec pLockSpec,
                                           final char[] pPassword) throws GordianException {
        /* Create a new random keySet */
        final GordianKeySet myKeySet = theFactory.getKeySetFactory().generateKeySet(pLockSpec.getKeySetSpec());

        /* Create the keySetLock */
        return newKeySetLock(myKeySet, pLockSpec, pPassword);
    }

    @Override
    public GordianKeySetLock resolveKeySetLock(final byte[] pLockBytes,
                                               final char[] pPassword) throws GordianException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, pLockBytes, pPassword);
    }

    /**
     * Resolve keySetLock.
     *
     * @param pLockASN1 the locking ASN1
     * @param pPassword the password
     * @return the resolved lock
     * @throws GordianException on error
     */
    public GordianKeySetLock resolveKeySetLock(final GordianPasswordLockASN1 pLockASN1,
                                               final char[] pPassword) throws GordianException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, pLockASN1, pPassword);
    }

    @Override
    public GordianKeyPairLock newKeyPairLock(final GordianPasswordLockSpec pLockSpec,
                                             final GordianKeyPair pKeyPair,
                                             final char[] pPassword) throws GordianException {
        /* Check the passwordLockSpec */
        checkPasswordLockSpec(pLockSpec);

        /* Create the keyPairLock */
        return new GordianKeyPairLockImpl(theFactory, pLockSpec, pKeyPair, pPassword);
    }

    @Override
    public GordianKeyPairLock resolveKeyPairLock(final byte[] pLockBytes,
                                                 final GordianKeyPair pKeyPair,
                                                 final char[] pPassword) throws GordianException {
        /* Create the keySetLock */
        return new GordianKeyPairLockImpl(theFactory, pLockBytes, pKeyPair, pPassword);
    }

    /**
     * Resolve keyPair lock.
     *
     * @param pLockASN1 the lock ASN1
     * @param pKeyPair  the keyPair
     * @param pPassword the password
     * @return the resolved keyPairLock
     * @throws GordianException on error
     */
    public GordianKeyPairLock resolveKeyPairLock(final GordianKeyPairLockASN1 pLockASN1,
                                                 final GordianKeyPair pKeyPair,
                                                 final char[] pPassword) throws GordianException {
        /* Create the keySetLock */
        return new GordianKeyPairLockImpl(theFactory, pLockASN1, pKeyPair, pPassword);
    }

    /**
     * obtain the locking factory.
     *
     * @return the locking factory
     * @throws GordianException on error
     */
    private GordianBaseFactory getLockingFactory() throws GordianException {
        /* Return lockFactory (if created) */
        if (lockFactory != null) {
            return lockFactory;
        }

        /* Synchronise on class */
        synchronized (this) {
            /* If lockFactory is not created */
            if (lockFactory == null) {
                /* Create the lockFactory */
                final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC, getHostName());
                lockFactory = (GordianBaseFactory) theFactory.newFactory(myParams);
            }

            /* Return the lockFactory */
            return lockFactory;
        }
    }

    /**
     * determine hostName.
     *
     * @return the hostName
     */
    private static char[] getHostName() {
        /* Protect against exceptions */
        try {
            final InetAddress myAddr = InetAddress.getLocalHost();
            return myAddr.getHostName().toCharArray();

        } catch (UnknownHostException e) {
            return "localhost".toCharArray();
        }
    }

    /**
     * Check the passwordLockSpec.
     *
     * @param pSpec the passwoerdLockSpec
     * @throws GordianException on error
     */
    public void checkPasswordLockSpec(final GordianPasswordLockSpec pSpec) throws GordianException {
        /* Check validity of PasswordLockSpec */
        if (!validPasswordLockSpec(pSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        }
    }

    /**
     * check valid passwordLockSpec.
     *
     * @param pSpec the passwordLockSpec
     * @return true/false
     */
    private static boolean validPasswordLockSpec(final GordianPasswordLockSpec pSpec) {
        /* Check for invalid spec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check on length */
        return GordianCoreKeySetFactory.validKeySetSpec(pSpec.getKeySetSpec());
    }
}
