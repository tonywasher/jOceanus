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
package net.sourceforge.joceanus.jgordianknot.util;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
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
     * secure Factory.
     * @param pKeySet the keySet to use
     * @param pFactoryToSecure the keySet to secure
     * @return the securedFactory
     * @throws OceanusException on error
     */
    public static byte[] secureFactory(final GordianKeySet pKeySet,
                                       final GordianFactory pFactoryToSecure) throws OceanusException {
        return ((GordianCoreKeySet) pKeySet).secureFactory(pFactoryToSecure);
    }

    /**
     * derive Factory.
     * @param pKeySet the keySet to use
     * @param pSecuredFactory the secured factory
     * @return the derived factory
     * @throws OceanusException on error
     */
    public static GordianFactory deriveFactory(final GordianKeySet pKeySet,
                                               final byte[] pSecuredFactory) throws OceanusException {
        return ((GordianCoreKeySet) pKeySet).deriveFactory(pSecuredFactory);
    }

    /**
     * Create a new factoryLock.
     * @param pFactory the factory
     * @param pPassword the password
     * @return the factoryLock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock createFactoryLock(final GordianFactory pFactory,
                                                       final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return createFactoryLock(pFactory, new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a new factoryLock.
     * @param pFactory the factory
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the factoryLock
     * @throws OceanusException on error
     */
    public static GordianFactoryLock createFactoryLock(final GordianFactory pFactory,
                                                       final GordianPasswordLockSpec pLockSpec,
                                                       final char[] pPassword) throws OceanusException {
        /* Create the factoryLock */
        return GordianBuilder.createFactoryLock(pFactory, pLockSpec, pPassword);
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
        /* Resolve the factoryLock */
        return GordianBuilder.resolveFactoryLock(pLockBytes, pPassword);
    }


    /**
     * Create a new keySetLock.
     * @param pLockingFactory the locking factory
     * @param pKeySetToLock the keySet to lock
     * @param pPassword the password
     * @return the keySetLock
     * @throws OceanusException on error
     */
    public static GordianKeySetLock createKeySetLock(final GordianFactory pLockingFactory,
                                                     final GordianKeySet pKeySetToLock,
                                                     final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return createKeySetLock(pLockingFactory, pKeySetToLock, new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a new keySetLock.
     * @param pLockingFactory the locking factory
     * @param pKeySetToLock the keySet to lock
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the keySetLock
     * @throws OceanusException on error
     */
    public static GordianKeySetLock createKeySetLock(final GordianFactory pLockingFactory,
                                                     final GordianKeySet pKeySetToLock,
                                                     final GordianPasswordLockSpec pLockSpec,
                                                     final char[] pPassword) throws OceanusException {
        /* Create the keySetLock */
        return GordianBuilder.createKeySetLock(pLockingFactory, pKeySetToLock, pLockSpec, pPassword);
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
        /* Resolve the keySetLock */
        return GordianBuilder.resolveKeySetLock(pLockingFactory, pLockBytes, pPassword);
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory) throws OceanusException {
         return newPasswordManager(pGuiFactory, new GordianPasswordLockSpec());
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @param pSecurityFactory the securityFactory
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                            final GordianFactory pSecurityFactory) throws OceanusException {
        return newPasswordManager(pGuiFactory, pSecurityFactory, new GordianPasswordLockSpec());
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @param pLockSpec the passwordLockSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                            final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        final GordianFactory mySecurityFactory = GordianGenerator.createRandomFactory();
        return newPasswordManager(pGuiFactory, mySecurityFactory, pLockSpec);
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                            final GordianFactoryType pFactoryType,
                                                            final char[] pSecurityPhrase) throws OceanusException {
        return newPasswordManager(pGuiFactory, new GordianPasswordLockSpec(), pFactoryType, pSecurityPhrase);
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @param pLockSpec the passwordLockSpec
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                            final GordianPasswordLockSpec pLockSpec,
                                                            final GordianFactoryType pFactoryType,
                                                            final char[] pSecurityPhrase) throws OceanusException {
        final GordianFactory mySecurityFactory = GordianGenerator.createFactory(pFactoryType, pSecurityPhrase);
        return newPasswordManager(pGuiFactory, mySecurityFactory, pLockSpec);
    }

    /**
     * Create a password Manager.
     * @param pGuiFactory the GUI Factory
     * @param pSecurityFactory the securityFactory
     * @param pLockSpec the passwordLockSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                            final GordianFactory pSecurityFactory,
                                                            final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        final GordianDialogController myController = new GordianBaseDialogControl(pGuiFactory);
        return new GordianBasePasswordManager(pSecurityFactory, pLockSpec, myController);
    }
}
