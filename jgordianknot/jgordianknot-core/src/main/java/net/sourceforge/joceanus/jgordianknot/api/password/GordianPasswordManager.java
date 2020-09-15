/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeySetHash Manager.
 */
public interface GordianPasswordManager {
    /**
     * Obtain the security factory.
     * @return the security factory
     */
    GordianFactory getSecurityFactory();

    /**
     * Obtain the keySetSpec.
     * @return the keySetSpec
     */
    GordianKeySetSpec getKeySetSpec();

    /**
     * Create a new keySet Hash.
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    default GordianKeySetHash newKeySetHash(String pSource) throws OceanusException {
        return newKeySetHash(new GordianKeySetHashSpec(), pSource);
    }

    /**
     * Create a new keySet Hash.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash newKeySetHash(GordianKeySetHashSpec pKeySetHashSpec,
                                    String pSource) throws OceanusException;

    /**
     * Resolve the keySet Hash.
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash resolveKeySetHash(byte[] pHashBytes,
                                        String pSource) throws OceanusException;

    /**
     * obtain similar (same password) hash.
     * @param pReference the keySetHash to clone password from
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    default GordianKeySetHash similarKeySetHash(GordianKeySetHash pReference) throws OceanusException {
        return similarKeySetHash(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) hash.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the keySetHash to clone password from
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash similarKeySetHash(GordianKeySetHashSpec pKeySetHashSpec,
                                        GordianKeySetHash pReference) throws OceanusException;

    /**
     * Create a new zipLock.
     * @param pSource the description of the secured resource
     * @return the zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock newZipLock(String pSource) throws OceanusException {
        return newZipLock(new GordianKeySetHashSpec(), pSource);
    }

    /**
     * Create a new zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianZipLock newZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                              String pSource) throws OceanusException;

    /**
     * Create a new zipLock.
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock newZipLock(GordianKeyPair pKeyPair,
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
    GordianZipLock newZipLock(GordianKeyPair pKeyPair,
                              GordianKeySetHashSpec pKeySetHashSpec,
                              String pSource) throws OceanusException;

    /**
     * Resolve the zipLock.
     * @param pZipLock the zipLock to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    void resolveZipLock(GordianZipLock pZipLock,
                        String pSource) throws OceanusException;

    /**
     * Resolve the zipLock.
     * @param pKeyPair the keyPair
     * @param pZipLock the zipLock to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    void resolveZipLock(GordianKeyPair pKeyPair,
                        GordianZipLock pZipLock,
                        String pSource) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pReference the keySetHash to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock similarZipLock(GordianKeySetHash pReference) throws OceanusException {
        return similarZipLock(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the keySetHash to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianZipLock similarZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                                  GordianKeySetHash pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the keySetHash to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock similarZipLock(GordianKeyPair pKeyPair,
                                          GordianKeySetHash pReference) throws OceanusException {
        return similarZipLock(pKeyPair, new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the keySetHash to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianZipLock similarZipLock(GordianKeyPair pKeyPair,
                                  GordianKeySetHashSpec pKeySetHashSpec,
                                  GordianKeySetHash pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pReference the zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock similarZipLock(GordianZipLock pReference) throws OceanusException {
        return similarZipLock(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    GordianZipLock similarZipLock(GordianKeySetHashSpec pKeySetHashSpec,
                                  GordianZipLock pReference) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the keySetHash/zipLock to clone password from
     * @return the similar zipLock
     * @throws OceanusException on error
     */
    default GordianZipLock similarZipLock(GordianKeyPair pKeyPair,
                                          GordianZipLock pReference) throws OceanusException {
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
    GordianZipLock similarZipLock(GordianKeyPair pKeyPair,
                                  GordianKeySetHashSpec pKeySetHashSpec,
                                  GordianZipLock pReference) throws OceanusException;
}
