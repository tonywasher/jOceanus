/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.base;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianDestroyable;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;

/**
 * Base destroyable interface.
 */
public interface GordianBaseDestroyable
        extends GordianDestroyable {
    /**
     * Check for destroyed key.
     *
     * @throws GordianException if key has been destroyed
     */
    default void checkForDestroyedKey() throws GordianException {
        checkForDestroyed("Key");
    }

    /**
     * Check for destroyed keySet.
     *
     * @throws GordianException if keySet has been destroyed
     */
    default void checkForDestroyedKeySet() throws GordianException {
        checkForDestroyed("KeySet");
    }

    /**
     * Check for destroyed object.
     * /**
     * Check for destroyed keyPair.
     *
     * @throws GordianException if keyPair has been destroyed
     */
    default void checkForDestroyedKeyPair() throws GordianException {
        checkForDestroyed("KeyPair");
    }

    /**
     * Check for destroyed object.
     *
     * @param pName the name of the destroyed object.
     * @throws GordianException if key has been destroyed
     */
    void checkForDestroyed(String pName) throws GordianException;
}
