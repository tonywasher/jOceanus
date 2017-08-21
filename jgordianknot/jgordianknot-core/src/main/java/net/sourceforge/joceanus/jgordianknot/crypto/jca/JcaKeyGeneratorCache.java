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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGeneratorCache;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeySpec;

/**
 * Cache for Jca BouncyCastle Classes.
 */
public class JcaKeyGeneratorCache
        extends GordianKeyGeneratorCache {
    /**
     * Lookup KeyGenerator in cache.
     * @param <T> the keyType class
     * @param pKeyType the keyType
     * @return the generator (or null)
     */
    @SuppressWarnings("unchecked")
    protected <T> JcaKeyGenerator<T> getCachedKeyGenerator(final T pKeyType) {
        if (pKeyType instanceof GordianSymKeySpec) {
            return (JcaKeyGenerator<T>) getCachedSymKeyGenerator((GordianSymKeySpec) pKeyType);
        }
        if (pKeyType instanceof GordianStreamKeyType) {
            return (JcaKeyGenerator<T>) getCachedStreamKeyGenerator((GordianStreamKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianMacSpec) {
            return (JcaKeyGenerator<T>) getCachedMacKeyGenerator((GordianMacSpec) pKeyType);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected JcaKeyPairGenerator getCachedKeyPairGenerator(final GordianAsymKeySpec pKeySpec) {
        return (JcaKeyPairGenerator) super.getCachedKeyPairGenerator(pKeySpec);
    }

    /**
     * Cache KeyGenerator.
     * @param <T> the keyType class
     * @param pGenerator the generator
     */
    @SuppressWarnings("unchecked")
    protected <T> void cacheKeyGenerator(final JcaKeyGenerator<T> pGenerator) {
        final T myKeyType = pGenerator.getKeyType();
        if (myKeyType instanceof GordianSymKeySpec) {
            addToSymKeyCache((JcaKeyGenerator<GordianSymKeySpec>) pGenerator);
        } else if (myKeyType instanceof GordianStreamKeyType) {
            addToStreamKeyCache((JcaKeyGenerator<GordianStreamKeyType>) pGenerator);
        } else if (myKeyType instanceof GordianMacSpec) {
            addToMacKeyCache((JcaKeyGenerator<GordianMacSpec>) pGenerator);
        }
    }

    /**
     * Cache KeyPairGenerator.
     * @param pGenerator the generator
     */
    protected void cacheKeyPairGenerator(final JcaKeyPairGenerator pGenerator) {
        addToKeyPairCache(pGenerator);
    }
}
