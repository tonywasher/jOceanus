/*
 * Prometheus: Application Framework
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
package io.github.tonywasher.joceanus.prometheus.security;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import io.github.tonywasher.joceanus.prometheus.exc.PrometheusSecurityException;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;

/**
 * Security Generator.
 */
public final class PrometheusSecurityGenerator {
    /**
     * Private constructor.
     */
    private PrometheusSecurityGenerator() {
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory the GUI Factory
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory) throws OceanusException {
        return newPasswordManager(pGuiFactory, GordianFactoryType.BC, new GordianPasswordLockSpec());
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory      the GUI Factory
     * @param pSecurityFactory the securityFactory
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianFactory pSecurityFactory) throws OceanusException {
        return newPasswordManager(pGuiFactory, pSecurityFactory, new GordianPasswordLockSpec());
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory  the GUI Factory
     * @param pFactoryType the factory type
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianFactoryType pFactoryType) throws OceanusException {
        return newPasswordManager(pGuiFactory, pFactoryType, new GordianPasswordLockSpec());
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory  the GUI Factory
     * @param pFactoryType the factory type
     * @param pLockSpec    the passwordLockSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianFactoryType pFactoryType,
                                                                       final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        /* Protect against exceptions */
        try {
            final GordianFactory mySecurityFactory = GordianGenerator.createRandomFactory(pFactoryType);
            return newPasswordManager(pGuiFactory, mySecurityFactory, pLockSpec);
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory     the GUI Factory
     * @param pFactoryType    the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianFactoryType pFactoryType,
                                                                       final char[] pSecurityPhrase) throws OceanusException {
        return newPasswordManager(pGuiFactory, new GordianPasswordLockSpec(), pFactoryType, pSecurityPhrase);
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory     the GUI Factory
     * @param pLockSpec       the passwordLockSpec
     * @param pFactoryType    the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianPasswordLockSpec pLockSpec,
                                                                       final GordianFactoryType pFactoryType,
                                                                       final char[] pSecurityPhrase) throws OceanusException {
        /* Protect against exceptions */
        try {
            final GordianFactory mySecurityFactory = GordianGenerator.createFactory(pFactoryType, pSecurityPhrase);
            return newPasswordManager(pGuiFactory, mySecurityFactory, pLockSpec);
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Create a password Manager.
     *
     * @param pGuiFactory      the GUI Factory
     * @param pSecurityFactory the securityFactory
     * @param pLockSpec        the passwordLockSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static PrometheusSecurityPasswordManager newPasswordManager(final TethysUIFactory<?> pGuiFactory,
                                                                       final GordianFactory pSecurityFactory,
                                                                       final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        final PrometheusSecurityDefaultDialog myController = new PrometheusSecurityDefaultDialog(pGuiFactory);
        return new PrometheusSecurityPasswordManager(pSecurityFactory, pLockSpec, myController);
    }
}
