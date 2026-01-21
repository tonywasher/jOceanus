/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

/**
 * KeyStore Symmetric Tests.
 */
public final class KeyStoreSymmetric {
    /**
     * The testKey length.
     */
    private static final GordianLength KEYLEN = GordianLength.LEN_256;

    /**
     * The KeySetSpec.
     */
    static final GordianKeySetSpec KEYSETSPEC = new GordianKeySetSpec(KEYLEN);

    /**
     * The KeyStore Manager.
     */
    private final GordianKeyStoreManager theManager;

    /**
     * The KeyStore.
     */
    private final GordianCoreKeyStore theStore;

    /**
     * The KeyStore Gateway.
     */
    private final GordianKeyStoreGateway theGateway;

    /**
     * Private constructor.
     *
     * @param pManager the manager
     */
    KeyStoreSymmetric(final GordianKeyStoreManager pManager) {
        theManager = pManager;
        theStore = (GordianCoreKeyStore) theManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = theStore.getFactory().getAsyncFactory().getKeyStoreFactory();
        theGateway = myFactory.createKeyStoreGateway(theManager);
    }

    /**
     * create symmetric test stream.
     *
     * @return the test stream
     */
    DynamicNode symmetricTest() {
        /* Create tests */
        return DynamicContainer.dynamicContainer("symmetric", Stream.of(
                testSymmetricKeySet(),
                testSymmetricKey(KeyStoreAlias.SYMKEY, GordianSymKeySpecBuilder.aes(KEYLEN)),
                testSymmetricKey(KeyStoreAlias.STREAMKEY, GordianStreamKeySpecBuilder.hc(KEYLEN)),
                testSymmetricKey(KeyStoreAlias.MACKEY, GordianMacSpecBuilder.vmpcMac(KEYLEN)),
                KeyStoreUtils.testKeyStoreSave(theStore),
                testSymmetricCleanup()
        ));
    }

    /**
     * Create a symmetricKeySet Test.
     *
     * @return the test
     */
    private DynamicNode testSymmetricKeySet() {
        return DynamicTest.dynamicTest(KeyStoreAlias.KEYSET.getName(), () -> {
            final GordianKeyStoreSet mySet = theManager.createKeySet(KEYSETSPEC, KeyStoreAlias.KEYSET.getName(), KeyStoreUtils.DEF_PASSWORD);
            checkKeySet(mySet);
        });
    }

    /**
     * Create a symmetricKey test.
     *
     * @param pAlias   the Alias
     * @param pKeySpec the keySpec
     * @return the test
     */
    private <K extends GordianKeySpec> DynamicNode testSymmetricKey(final KeyStoreAlias pAlias,
                                                                    final K pKeySpec) {
        return DynamicTest.dynamicTest(pAlias.getName(), () -> {
            final GordianKeyStoreKey<K> myKey = theManager.createKey(pKeySpec, pAlias.getName(), KeyStoreUtils.DEF_PASSWORD);
            checkKey(pAlias, myKey);
        });
    }

    /**
     * Create cleanup Symmetric test.
     *
     * @return the test
     */
    private DynamicNode testSymmetricCleanup() {
        return DynamicTest.dynamicTest("Cleanup", () -> {
            /* delete the entries */
            theStore.deleteEntry(KeyStoreAlias.KEYSET.getName());
            theStore.deleteEntry(KeyStoreAlias.SYMKEY.getName());
            theStore.deleteEntry(KeyStoreAlias.STREAMKEY.getName());
            theStore.deleteEntry(KeyStoreAlias.MACKEY.getName());

            /* Check that we have deleted all values */
            int mySize = theStore.size();
            Assertions.assertEquals(0, mySize);
        });
    }

    /**
     * check keySet.
     *
     * @param pKeySet the keySet
     * @throws GordianException on error
     */
    private void checkKeySet(final GordianKeyStoreSet pKeySet) throws GordianException {
        final String myName = KeyStoreAlias.KEYSET.getName();
        Assertions.assertTrue(theStore.containsAlias(myName));
        Assertions.assertTrue(theStore.isKeySetEntry(myName));
        Assertions.assertTrue(theStore.entryInstanceOf(myName, GordianKeyStoreSet.class));
        Assertions.assertEquals(pKeySet.getKeySet(), theStore.getKeySet(myName, KeyStoreUtils.DEF_PASSWORD));
        Assertions.assertEquals(pKeySet, theStore.getEntry(myName, KeyStoreUtils.DEF_PASSWORD));
        KeyStoreUtils.checkExport(theGateway, KeyStoreAlias.KEYSET, pKeySet);
    }

    /**
     * check key.
     *
     * @param pAlias the alias
     * @param pKey   the key
     * @throws GordianException on error
     */
    private void checkKey(final KeyStoreAlias pAlias,
                          final GordianKeyStoreKey<?> pKey) throws GordianException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(theStore.containsAlias(myName));
        Assertions.assertTrue(theStore.isKeyEntry(myName));
        Assertions.assertTrue(theStore.entryInstanceOf(myName, GordianKeyStoreKey.class));
        Assertions.assertEquals(pKey.getKey(), theStore.getKey(myName, KeyStoreUtils.DEF_PASSWORD));
        Assertions.assertEquals(pKey, theStore.getEntry(myName, KeyStoreUtils.DEF_PASSWORD));
        KeyStoreUtils.checkExport(theGateway, pAlias, pKey);
    }
}
