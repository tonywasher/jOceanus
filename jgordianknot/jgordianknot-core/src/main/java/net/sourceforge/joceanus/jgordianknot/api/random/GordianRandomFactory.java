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
package net.sourceforge.joceanus.jgordianknot.api.random;

import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Random Factory API.
 */
public interface GordianRandomFactory {
    /**
     * create SecureRandom.
     * @param pRandomSpec the randomSpec
     * @return the new SecureRandom
     * @throws OceanusException on error
     */
    SecureRandom createRandom(GordianRandomSpec pRandomSpec) throws OceanusException;

    /**
     * create CombinedRandom.
     * @param pCtrSpec the ctrRandomSpec
     * @param pHashSpec the hashRandomSpec
     * @return the new SecureRandom
     * @throws OceanusException on error
     */
    SecureRandom createRandom(GordianRandomSpec pCtrSpec,
                              GordianRandomSpec pHashSpec) throws OceanusException;

    /**
     * Obtain predicate for supported randomSpecs.
     * @return the predicate
     */
    Predicate<GordianRandomSpec> supportedRandomSpecs();

    /**
     * Obtain predicate for supported combined randomSpecs.
     * @return the predicate
     */
    BiPredicate<GordianRandomSpec, GordianRandomSpec> supportedCombinedSpecs();

    /**
     * generate random GordianDigest.
     * @param pLargeData only generate a digest that is suitable for processing large amounts of data
     * @return the new Digest
     * @throws OceanusException on error
     */
    GordianDigest generateRandomDigest(boolean pLargeData) throws OceanusException;

    /**
     * generate random GordianMac.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for parsing large amounts of data
     * @return the new MAC
     * @throws OceanusException on error
     */
    GordianMac generateRandomMac(GordianLength pKeyLen,
                                 boolean pLargeData) throws OceanusException;

    /**
     * generate random SymKey.
     * @param pKeyLen the keyLength
     * @return the new key
     * @throws OceanusException on error
     */
    GordianKey<GordianSymKeySpec> generateRandomSymKey(GordianLength pKeyLen) throws OceanusException;

    /**
     * generate random GordianStreamKey.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for parsing large amounts of data
     * @return the new StreamKey
     * @throws OceanusException on error
     */
    GordianKey<GordianStreamKeySpec> generateRandomStreamKey(GordianLength pKeyLen,
                                                             boolean pLargeData) throws OceanusException;

    /**
     * Obtain a list of supported randomSpecs.
     * @return the list of supported randomSpecs.
     */
    List<GordianRandomSpec> listAllSupportedRandomSpecs();

    /**
     * Obtain a list of supported randomSpecs of a given type.
     * @param pType the random type
     * @return the list of supported randomSpecs.
     */
    List<GordianRandomSpec> listAllSupportedRandomSpecs(GordianRandomType pType);

    /**
     * Obtain a list of supported randomSpecs of a given type and keyLength.
     * <p>Only valid for CTR And X931 types</p>
     * @param pType the random type
     * @param pKeyLen the keyLength
     * @return the list of supported randomSpecs.
     */
    List<GordianRandomSpec> listAllSupportedRandomSpecs(GordianRandomType pType,
                                                        GordianLength pKeyLen);
}
