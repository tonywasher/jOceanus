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
package net.sourceforge.joceanus.gordianknot.util;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaFactory;
import net.sourceforge.joceanus.gordianknot.impl.password.GordianBaseDialogControl;
import net.sourceforge.joceanus.gordianknot.impl.password.GordianBasePasswordManager;
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
        final GordianParameters myParams = GordianParameters.randomParams();
        return myGenerator.newFactory(myParams);
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
        final GordianFactory mySecurityFactory = createRandomFactory();
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
        final GordianFactory mySecurityFactory = createFactory(pFactoryType, pSecurityPhrase);
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
