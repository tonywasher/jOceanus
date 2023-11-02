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
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBaseDialogControl;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBasePasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBuilder;
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
        /* Create the factory */
        return GordianBuilder.createFactory(pFactoryType, pSecurityPhrase);
    }

    /**
     * Create a new random bouncyCastle factory instance.
     * @return the new factory
     * @throws OceanusException on error
     */
    public static GordianFactory createRandomFactory() throws OceanusException {
        /* Create the random factory */
        return GordianBuilder.createRandomFactory();
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
        return GordianBuilder.createFactoryLock(pFactory, pPassword);
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
        /* Resolve the factoryLock */
        return GordianBuilder.resolveFactoryLock(pFactory, pLock, pPassword);
    }

    /**
     * Create a password Manager.
     * @param pFactory the GUI Factory
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pFactory,
                                                            final GordianFactoryType pFactoryType,
                                                            final char[] pSecurityPhrase) throws OceanusException {
        final GordianFactory myFactory = GordianGenerator.createFactory(pFactoryType, pSecurityPhrase);
        final GordianDialogController myController = new GordianBaseDialogControl(pFactory);
        return new GordianBasePasswordManager(myFactory, myController);
    }
}
