/*
 * GordianKnot: Security Suite
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
package io.github.tonywasher.joceanus.gordianknot.api.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Digest factory.
 */
public interface GordianDigestFactory {
    /**
     * create GordianDigest.
     *
     * @param pDigestSpec the DigestSpec
     * @return the new Digest
     * @throws GordianException on error
     */
    GordianDigest createDigest(GordianDigestSpec pDigestSpec) throws GordianException;

    /**
     * Obtain predicate for supported digestSpecs.
     *
     * @return the predicate
     */
    Predicate<GordianDigestSpec> supportedDigestSpecs();

    /**
     * Obtain predicate for supported digestTypes.
     *
     * @return the predicate
     */
    Predicate<GordianDigestType> supportedDigestTypes();

    /**
     * Obtain a list of supported digestSpecs.
     *
     * @return the list of supported digestSpecs.
     */
    List<GordianDigestSpec> listAllSupportedSpecs();

    /**
     * Obtain a list of supported digestTypes.
     *
     * @return the list of supported digestTypes.
     */
    List<GordianDigestType> listAllSupportedTypes();

    /**
     * List all possible digestSpecs.
     *
     * @return the list
     */
    List<GordianDigestSpec> listAllPossibleSpecs();
}
