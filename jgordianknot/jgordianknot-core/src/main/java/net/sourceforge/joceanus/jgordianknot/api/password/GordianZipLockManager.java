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
package net.sourceforge.joceanus.jgordianknot.api.password;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Lock Manager.
 */
public interface GordianZipLockManager {
    /**
     * Create a new zipLock.
     * @param pSource the description of the secured resource
     * @return the zipLock
     * @throws OceanusException on error
     */
    default GordianLock newZipLock(String pSource) throws OceanusException {
        return newZipLock(new GordianKeySetHashSpec(), pSource);
    }

    /**
     * Create a new zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianLock newZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                           String pSource) throws OceanusException;

    /**
     * Create a new zipLock.
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the zipLock
     * @throws OceanusException on error
     */
    default GordianLock newZipLock(GordianKeyPair pKeyPair,
                                   String pSource) throws OceanusException {
        return newZipLock(pKeyPair, new GordianKeySetHashSpec(), pSource);
    }

    /**
     * Create a new zipLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianLock newZipLock(GordianKeyPair pKeyPair,
                           GordianKeySetHashSpec pKeySetHashSpec,
                           String pSource) throws OceanusException;

    /**
     * Resolve the zipLock.
     * @param pZipLock the zipLock to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    void resolveZipLock(GordianLock pZipLock,
                        String pSource) throws OceanusException;

    /**
     * Resolve the zipLock.
     * @param pKeyPair the keyPair
     * @param pZipLock the zipLock to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    void resolveZipLock(GordianKeyPair pKeyPair,
                        GordianLock pZipLock,
                        String pSource) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pReference the reference to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianLock similarZipLock(Object pReference) throws OceanusException {
        return similarZipLock(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the reference to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianLock similarZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                               Object pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the reference to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianLock similarZipLock(GordianKeyPair pKeyPair,
                                       Object pReference) throws OceanusException {
        return similarZipLock(pKeyPair, new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the reference to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianLock similarZipLock(GordianKeyPair pKeyPair,
                               GordianKeySetHashSpec pKeySetHashSpec,
                               Object pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pReference the zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianLock similarZipLock(GordianLock pReference) throws OceanusException {
        return similarZipLock(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianLock similarZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                               GordianLock pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the keySetHash/zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianLock similarZipLock(GordianKeyPair pKeyPair,
                                       GordianLock pReference) throws OceanusException {
        return similarZipLock(pKeyPair, new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianLock similarZipLock(GordianKeyPair pKeyPair,
                               GordianKeySetHashSpec pKeySetHashSpec,
                               GordianLock pReference) throws OceanusException;
}