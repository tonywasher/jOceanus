/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBasePasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Test SecurityManager functionality.
 */
class PasswordManagerTest {
    /**
     * The List of password names.
     */
    static final String[] NAMES = {
            "First",
            "Second",
            "Third",
            "Fourth",
            "Fifth",
            "Last",
    };

    /**
     * The index of the unknown password.
     */
    static int UNKNOWN = NAMES.length - 1;

    /**
     * The List of passwords.
     */
    static final char[][] PASSWORDS = {
            "Password1".toCharArray(),
            "Password2".toCharArray(),
            "Password3".toCharArray(),
            "Password4".toCharArray(),
            "Password5".toCharArray(),
            "Password6".toCharArray()
    };

    /**
     * PasswordManagerTest Dialog Controller.
     */
    private static class DialogController
            implements GordianDialogController {
        /**
         * Map.
         */
        private final Map<String, char[]> theMap;

        /**
         * Map.
         */
        private final boolean[] theSeen;

        /**
         * The selected password.
         */
        private char[] thePassword;

        /**
         * Constructor.
         */
        DialogController() {
            /* Create the Map */
            theMap = new HashMap<>();
            theSeen = new boolean[PASSWORDS.length];

            /* Populate the map */
            for (int i = 0; i < NAMES.length; i++) {
                theMap.put(NAMES[i], PASSWORDS[i]);
            }
        }

        @Override
        public void createTheDialog(String pTitle,
                                    boolean pNeedConfirm) {
            thePassword = theMap.get(pTitle);
        }

        @Override
        public boolean showTheDialog() {
            return thePassword != null;
        }

        @Override
        public void releaseDialog() {
            thePassword = null;
        }

        @Override
        public char[] getPassword() {
            return thePassword == null ? null : thePassword.clone();
        }

        @Override
        public void reportBadPassword() {
        }

        /**
         * Resolve a hash.
         * @param pManager the security manager
         * @param pHash the hashIndex
         * @throws OceanusException on error
         */
        GordianKeySetHash resolveHash(final GordianBasePasswordManager pManager,
                                      final HashIndex pHash) throws OceanusException {
            final int myIndex = pHash.theIndex;
            final boolean isKnown = myIndex != UNKNOWN;
            final boolean isNew = isKnown && !theSeen[myIndex];
            final String myPrompt = isNew ? NAMES[myIndex] : "";
            theSeen[myIndex] = true;
            return pManager.resolveKeySetHash(pHash.theHash.getHash(), myPrompt);
        }

        /**
         * Resolve a factory.
         * @param pManager the security manager
         * @param pFactory the factoryIndex
         * @throws OceanusException on error
         */
        GordianFactoryLock resolveFactory(final GordianBasePasswordManager pManager,
                                          final FactoryIndex pFactory) throws OceanusException {
            final int myIndex = pFactory.theIndex;
            final boolean isKnown = myIndex != UNKNOWN;
            final boolean isNew = isKnown && !theSeen[myIndex];
            final String myPrompt = isNew ? NAMES[myIndex] : "";
            theSeen[myIndex] = true;
            return pManager.resolveFactoryLock(pFactory.theLock.getLockBytes(), myPrompt);
        }
    }

    /**
     * Hash and index record.
     */
    static class HashIndex {
        /**
         * Hash.
         */
        final GordianKeySetHash theHash;

        /**
         * Index.
         */
        final int theIndex;

        /**
         * Constructor.
         * @param pHash the hash
         * @param pIndex the index
         */
        HashIndex(final GordianKeySetHash pHash,
                  final int pIndex) {
            theHash = pHash;
            theIndex = pIndex;
        }

        /**
         * Should this hash be resolved?
         * @return true/false
         */
        boolean resolved() {
            return theIndex != UNKNOWN;
        }
    }

    /**
     * The list of hashes.
     */
    static final List<HashIndex> HASHES = new ArrayList<>();

    /**
     * Create a new hash for an indexed password.
     * @param pManager the security manager
     * @param pIndex the index of the password
     * @return the new Hash
     * @throws OceanusException on error
     */
    static HashIndex createNewHash(final GordianBasePasswordManager pManager,
                                   final int pIndex) throws OceanusException {
        final HashIndex myHash = new HashIndex(pManager.newKeySetHash(NAMES[pIndex]), pIndex);
        HASHES.add(myHash);
        return myHash;
    }

    /**
     * Create a new hash for an indexed password.
     * @param pManager the security manager
     * @param pHash the hashIndex
     * @return the new Hash
     * @throws OceanusException on error
     */
    static HashIndex createSimilarHash(final GordianBasePasswordManager pManager,
                                       final HashIndex pHash) throws OceanusException {
        final HashIndex myHash = new HashIndex(pManager.similarKeySetHash(pHash.theHash), pHash.theIndex);
        HASHES.add(myHash);
        return myHash;
    }

    /**
     * Set up the hashes.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void setUpHashes() throws OceanusException {
        /* Create the security manager */
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final GordianBasePasswordManager myManager = new GordianBasePasswordManager(myFactory, new DialogController());

        /* For each NAME */
        for (int i = 0; i < NAMES.length; i++) {
            /* Create some hashes */
            createNewHash(myManager, i);
            createNewHash(myManager, i);
            createNewHash(myManager, i);
            final HashIndex myHash = createNewHash(myManager, i);

            /* Create a couple of similar hashes */
            createSimilarHash(myManager, myHash);
        }

        /* Shuffle the hashes */
        Collections.shuffle(HASHES);
    }

    /**
     * Resolve the hashes.
     * @throws OceanusException on error
     */
    @Test
    void HashPasswordTests() throws OceanusException {
        /* Create the security manager */
        final DialogController myController = new DialogController();
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final GordianBasePasswordManager myManager = new GordianBasePasswordManager(myFactory, myController);

        /* Loop through the hashes in the list */
        for (HashIndex myHash : HASHES) {
            /* Resolve the hash */
            if (myHash.resolved()) {
                final GordianKeySetHash myResolved = myController.resolveHash(myManager, myHash);
                Assertions.assertEquals(myHash.theHash, myResolved, "Incorrect hash");
            } else {
                Assertions.assertThrows(GordianDataException.class,
                        () -> myController.resolveHash(myManager, myHash), "Resolution failure");
            }
        }
    }


    /**
     * Factory and index record.
     */
    static class FactoryIndex {
        /**
         * Hash.
         */
        final GordianFactoryLock theLock;

        /**
         * Index.
         */
        final int theIndex;

        /**
         * Constructor.
         * @param pLock the lock
         * @param pIndex the index
         */
        FactoryIndex(final GordianFactoryLock pLock,
                     final int pIndex) {
            theLock = pLock;
            theIndex = pIndex;
        }

        /**
         * Should this hash be resolved?
         * @return true/false
         */
        boolean resolved() {
            return theIndex != UNKNOWN;
        }
    }

    /**
     * The list of factories.
     */
    static final List<FactoryIndex> FACTORIES = new ArrayList<>();

    /**
     * Create a new hash for an indexed password.
     * @param pManager the security manager
     * @param pIndex the index of the password
     * @return the new Hash
     * @throws OceanusException on error
     */
    static FactoryIndex createNewFactory(final GordianBasePasswordManager pManager,
                                         final int pIndex) throws OceanusException {
        final GordianFactory myFactory = GordianGenerator.createRandomFactory();
        final FactoryIndex myLock = new FactoryIndex(pManager.newFactoryLock(myFactory, NAMES[pIndex]), pIndex);
        FACTORIES.add(myLock);
        return myLock;
    }

    /**
     * Create a new factoryLock for an indexed password.
     * @param pManager the security manager
     * @param pFactory the factoryIndex
     * @return the new factoryLock
     * @throws OceanusException on error
     */
    static FactoryIndex createSimilarFactory(final GordianBasePasswordManager pManager,
                                             final FactoryIndex pFactory) throws OceanusException {
        final FactoryIndex myFactory = new FactoryIndex(pManager.similarFactoryLock(pFactory.theLock), pFactory.theIndex);
        FACTORIES.add(myFactory);
        return myFactory;
    }

    /**
     * Set up the hashes.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void setUpFactories() throws OceanusException {
        /* Create the security manager */
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final GordianBasePasswordManager myManager = new GordianBasePasswordManager(myFactory, new DialogController());

        /* For each NAME */
        for (int i = 0; i < NAMES.length; i++) {
            /* Create some hashes */
            createNewFactory(myManager, i);
            createNewFactory(myManager, i);
            createNewFactory(myManager, i);
            final FactoryIndex myIndex = createNewFactory(myManager, i);

            /* Create a couple of similar hashes */
            createSimilarFactory(myManager, myIndex);
        }

        /* Shuffle the hashes */
        Collections.shuffle(FACTORIES);
    }

    /**
     * Resolve the hashes.
     * @throws OceanusException on error
     */
    @Test
    void FactoryPasswordTests() throws OceanusException {
        /* Create the security manager */
        final DialogController myController = new DialogController();
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final GordianBasePasswordManager myManager = new GordianBasePasswordManager(myFactory, myController);

        /* Loop through the factories in the list */
        for (FactoryIndex myIndex : FACTORIES) {
            /* Resolve the hash */
            if (myIndex.resolved()) {
                final GordianFactoryLock myLock = myController.resolveFactory(myManager, myIndex);
                Assertions.assertEquals(myIndex.theLock, myLock, "Incorrect lock");
            } else {
                Assertions.assertThrows(GordianDataException.class,
                        () -> myController.resolveFactory(myManager, myIndex), "Resolution failure");
            }
        }
    }
}
