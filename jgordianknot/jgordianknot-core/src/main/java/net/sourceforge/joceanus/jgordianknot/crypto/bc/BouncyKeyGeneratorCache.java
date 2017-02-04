/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGeneratorCache;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;

/**
 * Cache for BouncyCastle Classes.
 */
public class BouncyKeyGeneratorCache
        extends GordianKeyGeneratorCache {
    /**
     * Lookup KeyGenerator in cache.
     * @param <T> the keyType class
     * @param pKeyType the keyType
     * @return the generator (or null)
     */
    @SuppressWarnings("unchecked")
    protected <T> BouncyKeyGenerator<T> getCachedKeyGenerator(final T pKeyType) {
        if (pKeyType instanceof GordianSymKeyType) {
            return (BouncyKeyGenerator<T>) getCachedSymKeyGenerator((GordianSymKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianStreamKeyType) {
            return (BouncyKeyGenerator<T>) getCachedStreamKeyGenerator((GordianStreamKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianMacSpec) {
            return (BouncyKeyGenerator<T>) getCachedMacKeyGenerator((GordianMacSpec) pKeyType);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected BouncyKeyPairGenerator getCachedKeyPairGenerator(final GordianAsymKeySpec pKeySpec) {
        return (BouncyKeyPairGenerator) super.getCachedKeyPairGenerator(pKeySpec);
    }

    /**
     * Cache KeyGenerator.
     * @param <T> the keyType class
     * @param pGenerator the generator
     */
    @SuppressWarnings("unchecked")
    protected <T> void cacheKeyGenerator(final BouncyKeyGenerator<T> pGenerator) {
        T myKeyType = pGenerator.getKeyType();
        if (myKeyType instanceof GordianSymKeyType) {
            addToSymKeyCache((BouncyKeyGenerator<GordianSymKeyType>) pGenerator);
        } else if (myKeyType instanceof GordianStreamKeyType) {
            addToStreamKeyCache((BouncyKeyGenerator<GordianStreamKeyType>) pGenerator);
        } else if (myKeyType instanceof GordianMacSpec) {
            addToMacKeyCache((BouncyKeyGenerator<GordianMacSpec>) pGenerator);
        }
    }

    /**
     * Cache KeyPairGenerator.
     * @param pGenerator the generator
     */
    protected void cacheKeyPairGenerator(final BouncyKeyPairGenerator pGenerator) {
        addToKeyPairCache(pGenerator);
    }
}
