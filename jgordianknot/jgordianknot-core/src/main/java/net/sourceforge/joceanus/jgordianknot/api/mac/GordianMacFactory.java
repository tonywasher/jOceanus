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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Mac factory.
 */
public interface GordianMacFactory {
    /**
     * obtain GordianKeyGenerator for MacSpec.
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws OceanusException on error
     */
    GordianKeyGenerator<GordianMacSpec> getKeyGenerator(GordianMacSpec pKeyType) throws OceanusException;

    /**
     * create GordianMac.
     * @param pMacSpec the MacSpec
     * @return the new MAC
     * @throws OceanusException on error
     */
    GordianMac createMac(GordianMacSpec pMacSpec) throws OceanusException;

    /**
     * Obtain predicate for supported macSpecs.
     * @return the predicate
     */
    Predicate<GordianMacSpec> supportedMacSpecs();

    /**
     * Obtain predicate for supported macTypes.
     * @return the predicate
     */
    Predicate<GordianMacType> supportedMacTypes();

    /**
     * Obtain predicate for supported hMac digestSpecs.
     * @return the predicate
     */
    Predicate<GordianDigestSpec> supportedHMacDigestSpecs();

    /**
     * Obtain predicate for supported hMac digestTypes.
     * @return the predicate
     */
    Predicate<GordianDigestType> supportedHMacDigestTypes();

    /**
     * Obtain predicate for supported poly1305 symKeySpecs.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedPoly1305SymKeySpecs();

    /**
     * Obtain predicate for supported gMac symKeySpecs.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedGMacSymKeySpecs();

    /**
     * Obtain predicate for supported cMac symKeyTypes.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedCMacSymKeySpecs();

    /**
     * Obtain a list of supported digestSpecs.
     * @return the list of supported digestSpecs.
     */
    default List<GordianMacSpec> listAllSupportedSpecs() {
        return GordianMacSpec.listAll()
                .stream()
                .filter(supportedMacSpecs())
                .collect(Collectors.toList());
    }
}
