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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * KeyStore Tests.
 */
class KeyStoreTest {
    /**
     * The KeySetHashSpec.
     */
    private static final GordianPasswordLockSpec KEYSETLOCKSPEC = new GordianPasswordLockSpec(KeyStoreSymmetric.KEYSETSPEC);

    /**
     * Create the keyStore test suite.
     *
     * @return the test stream
     * @throws GordianException on error
     */
    @TestFactory
    Stream<DynamicNode> bouncyCastle() throws GordianException {
        return keyStoreTests(GordianFactoryType.BC);
    }

    /**
     * Create the jca keyStore test suite.
     *
     * @return the test stream
     * @throws GordianException on error
     */
    @TestFactory
    Stream<DynamicNode> jca() throws GordianException {
        return keyStoreTests(GordianFactoryType.JCA);
    }

    /**
     * Create the keySet test suite for a factoryType.
     *
     * @param pFactoryType the factoryType
     * @return the test stream
     * @throws GordianException on error
     */
    private Stream<DynamicNode> keyStoreTests(final GordianFactoryType pFactoryType) throws GordianException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(pFactoryType);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myKSFactory = myFactory.getAsyncFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myKSFactory.createKeyStore(KEYSETLOCKSPEC);
        final GordianKeyStoreManager myMgr = myKSFactory.createKeyStoreManager(myStore);

        /* Return the stream */
        return Stream.of(
                new KeyStoreSymmetric(myMgr).symmetricTest(),
                new KeyStorePairs(myMgr).keyPairsTest(),
                new KeyStoreRequest(myMgr).keyPairRequestTest()
        );
    }
}
