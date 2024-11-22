package net.sourceforge.joceanus.prometheus.junit;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityDialogController;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            implements PrometheusSecurityDialogController {
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
         * Resolve a factory.
         * @param pManager the security manager
         * @param pFactory the factoryIndex
         * @throws OceanusException on error
         */
        GordianFactoryLock resolveFactory(final PrometheusSecurityPasswordManager pManager,
                                          final FactoryIndex pFactory) throws OceanusException {
            final int myIndex = pFactory.theIndex;
            final boolean isKnown = myIndex != UNKNOWN;
            final boolean isNew = isKnown && !theSeen[myIndex];
            final String myPrompt = isNew ? NAMES[myIndex] : "";
            theSeen[myIndex] = true;
            return pManager.resolveFactoryLock(pFactory.theLock.getLockBytes(), myPrompt);
        }

        /**
         * Resolve a keySet.
         * @param pManager the security manager
         * @param pKeySet the keySetIndex
         * @throws OceanusException on error
         */
        GordianKeySetLock resolveKeySet(final PrometheusSecurityPasswordManager pManager,
                                        final KeySetIndex pKeySet) throws OceanusException {
            final int myIndex = pKeySet.theIndex;
            final boolean isKnown = myIndex != UNKNOWN;
            final boolean isNew = isKnown && !theSeen[myIndex];
            final String myPrompt = isNew ? NAMES[myIndex] : "";
            theSeen[myIndex] = true;
            return pManager.resolveKeySetLock(pKeySet.theLock.getLockBytes(), myPrompt);
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
         * Should this lock be resolved?
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
     * Create a new factory for an indexed password.
     * @param pManager the security manager
     * @param pIndex the index of the password
     * @return the new factory
     * @throws OceanusException on error
     */
    static FactoryIndex createNewFactory(final PrometheusSecurityPasswordManager pManager,
                                         final int pIndex) throws OceanusException {
        final FactoryIndex myLock = new FactoryIndex(pManager.newFactoryLock(NAMES[pIndex]), pIndex);
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
    static FactoryIndex createSimilarFactory(final PrometheusSecurityPasswordManager pManager,
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
        final PrometheusSecurityPasswordManager myManager = new PrometheusSecurityPasswordManager(myFactory, new DialogController());

        /* For each NAME */
        for (int i = 0; i < NAMES.length; i++) {
            /* Create some factories */
            createNewFactory(myManager, i);
            createNewFactory(myManager, i);
            createNewFactory(myManager, i);
            final FactoryIndex myIndex = createNewFactory(myManager, i);

            /* Create a couple of similar factories */
            createSimilarFactory(myManager, myIndex);
        }

        /* Shuffle the hashes */
        Collections.shuffle(FACTORIES);
    }

    /**
     * Resolve the factories.
     * @throws OceanusException on error
     */
    @Test
    void FactoryPasswordTests() throws OceanusException {
        /* Create the security manager */
        final DialogController myController = new DialogController();
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final PrometheusSecurityPasswordManager myManager = new PrometheusSecurityPasswordManager(myFactory, myController);

        /* Loop through the factories in the list */
        for (FactoryIndex myIndex : FACTORIES) {
            /* Resolve the factory */
            if (myIndex.resolved()) {
                final GordianFactoryLock myLock = myController.resolveFactory(myManager, myIndex);
                Assertions.assertEquals(myIndex.theLock, myLock, "Incorrect lock");
            } else {
                Assertions.assertThrows(PrometheusDataException.class,
                        () -> myController.resolveFactory(myManager, myIndex), "Resolution failure");
            }
        }
    }

    /**
     * KeySet and index record.
     */
    static class KeySetIndex {
        /**
         * Hash.
         */
        final GordianKeySetLock theLock;

        /**
         * Index.
         */
        final int theIndex;

        /**
         * Constructor.
         * @param pLock the lock
         * @param pIndex the index
         */
        KeySetIndex(final GordianKeySetLock pLock,
                    final int pIndex) {
            theLock = pLock;
            theIndex = pIndex;
        }

        /**
         * Should this lock be resolved?
         * @return true/false
         */
        boolean resolved() {
            return theIndex != UNKNOWN;
        }
    }

    /**
     * The list of keySets.
     */
    static final List<KeySetIndex> KEYSETS = new ArrayList<>();

    /**
     * Create a new keySet for an indexed password.
     * @param pManager the security manager
     * @param pIndex the index of the password
     * @return the new keySet
     * @throws OceanusException on error
     */
    static KeySetIndex createNewKeySet(final PrometheusSecurityPasswordManager pManager,
                                       final int pIndex) throws OceanusException {
        final KeySetIndex myLock = new KeySetIndex(pManager.newKeySetLock(NAMES[pIndex]), pIndex);
        KEYSETS.add(myLock);
        return myLock;
    }

    /**
     * Create a new keySetLock for an indexed password.
     * @param pManager the security manager
     * @param pKeySet the keySetIndex
     * @return the new keySetLock
     * @throws OceanusException on error
     */
    static KeySetIndex createSimilarKeySet(final PrometheusSecurityPasswordManager pManager,
                                           final KeySetIndex pKeySet) throws OceanusException {
        final KeySetIndex myKeySet = new KeySetIndex(pManager.similarKeySetLock(pKeySet.theLock), pKeySet.theIndex);
        KEYSETS.add(myKeySet);
        return myKeySet;
    }

    /**
     * Set up the keySets.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void setUpKeySets() throws OceanusException {
        /* Create the security manager */
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final PrometheusSecurityPasswordManager myManager = new PrometheusSecurityPasswordManager(myFactory, new DialogController());

        /* For each NAME */
        for (int i = 0; i < NAMES.length; i++) {
            /* Create some keySets */
            createNewKeySet(myManager, i);
            createNewKeySet(myManager, i);
            createNewKeySet(myManager, i);
            final KeySetIndex myIndex = createNewKeySet(myManager, i);

            /* Create a couple of similar keySets */
            createSimilarKeySet(myManager, myIndex);
        }

        /* Shuffle the hashes */
        Collections.shuffle(FACTORIES);
    }

    /**
     * Resolve the factories.
     * @throws OceanusException on error
     */
    @Test
    void KeySetPasswordTests() throws OceanusException {
        /* Create the security manager */
        final DialogController myController = new DialogController();
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final PrometheusSecurityPasswordManager myManager = new PrometheusSecurityPasswordManager(myFactory, myController);

        /* Loop through the keySets in the list */
        for (KeySetIndex myIndex : KEYSETS) {
            /* Resolve the hash */
            if (myIndex.resolved()) {
                final GordianKeySetLock myLock = myController.resolveKeySet(myManager, myIndex);
                Assertions.assertEquals(myIndex.theLock, myLock, "Incorrect lock");
            } else {
                Assertions.assertThrows(PrometheusDataException.class,
                        () -> myController.resolveKeySet(myManager, myIndex), "Resolution failure");
            }
        }
    }
}
