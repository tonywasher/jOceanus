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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.util.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Test SecurityManager functionality.
 */
public class HashManagerTest {
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
     * HashManagerTest Dialog Controller.
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
        public void setError(String pError) {
        }

        /**
         * Resolve a hash.
         * @param pManager the security manager
         * @param pHash the hashIndex
         * @throws OceanusException on error
         */
        GordianKeySetHash resolveHash(final GordianSecurityManager pManager,
                                      final HashIndex pHash) throws OceanusException {
            final int myIndex = pHash.theIndex;
            final boolean isKnown = myIndex != UNKNOWN;
            final boolean isNew = isKnown && !theSeen[myIndex];
            final String myPrompt = isNew ? NAMES[myIndex] : "";
            theSeen[myIndex] = true;
            return pManager.resolveKeySetHash(pHash.theHash.getHash(), myPrompt);
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
    static HashIndex createNewHash(final GordianSecurityManager pManager,
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
    static HashIndex createSimilarHash(final GordianSecurityManager pManager,
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
        final GordianKeySetHashSpec mySpec = new GordianKeySetHashSpec();
        final GordianSecurityManager myManager = new GordianSecurityManager(GordianFactoryType.BC, mySpec, new DialogController());

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
    public void SecurityManagerTest() throws OceanusException {
        /* Create the security manager */
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        final GordianKeySetHashSpec mySpec = new GordianKeySetHashSpec();
        final DialogController myController = new DialogController();
        final GordianSecurityManager myManager = new GordianSecurityManager(GordianFactoryType.BC, mySpec, myController);

        /* Loop through the hashes in the list */
        for (HashIndex myHash : HASHES) {
            /* Resolve the hash */
            if (myHash.resolved()) {
                myController.resolveHash(myManager, myHash);
            } else {
                Assertions.assertThrows(GordianDataException.class,
                        () -> myController.resolveHash(myManager, myHash), "Resolution failure");
            }
        }
    }
}
