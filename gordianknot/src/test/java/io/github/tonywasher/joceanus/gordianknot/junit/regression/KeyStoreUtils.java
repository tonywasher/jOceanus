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
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStore;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.api.zip.GordianZipLock;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * KeyStore Test Utilities.
 */
public final class KeyStoreUtils {
    /**
     * Default password.
     */
    static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * Private constructor.
     */
    private KeyStoreUtils() {
    }

    /**
     * Create keyStore Save test.
     *
     * @param pStore the keyStore
     * @return the test
     */
    static DynamicNode testKeyStoreSave(final GordianCoreKeyStore pStore) {
        return DynamicTest.dynamicTest("keyStoreSave", () -> {
            /* Create keyStore documents */
            final GordianKeyStoreFactory myFactory = pStore.getFactory().getAsyncFactory().getKeyStoreFactory();
            final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
            pStore.storeToStream(myZipStream, KeyStoreUtils.DEF_PASSWORD);
            final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
            final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, KeyStoreUtils.DEF_PASSWORD);
            Assertions.assertEquals(pStore, myStore2);
        });
    }

    /**
     * check export.
     *
     * @param pGateway the keyStoreGateway
     * @param pAlias   the alias
     * @param pEntry   the entry
     * @throws GordianException on error
     */
    static void checkExport(final GordianKeyStoreGateway pGateway,
                            final KeyStoreAlias pAlias,
                            final GordianKeyStoreEntry pEntry) throws GordianException {
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pGateway.getKeyStore();
        final GordianZipLock myLock = myStore.getFactory().getZipFactory().keySetZipLock(DEF_PASSWORD);
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        pGateway.setPasswordResolver(a -> DEF_PASSWORD.clone());
        pGateway.exportEntry(pAlias.getName(), myOutStream, myLock);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        pGateway.setLockResolver(l -> l.unlock(DEF_PASSWORD));
        Assertions.assertEquals(pEntry, pGateway.importEntry(myInputStream));
    }

    /**
     * Build X500Name.
     *
     * @param pAlias the Alias
     */
    static X500Name buildX500Name(final KeyStoreAlias pAlias) {
        /* Build the name */
        X500NameBuilder myBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        myBuilder.addRDN(BCStyle.CN, pAlias.getName());
        myBuilder.addRDN(BCStyle.OU, "jOceanus development");
        myBuilder.addRDN(BCStyle.O, "jOceanus");
        myBuilder.addRDN(BCStyle.L, "Ampfield");
        myBuilder.addRDN(BCStyle.ST, "HANTS");
        myBuilder.addRDN(BCStyle.C, "UK");
        return myBuilder.build();
    }

    /**
     * Aliases.
     */
    enum KeyStoreAlias {
        /**
         * Root Certificate.
         */
        ROOT("Root Certificate"),

        /**
         * Root Certificate 2.
         */
        ROOT2("Root Certificate 2"),

        /**
         * Root Certificate Alt.
         */
        ROOTALT("Root Certificate Alt"),

        /**
         * Root Certificate 2 Alt.
         */
        ROOTALT2("Root Certificate 2 Alt"),

        /**
         * Intermediate Certificate.
         */
        INTER("Intermediate Certificate"),

        /**
         * Intermediate Certificate 2.
         */
        INTER2("Intermediate Certificate 2"),

        /**
         * Intermediate Certificate Alt.
         */
        INTERALT("Intermediate Certificate Alt"),

        /**
         * Root Certificate.
         */
        INTERALT2("Intermediate Certificate 2 Alt"),

        /**
         * Signing Certificate.
         */
        SIGNER("Signing Certificate"),

        /**
         * Agreement Certificate.
         */
        AGREE("Agreement Certificate"),

        /**
         * Encrypt Certificate.
         */
        ENCRYPT("Encrypt Certificate"),

        /**
         * Certifier Certificate.
         */
        CERTIFIER("Certifier Certificate"),

        /**
         * Target Certificate.
         */
        TARGET("Target Certificate"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

        /**
         * SymKey.
         */
        SYMKEY("SymKey"),

        /**
         * StreamKey.
         */
        STREAMKEY("StreamKey"),

        /**
         * MacKey.
         */
        MACKEY("MacKey");

        /**
         * Name.
         */
        private final String theName;

        /**
         * Constructor.
         *
         * @param pName the name
         */
        KeyStoreAlias(final String pName) {
            theName = pName;
        }

        /**
         * Obtain the name.
         *
         * @return the name
         */
        String getName() {
            return theName;
        }
    }
}
