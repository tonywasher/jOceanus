/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache of KeyGenerators.
 */
public abstract class GordianKeyGeneratorCache {
    /**
     * SymmetricKey Cache.
     */
    private final Map<GordianSymKeySpec, GordianKeyGenerator<GordianSymKeySpec>> theSymKeyCache;

    /**
     * StreamKey Cache.
     */
    private final Map<GordianStreamKeyType, GordianKeyGenerator<GordianStreamKeyType>> theStreamKeyCache;

    /**
     * MacKey Cache.
     */
    private final Map<GordianMacSpec, GordianKeyGenerator<GordianMacSpec>> theMacKeyCache;

    /**
     * KeyPair Cache.
     */
    private final Map<GordianAsymKeySpec, GordianKeyPairGenerator> theKeyPairCache;

    /**
     * Constructor.
     */
    protected GordianKeyGeneratorCache() {
        theSymKeyCache = new HashMap<>();
        theStreamKeyCache = new EnumMap<>(GordianStreamKeyType.class);
        theMacKeyCache = new HashMap<>();
        theKeyPairCache = new HashMap<>();
    }

    /**
     * Lookup SymKeyGenerator in cache.
     * @param pKeyType the keyType
     * @return the generator (or null)
     */
    protected GordianKeyGenerator<GordianSymKeySpec> getCachedSymKeyGenerator(final GordianSymKeySpec pKeyType) {
        return theSymKeyCache.get(pKeyType);
    }

    /**
     * Cache SymKeyGenerator.
     * @param pGenerator the generator
     */
    protected void addToSymKeyCache(final GordianKeyGenerator<GordianSymKeySpec> pGenerator) {
        theSymKeyCache.put(pGenerator.getKeyType(), pGenerator);
    }

    /**
     * Lookup StreamKeyGenerator in cache.
     * @param pKeyType the keyType
     * @return the generator (or null)
     */
    protected GordianKeyGenerator<GordianStreamKeyType> getCachedStreamKeyGenerator(final GordianStreamKeyType pKeyType) {
        return theStreamKeyCache.get(pKeyType);
    }

    /**
     * Cache StreamKeyGenerator.
     * @param pGenerator the generator
     */
    protected void addToStreamKeyCache(final GordianKeyGenerator<GordianStreamKeyType> pGenerator) {
        theStreamKeyCache.put(pGenerator.getKeyType(), pGenerator);
    }

    /**
     * Lookup MacKeyGenerator in cache.
     * @param pKeyType the keyType
     * @return the generator (or null)
     */
    protected GordianKeyGenerator<GordianMacSpec> getCachedMacKeyGenerator(final GordianMacSpec pKeyType) {
        return theMacKeyCache.get(pKeyType);
    }

    /**
     * Cache StreamKeyGenerator.
     * @param pGenerator the generator
     */
    protected void addToMacKeyCache(final GordianKeyGenerator<GordianMacSpec> pGenerator) {
        theMacKeyCache.put(pGenerator.getKeyType(), pGenerator);
    }

    /**
     * Lookup KeyPairGenerator in cache.
     * @param pKeySpec the keySpec
     * @return the generator (or null)
     */
    protected GordianKeyPairGenerator getCachedKeyPairGenerator(final GordianAsymKeySpec pKeySpec) {
        return theKeyPairCache.get(pKeySpec);
    }

    /**
     * Cache KeyPairGenerator.
     * @param pGenerator the generator
     */
    protected void addToKeyPairCache(final GordianKeyPairGenerator pGenerator) {
        theKeyPairCache.put(pGenerator.getKeySpec(), pGenerator);
    }

    /**
     * Reset the cache.
     */
    public void resetCache() {
        theSymKeyCache.clear();
        theStreamKeyCache.clear();
        theMacKeyCache.clear();
        theKeyPairCache.clear();
    }
}
