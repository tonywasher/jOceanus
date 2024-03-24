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

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Builder methods.
 */
public final class GordianBuilder {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianBuilder.class);

    /**
     * Locking factory.
     */
    private static GordianCoreFactory LOCKFACTORY;

    /**
     * Private class.
     */
    private GordianBuilder() {
    }

    /**
     * Create a new factory instance.
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the securityPhrase
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createFactory(final GordianFactoryType pFactoryType,
                                               final char[] pSecurityPhrase) throws OceanusException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = new GordianParameters(pFactoryType);
        myParams.setSecurityPhrase(pSecurityPhrase);
        return myGenerator.newFactory(myParams);
    }

    /**
     * Create a new random bouncyCastle factory instance.
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createRandomFactory() throws OceanusException {
        /* Allocate a generator and the parameters */
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        myParams.setSecuritySeeds(GordianRandomSource.getStrongRandom());
        myParams.setInternal();
        return myGenerator.newFactory(myParams);
    }

    /**
     * Create a new factoryLock.
     * @param pFactoryToLock the factory to lock
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the factory lock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock createFactoryLock(final GordianFactory pFactoryToLock,
                                                       final GordianPasswordLockSpec pLockSpec,
                                                       final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), (GordianCoreFactory) pFactoryToLock, pLockSpec, pPassword);
    }

    /**
     * Resolve a factoryLock.
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @return the resolved factoryLock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                        final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianFactoryLockImpl(getLockingFactory(), pLockBytes, pPassword);
    }

    /**
     * Create a new keySetLock.
     * @param pLockingFactory the locking factory
     * @param pKeySetToLock the keySet to lock
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the keySet lock
     * @throws OceanusException on error
     */
    public static GordianKeySetLock createKeySetLock(final GordianFactory pLockingFactory,
                                                     final GordianKeySet pKeySetToLock,
                                                     final GordianPasswordLockSpec pLockSpec,
                                                     final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl((GordianCoreFactory) pLockingFactory, (GordianCoreKeySet) pKeySetToLock, pLockSpec, pPassword);
    }

    /**
     * Resolve a keySetLock.
     * @param pLockingFactory the locking factory
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @return the resolved keySetLock
     * @throws OceanusException on error
     */
    public static GordianKeySetLock resolveKeySetLock(final GordianFactory pLockingFactory,
                                                      final byte[] pLockBytes,
                                                      final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return new GordianKeySetLockImpl((GordianCoreFactory) pLockingFactory, pLockBytes, pPassword);
    }

    /**
     * obtain the locking factory.
     *
     * @return the locking factory
     * @throws OceanusException on error
     */
    private static GordianCoreFactory getLockingFactory() throws OceanusException {
        if (LOCKFACTORY != null) {
            return LOCKFACTORY;
        }
        synchronized (GordianBuilder.class) {
            if (LOCKFACTORY == null) {
                LOCKFACTORY = (GordianCoreFactory) createFactory(GordianFactoryType.BC, getHostName());
            }
            return LOCKFACTORY;
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

    /**
     * True Factory generator.
     */
    static class GordianUtilGenerator
            implements GordianFactoryGenerator {
        /**
         * Constructor.
         */
        GordianUtilGenerator() {
        }

        @Override
        public GordianFactory newFactory(final GordianParameters pParameters) throws OceanusException {
            /* Allocate the factory */
            return GordianFactoryType.BC.equals(pParameters.getFactoryType())
                    ? new BouncyFactory(this, pParameters)
                    : new JcaFactory(this, pParameters);
        }
    }
}
