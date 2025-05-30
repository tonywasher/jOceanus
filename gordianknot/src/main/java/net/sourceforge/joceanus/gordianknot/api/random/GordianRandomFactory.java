/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.random;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;

import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * GordianKnot Random Factory API.
 */
public interface GordianRandomFactory {
    /**
     * create SecureRandom.
     * @param pRandomSpec the randomSpec
     * @return the new SecureRandom
     * @throws GordianException on error
     */
    SecureRandom createRandom(GordianRandomSpec pRandomSpec) throws GordianException;

    /**
     * create CombinedRandom.
     * @param pCtrSpec the ctrRandomSpec
     * @param pHashSpec the hashRandomSpec
     * @return the new SecureRandom
     * @throws GordianException on error
     */
    SecureRandom createRandom(GordianRandomSpec pCtrSpec,
                              GordianRandomSpec pHashSpec) throws GordianException;

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
     * @throws GordianException on error
     */
    GordianDigest generateRandomDigest(boolean pLargeData) throws GordianException;

    /**
     * generate random GordianMac.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for parsing large amounts of data
     * @return the new MAC
     * @throws GordianException on error
     */
    GordianMac generateRandomMac(GordianLength pKeyLen,
                                 boolean pLargeData) throws GordianException;

    /**
     * generate random SymKey.
     * @param pKeyLen the keyLength
     * @return the new key
     * @throws GordianException on error
     */
    GordianKey<GordianSymKeySpec> generateRandomSymKey(GordianLength pKeyLen) throws GordianException;

    /**
     * generate random GordianStreamKey.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for parsing large amounts of data
     * @return the new StreamKey
     * @throws GordianException on error
     */
    GordianKey<GordianStreamKeySpec> generateRandomStreamKey(GordianLength pKeyLen,
                                                             boolean pLargeData) throws GordianException;

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
