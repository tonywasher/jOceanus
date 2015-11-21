/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGeneratorCache;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;

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
        if (pKeyType instanceof GordianSymKeyType) {
            return (JcaKeyGenerator<T>) getCachedSymKeyGenerator((GordianSymKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianStreamKeyType) {
            return (JcaKeyGenerator<T>) getCachedStreamKeyGenerator((GordianStreamKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianMacSpec) {
            return (JcaKeyGenerator<T>) getCachedMacKeyGenerator((GordianMacSpec) pKeyType);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Cache KeyGenerator.
     * @param <T> the keyType class
     * @param pGenerator the generator
     */
    @SuppressWarnings("unchecked")
    protected <T> void cacheKeyGenerator(final JcaKeyGenerator<T> pGenerator) {
        T myKeyType = pGenerator.getKeyType();
        if (myKeyType instanceof GordianSymKeyType) {
            addToSymKeyCache((JcaKeyGenerator<GordianSymKeyType>) pGenerator);
        } else if (myKeyType instanceof GordianStreamKeyType) {
            addToStreamKeyCache((JcaKeyGenerator<GordianStreamKeyType>) pGenerator);
        } else if (myKeyType instanceof GordianMacSpec) {
            addToMacKeyCache((JcaKeyGenerator<GordianMacSpec>) pGenerator);
        }
    }
}
