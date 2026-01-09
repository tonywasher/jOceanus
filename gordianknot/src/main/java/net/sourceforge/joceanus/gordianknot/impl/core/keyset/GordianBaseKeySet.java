/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;

import java.util.Map;

/**
 * Base KeySet interface.
 */
public interface GordianBaseKeySet
        extends GordianKeySet {
    /**
     * Obtain the factory.
     * @return the factory
     */
    GordianBaseFactory getFactory();

    /**
     * Obtain the symKeySet.
     *
     * @return the keySet
     */
    Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> getSymKeyMap();

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws GordianException on error
     */
    void declareSymKey(GordianKey<GordianSymKeySpec> pKey) throws GordianException;
}
