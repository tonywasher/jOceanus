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
package net.sourceforge.joceanus.jgordianknot.impl.core.lock;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Lock factory implementation.
 */
public class GordianCoreLockFactory
        implements GordianLockFactory {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianCoreLockFactory.class);

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Locking factory.
     */
    private GordianCoreFactory lockFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreLockFactory(final GordianFactory pFactory) {
        theFactory = (GordianCoreFactory) pFactory;
    }

    @Override
    public GordianFactoryLock newFactoryLock(final GordianFactory pFactoryToLock,
                                             final GordianPasswordLockSpec pLockSpec,
                                             final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), (GordianCoreFactory) pFactoryToLock, pLockSpec, pPassword);
    }

    @Override
    public GordianFactoryLock newFactoryLock(final GordianPasswordLockSpec pLockSpec,
                                             final char[] pPassword) throws OceanusException {
        /* Create the lockFactory */
        final GordianParameters myParams = GordianParameters.randomParams();
        final GordianFactory myFactory = theFactory.newFactory(myParams);

        /* Create the factoryLock */
        return newFactoryLock(myFactory, pLockSpec, pPassword);
    }

    @Override
    public GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), pLockBytes, pPassword);
    }

    /**
     * Resolve factory lock.
     * @param pLockASN1 th lock ASN1
     * @param pPassword the password
     * @return the resolved lock
     * @throws OceanusException on error
     */
    public GordianFactoryLock resolveFactoryLock(final GordianPasswordLockASN1 pLockASN1,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), pLockASN1, pPassword);
    }

    @Override
    public GordianKeySetLock newKeySetLock(final GordianKeySet pKeySetToLock,
                                           final GordianPasswordLockSpec pLockSpec,
                                           final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, (GordianCoreKeySet) pKeySetToLock, pLockSpec, pPassword);
    }

    @Override
    public GordianKeySetLock newKeySetLock(final GordianPasswordLockSpec pLockSpec,
                                           final char[] pPassword) throws OceanusException {
        /* Create a new random keySet */
        final GordianKeySet myKeySet = theFactory.getKeySetFactory().generateKeySet(pLockSpec.getKeySetSpec());

        /* Create the keySetLock */
        return newKeySetLock(myKeySet, pLockSpec, pPassword);
    }

    @Override
    public GordianKeySetLock resolveKeySetLock(final byte[] pLockBytes,
                                               final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, pLockBytes, pPassword);
    }

    /**
     * Resolve keySetLock.
     * @param pLockASN1 the locking ASN1
     * @param pPassword the password
     * @return the resolved lock
     * @throws OceanusException on error
     */
    public GordianKeySetLock resolveKeySetLock(final GordianPasswordLockASN1 pLockASN1,
                                               final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl(theFactory, pLockASN1, pPassword);
    }

    @Override
    public GordianKeyPairLock newKeyPairLock(final GordianPasswordLockSpec pLockSpec,
                                             final GordianKeyPair pKeyPair,
                                             final char[] pPassword) throws OceanusException {
        /* Create the keyPairLock */
        return new GordianKeyPairLockImpl(theFactory, pLockSpec, pKeyPair, pPassword);
    }

    @Override
    public GordianKeyPairLock resolveKeyPairLock(final byte[] pLockBytes,
                                                 final GordianKeyPair pKeyPair,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeyPairLockImpl(theFactory, pLockBytes, pKeyPair, pPassword);
    }

    /**
     * Resolve keyPair lock.
     * @param pLockASN1 the lock ASN1
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the resolved keyPairLock
     * @throws OceanusException on error
     */
    public GordianKeyPairLock resolveKeyPairLock(final GordianKeyPairLockASN1 pLockASN1,
                                                 final GordianKeyPair pKeyPair,
                                                 final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeyPairLockImpl(theFactory, pLockASN1, pKeyPair, pPassword);
    }

    /**
     * obtain the locking factory.
     *
     * @return the locking factory
     * @throws OceanusException on error
     */
    private GordianCoreFactory getLockingFactory() throws OceanusException {
        /* Return lockFactory (if created) */
        if (lockFactory != null) {
            return lockFactory;
        }

        /* Synchronise on class */
        synchronized (this) {
            /* If lockFactory is not created */
            if (lockFactory == null) {
                /* Create the lockFactory */
                final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
                myParams.setSecurityPhrase(getHostName());
                lockFactory = (GordianCoreFactory) theFactory.newFactory(myParams);
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
            LOGGER.error("Hostname can not be resolved", e);
            return "localhost".toCharArray();
        }
    }
}
