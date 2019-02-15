/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keyset;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Hash from which to derive KeySet.
 */
public interface GordianKeySetHash {
    /**
     * Obtain the Hash.
     *
     * @return the Hash
     */
    byte[] getHash();

    /**
     * Get CipherSet.
     *
     * @return the CipherSet
     */
    GordianKeySet getKeySet();

    /**
     * obtain similar keySetHash (same password).
     *
     * @return the similar hash
     * @throws OceanusException on error
     */
    GordianKeySetHash similarHash() throws OceanusException;

    /**
     * obtain child keySetHash (internal password).
     *
     * @return the similar hash
     * @throws OceanusException on error
     */
    GordianKeySetHash childHash() throws OceanusException;

    /**
     * resolve child keySetHash (internal password).
     * @param pHash the hash to resolve
     * @return the similar hash
     * @throws OceanusException on error
     */
    GordianKeySetHash resolveChildHash(final byte[] pHash) throws OceanusException;
}
