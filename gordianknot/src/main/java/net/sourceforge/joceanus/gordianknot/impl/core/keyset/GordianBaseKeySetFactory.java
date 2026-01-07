/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;

/**
 * Base KeySetFactory interface.
 */
public interface GordianBaseKeySetFactory
        extends GordianKeySetFactory {
    /**
     * create an empty keySet.
     * @param pSpec the keySetSpec
     * @return the empty keySedt
     * @throws GordianException on error
     */
    GordianBaseKeySet createKeySet(GordianKeySetSpec pSpec) throws GordianException;
}
