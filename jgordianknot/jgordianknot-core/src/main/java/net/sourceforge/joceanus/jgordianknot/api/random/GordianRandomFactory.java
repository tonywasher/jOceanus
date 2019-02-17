/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
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
     * Obtain predicate for supported randomSpecs.
     * @return the predicate
     */
    Predicate<GordianRandomSpec> supportedRandomSpecs();

    /**
     * generate random GordianDigest.
     * @return the new Digest
     * @throws OceanusException on error
     */
    GordianDigest generateRandomDigest() throws OceanusException;

    /**
     * generate random GordianMac.
     * @return the new MAC
     * @throws OceanusException on error
     */
    GordianMac generateRandomMac() throws OceanusException;

    /**
     * generate random SymKey.
     * @return the new key
     * @throws OceanusException on error
     */
    GordianKey<GordianSymKeySpec> generateRandomSymKey() throws OceanusException;

    /**
     * generate random GordianStreamKey.
     * @return the new StreamKey
     * @throws OceanusException on error
     */
    GordianKey<GordianStreamKeyType> generateRandomStreamKey() throws OceanusException;

    /**
     * Obtain a list of supported randomSpecs.
     * @return the list of supported randomSpecs.
     */
    default List<GordianRandomSpec> listAllSupportedRandomSpecs() {
        return GordianRandomSpec.listAll()
                .stream()
                .filter(supportedRandomSpecs())
                .collect(Collectors.toList());
    }
}
