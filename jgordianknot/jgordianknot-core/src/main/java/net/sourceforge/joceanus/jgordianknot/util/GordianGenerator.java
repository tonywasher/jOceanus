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
package net.sourceforge.joceanus.jgordianknot.util;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactoryLock;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.password.GordianCoreDialogControl;
import net.sourceforge.joceanus.jgordianknot.impl.core.password.GordianCorePasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * Factory generator.
 */
public final class GordianGenerator {
    /**
     * Private  Constructor.
     */
    private GordianGenerator() {
    }

    /**
     * Create a new factory instance.
     * @param pFactoryType the factoryType
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createFactory(final GordianFactoryType pFactoryType) throws OceanusException {
        /* Create a factory with null security phrase */
        return createFactory(pFactoryType, null);
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
     * @param pFactory the factory
     * @param pPassword the password
     * @return the factory lock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock createFactoryLock(final GordianFactory pFactory,
                                                       final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return new GordianCoreFactoryLock(pFactory, pPassword);
    }

    /**
     * Resolve a factoryLock.
     * @param pFactory a factory
     * @param pLock the lock
     * @param pPassword the password
     * @return the resolved factoryLock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock resolveFactoryLock(final GordianFactory pFactory,
                                                        final byte[] pLock,
                                                        final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        final GordianCoreFactoryLock myLock = new GordianCoreFactoryLock(pFactory, pLock, pPassword);
        final GordianParameters myParams = myLock.getParameters();
        final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
        final GordianFactory myFactory = myGenerator.newFactory(myParams);
        myLock.setFactory(myFactory);
        return myLock;
    }

    /**
     * Create a password Manager.
     * @param pFactory the GUI Factory
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @param pKeySetSpec the keySetSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pFactory,
                                                            final GordianFactoryType pFactoryType,
                                                            final char[] pSecurityPhrase,
                                                            final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        final GordianFactory myFactory = GordianGenerator.createFactory(pFactoryType, pSecurityPhrase);
        final GordianDialogController myController = new GordianCoreDialogControl(pFactory);
        return new GordianCorePasswordManager(myFactory, pKeySetSpec, myController);
    }

    /**
     * True Factory generator.
     */
    private static class GordianUtilGenerator
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
