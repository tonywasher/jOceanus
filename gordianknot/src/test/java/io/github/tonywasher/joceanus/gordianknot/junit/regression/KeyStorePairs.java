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
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDSAElliptic;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStore;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

/**
 * KeyStore Pairs Tests.
 */
public final class KeyStorePairs {
    /**
     * KeyPairSpec.
     */
    private static final GordianKeyPairSpec KEYPAIRSPEC = GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1);

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
    KeyStorePairs(final GordianKeyStoreManager pManager) {
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
    DynamicNode keyPairsTest() {
        /* Create tests */
        return DynamicContainer.dynamicContainer("keyPairs", Stream.of(
                testKeyPairRoot(KeyStoreAlias.ROOT),
                testKeyPairRoot(KeyStoreAlias.ROOT2),
                testKeyPairAlternate(KeyStoreAlias.ROOT, KeyStoreAlias.ROOT2, KeyStoreAlias.ROOTALT),
                testKeyPairAlternate(KeyStoreAlias.ROOT2, KeyStoreAlias.ROOT, KeyStoreAlias.ROOTALT2),
                testKeyPairCreate(KeyStoreAlias.ROOT, KeyStoreAlias.INTER, new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE)),
                testKeyPairCreate(KeyStoreAlias.ROOT2, KeyStoreAlias.INTER2, new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE)),
                testKeyPairAlternate(KeyStoreAlias.INTER, KeyStoreAlias.ROOT2, KeyStoreAlias.INTERALT),
                testKeyPairAlternate(KeyStoreAlias.INTER2, KeyStoreAlias.ROOT, KeyStoreAlias.INTERALT2),
                testKeyPairCreate(KeyStoreAlias.INTER, KeyStoreAlias.SIGNER, new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE)),
                testKeyPairCreate(KeyStoreAlias.INTER, KeyStoreAlias.AGREE, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT)),
                testKeyPairCreate(KeyStoreAlias.INTER, KeyStoreAlias.ENCRYPT, new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT)),
                KeyStoreUtils.testKeyStoreSave(theStore),
                testKeyPairCleanup()
        ));
    }

    /**
     * Create keyPairAlternate test.
     *
     * @param pAlias the alias
     * @return the test
     */
    private DynamicNode testKeyPairRoot(final KeyStoreAlias pAlias) {
        return DynamicTest.dynamicTest(pAlias.getName(), () -> {
            /* Create a keyPair */
            final X500Name myName = KeyStoreUtils.buildX500Name(pAlias);
            final GordianKeyStorePair myKeyPair = theManager.createRootKeyPair(KEYPAIRSPEC, myName, pAlias.getName(), KeyStoreUtils.DEF_PASSWORD);
            checkKeyPair(theGateway, pAlias, myKeyPair);
        });
    }

    /**
     * Create keyPairCreate test.
     *
     * @param pSigner the signer alias
     * @param pAlias  the alias
     * @param pUsage  the usage of the keyPair
     * @return the test
     */
    private DynamicNode testKeyPairCreate(final KeyStoreAlias pSigner,
                                          final KeyStoreAlias pAlias,
                                          final GordianKeyPairUsage pUsage) {
        return DynamicTest.dynamicTest(pAlias.getName(), () -> {
            /* Create a keyPair */
            final GordianKeyStorePair mySigner = (GordianKeyStorePair) theStore.getEntry(pSigner.getName(), KeyStoreUtils.DEF_PASSWORD);
            final X500Name myName = KeyStoreUtils.buildX500Name(pAlias);
            final GordianKeyStorePair myKeyPair = theManager.createKeyPair(KEYPAIRSPEC, myName, pUsage, mySigner, pAlias.getName(), KeyStoreUtils.DEF_PASSWORD);
            checkKeyPair(theGateway, pAlias, myKeyPair);
        });
    }

    /**
     * Create keyPairAlternate test.
     *
     * @param pBase   the base alias
     * @param pSigner the signer alias
     * @param pAlias  the alias
     * @return the test
     */
    private DynamicNode testKeyPairAlternate(final KeyStoreAlias pBase,
                                             final KeyStoreAlias pSigner,
                                             final KeyStoreAlias pAlias) {
        return DynamicTest.dynamicTest(pAlias.getName(), () -> {
            /* Create a keyPair */
            final GordianKeyStorePair myBase = (GordianKeyStorePair) theStore.getEntry(pBase.getName(), KeyStoreUtils.DEF_PASSWORD);
            final GordianKeyStorePair mySigner = (GordianKeyStorePair) theStore.getEntry(pSigner.getName(), KeyStoreUtils.DEF_PASSWORD);
            final GordianKeyStorePair myKeyPair = theManager.createAlternate(myBase, mySigner, pAlias.getName(), KeyStoreUtils.DEF_PASSWORD);
            checkKeyPair(theGateway, pAlias, myKeyPair);
        });
    }

    /**
     * Create cleanup Pairs test.
     *
     * @return the test
     */
    private DynamicNode testKeyPairCleanup() {
        return DynamicTest.dynamicTest("Cleanup", () -> {
            /* delete the entries */
            theStore.deleteEntry(KeyStoreAlias.ROOT.getName());
            theStore.deleteEntry(KeyStoreAlias.ROOT2.getName());
            theStore.deleteEntry(KeyStoreAlias.ROOTALT.getName());
            theStore.deleteEntry(KeyStoreAlias.ROOTALT2.getName());
            theStore.deleteEntry(KeyStoreAlias.INTER.getName());
            theStore.deleteEntry(KeyStoreAlias.INTER2.getName());
            theStore.deleteEntry(KeyStoreAlias.INTERALT.getName());
            theStore.deleteEntry(KeyStoreAlias.INTERALT2.getName());
            theStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
            theStore.deleteEntry(KeyStoreAlias.AGREE.getName());
            theStore.deleteEntry(KeyStoreAlias.ENCRYPT.getName());

            /* Check that we have deleted all values */
            int mySize = theStore.size();
            Assertions.assertEquals(0, mySize);
        });
    }

    /**
     * check keyPair.
     *
     * @param pGateway the keyStoreGateway
     * @param pAlias   the alias
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    static void checkKeyPair(final GordianKeyStoreGateway pGateway,
                             final KeyStoreAlias pAlias,
                             final GordianKeyStorePair pKeyPair) throws GordianException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair.getKeyPair(), myStore.getKeyPair(myName, KeyStoreUtils.DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair.getCertificateChain(), myStore.getCertificateChain(myName));
        Assertions.assertEquals(pKeyPair, myStore.getEntry(myName, KeyStoreUtils.DEF_PASSWORD));
        KeyStoreUtils.checkExport(pGateway, pAlias, pKeyPair);
    }
}
