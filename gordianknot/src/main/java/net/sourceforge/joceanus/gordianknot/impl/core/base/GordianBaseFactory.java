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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Base factory.
 */
public interface GordianBaseFactory
    extends GordianFactory, GordianFactoryGenerator, GordianBaseSupplier {
    /**
     * CreateKeySet interface.
     */
    interface GordianKeySetGenerate {
        /**
         * create and build a keySet from seed.
         * @param pSeed the base seed
         * @return the keySet
         * @throws GordianException on error
         */
        GordianKeySet generateKeySet(byte[] pSeed) throws GordianException;
    }
    /**
     * Is this a random factory?
     * @return true/false
     */
    boolean isRandom();

    /**
     * Obtain the personalization.
     * @return the personalization
     */
    GordianPersonalisation getPersonalisation();

    /**
     * Obtain the idManager.
     * @return the idManager
     */
    GordianIdManager getIdManager();

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public GordianParameters getParameters();

    /**
     * Obtain Identifier for keySpec.
     * @param pSpec the keySpec.
     * @return the Identifier
     */
    AlgorithmIdentifier getIdentifierForSpec(GordianKeySpec pSpec);

    /**
     * Obtain keySpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the keySpec (or null if not found)
     */
    GordianKeySpec getKeySpecForIdentifier(AlgorithmIdentifier pIdentifier);

    /**
     * Obtain Identifier for DigestSpec.
     * @param pSpec the digestSpec.
     * @return the Identifier
     */
    AlgorithmIdentifier getIdentifierForSpec(GordianDigestSpec pSpec);
    /**
     * Obtain DigestSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the digestSpec (or null if not found)
     */
    GordianDigestSpec getDigestSpecForIdentifier(AlgorithmIdentifier pIdentifier);
}
