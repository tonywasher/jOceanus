/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * KeyStore Factory implementation.
 */
public class GordianCoreKeyStoreFactory
        implements GordianKeyStoreFactory  {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private final GordianSignatureAlgId theAlgIds;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreKeyStoreFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
        theAlgIds = new GordianSignatureAlgId(pFactory);
    }

    /**
     * Obtain the signature algrithm Ids.
     * @return the signature Algorithm Ids
     */
    public GordianSignatureAlgId getAlgorithmIds() {
        return theAlgIds;
    }

    /**
     * Create a new empty KeyStore.
     * @return the keyStore
     */
    public GordianKeyStore createKeyStore() {
        return new GordianCoreKeyStore(theFactory);
    }
}
