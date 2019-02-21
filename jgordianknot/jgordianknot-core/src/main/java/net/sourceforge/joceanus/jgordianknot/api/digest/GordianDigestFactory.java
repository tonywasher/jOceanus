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
package net.sourceforge.joceanus.jgordianknot.api.digest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Digest factory.
 */
public interface GordianDigestFactory {
    /**
     * create GordianDigest.
     * @param pDigestSpec the DigestSpec
     * @return the new Digest
     * @throws OceanusException on error
     */
    GordianDigest createDigest(GordianDigestSpec pDigestSpec) throws OceanusException;

    /**
     * Obtain predicate for supported digestSpecs.
     * @return the predicate
     */
    Predicate<GordianDigestSpec> supportedDigestSpecs();

    /**
     * Obtain predicate for supported digestTypes.
     * @return the predicate
     */
    Predicate<GordianDigestType> supportedDigestTypes();

    /**
     * Obtain predicate for supported external digests.
     * @return the predicate
     */
    Predicate<GordianDigestType> supportedExternalDigestTypes();

    /**
     * Obtain a list of supported digestSpecs.
     * @return the list of supported digestSpecs.
     */
    default List<GordianDigestSpec> listAllSupportedSpecs() {
        return GordianDigestSpec.listAll()
                .stream()
                .filter(supportedDigestSpecs())
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of supported digestTypes.
     * @return the list of supported digestTypes.
     */
    default List<GordianDigestType> listAllSupportedTypes() {
        return Arrays.stream(GordianDigestType.values())
                .filter(supportedDigestTypes())
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of external digestTypes.
     * @return the list of supported digestTypes.
     */
    default List<GordianDigestType> listAllExternalTypes() {
        return Arrays.stream(GordianDigestType.values())
                .filter(supportedExternalDigestTypes())
                .collect(Collectors.toList());
    }
}