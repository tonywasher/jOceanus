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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeyPairLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianLock;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetHash;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Password Cache.
 */
public class GordianPasswordCache {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianPasswordCache.class);

    /**
     * Password failed message.
     */
    private static final String PASSWORD_FAIL = "Password attempt failed";

    /**
     * List of resolved password hashes.
     */
    private final List<GordianKeySetHashCache> theHashes;

    /**
     * List of resolved Locks.
     */
    private final List<GordianLockCache<?>> theLocks;

    /**
     * List of successful passwords.
     */
    private final List<ByteBuffer> thePasswords;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * PasswordLockSpec.
     */
    private final GordianPasswordLockSpec theLockSpec;

    /**
     * Local keySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pManager the password manager
     * @param pLockSpec the passwordLockSpec
     * @throws OceanusException on error
     */
    GordianPasswordCache(final GordianPasswordManager pManager,
                         final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        /* Store factory and lockSpec*/
        theFactory = pManager.getSecurityFactory();
        theLockSpec = pLockSpec;

        /* Create a keySet */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        theKeySet = myFactory.generateKeySet(pLockSpec.getKeySetSpec());

        /* Create the lists */
        theHashes = new ArrayList<>();
        theLocks = new ArrayList<>();
        thePasswords = new ArrayList<>();
    }

    /**
     * Add resolved Hash to cache.
     * @param pHash the resolved Hash
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void addResolvedHash(final GordianCoreKeySetHash pHash,
                         final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theHashes.add(new GordianKeySetHashCache(pHash, myBuffer));
            thePasswords.add(myBuffer);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * Add resolved factoryLock to cache.
     * @param pFactory the resolved FactoryLock
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void addResolvedFactory(final GordianFactoryLock pFactory,
                            final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new GordianLockCache<>(pFactory, myBuffer));
            thePasswords.add(myBuffer);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * Add resolved keySetLock to cache.
     * @param pKeySet the resolved keySetLock
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void addResolvedKeySet(final GordianKeySetLock pKeySet,
                           final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new GordianLockCache<>(pKeySet, myBuffer));
            thePasswords.add(myBuffer);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * Add resolved keyPairLock to cache.
     * @param pKeyPair the resolved keyPairLock
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void addResolvedKeyPair(final GordianKeyPairLock pKeyPair,
                            final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new GordianLockCache<>(pKeyPair, myBuffer));
            thePasswords.add(myBuffer);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * Add resolved Password to cache.
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void addResolvedPassword(final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            thePasswords.add(myBuffer);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * LookUp previously resolved KeySetHash.
     * @param pHashBytes the HashBytes to search for
     * @return the previous PasswordHash if found, otherwise null
     */
    GordianCoreKeySetHash lookUpResolvedHash(final byte[] pHashBytes) {
        /* Look for the hash in the list */
        for (GordianKeySetHashCache myCurr : theHashes) {
            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(pHashBytes, myCurr.getHash().getHash())) {
                return myCurr.getHash();
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * LookUp previously resolved Factory.
     * @param pLockBytes the LockBytes to search for
     * @return the previous factoryLock if found, otherwise null
     */
    GordianFactoryLock lookUpResolvedFactoryLock(final byte[] pLockBytes) {
        /* Look for the hash in the list */
        for (GordianLockCache<?> myCurr : theLocks) {
            /* If this is the factoryLock we are looking for, return it */
            if (myCurr.getLock() instanceof GordianFactoryLock
                    && Arrays.equals(pLockBytes, myCurr.getLock().getLockBytes())) {
                return (GordianFactoryLock) myCurr.getLock();
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * LookUp previously resolved keySet.
     * @param pLockBytes the LockBytes to search for
     * @return the previous keySetLock if found, otherwise null
     */
    GordianKeySetLock lookUpResolvedKeySetLock(final byte[] pLockBytes) {
        /* Look for the hash in the list */
        for (GordianLockCache<?> myCurr : theLocks) {
            /* If this is the keySetLock we are looking for, return it */
            if (myCurr.getLock() instanceof GordianKeySetLock
                    && Arrays.equals(pLockBytes, myCurr.getLock().getLockBytes())) {
                return (GordianKeySetLock) myCurr.getLock();
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * LookUp previously resolved keySet.
     * @param pLockBytes the LockBytes to search for
     * @param pKeyPair the keyPair
     * @return the previous keySetLock if found, otherwise null
     */
    GordianKeyPairLock lookUpResolvedKeyPairLock(final byte[] pLockBytes,
                                                 final GordianKeyPair pKeyPair) {
        /* Look for the hash in the list */
        for (GordianLockCache<?> myCurr : theLocks) {
            /* If this is the keyPairLock we are looking for, return it */
            if (myCurr.getLock() instanceof GordianKeyPairLock
                    && Arrays.equals(pLockBytes, myCurr.getLock().getLockBytes())) {
                return (GordianKeyPairLock) myCurr.getLock();
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * LookUp previously resolved Password.
     * @param pReference the Reference to search for
     * @return the encrypted password
     * @throws OceanusException on error
     */
    ByteBuffer lookUpResolvedPassword(final Object pReference) throws OceanusException {
        /* If the reference is a keySetHash */
        if (pReference instanceof GordianKeySetHash) {
            /* Look for the hash in the list */
            final GordianKeySetHash myReference = (GordianKeySetHash) pReference;
            final byte[] myHashBytes = myReference.getHash();
            for (GordianKeySetHashCache myCurr : theHashes) {
                /* If this is the hash we are looking for, return it */
                if (Arrays.equals(myHashBytes, myCurr.getHash().getHash())) {
                    return myCurr.getPassword();
                }
            }
        }

        /* If the reference is a lock */
        if (pReference instanceof GordianLock) {
            /* Look for the lock in the list */
            final GordianLock<?> myReference = (GordianLock<?>) pReference;
            for (GordianLockCache<?> myCurr : theLocks) {
                /* If this is the lock are looking for, return it */
                if (Objects.equals(myReference, myCurr.getLock())) {
                    return myCurr.getPassword();
                }
            }
        }

        /* Throw error */
        throw new GordianDataException("Referenced Object not known");
    }

    /**
     * Attempt known passwords for hash.
     * @param pHashBytes the HashBytes to attempt passwords for
     * @return the new PasswordHash if successful, otherwise null
     */
    GordianCoreKeySetHash attemptKnownPasswordsForHash(final byte[] pHashBytes) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            final GordianCoreKeySetHash myHash = (GordianCoreKeySetHash) attemptPasswordForHash(pHashBytes, myCurr.array());

            /* If we succeeded */
            if (myHash != null) {
                /* Add the hash to the list and return it */
                theHashes.add(new GordianKeySetHashCache(myHash, myCurr));
                return myHash;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Attempt the cached password against the passed hash.
     * @param pHashBytes the Hash to test against
     * @param pPassword the encrypted password
     * @return the new PasswordHash if successful, otherwise null
     */
    private GordianKeySetHash attemptPasswordForHash(final byte[] pHashBytes,
                                                     final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the hash and return it */
            final GordianKeySetFactory myKeySets = theFactory.getKeySetFactory();
            return myKeySets.deriveKeySetHash(pHashBytes, myPasswordChars);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return null;

        } catch (GordianBadCredentialsException e) {
            return null;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Attempt known passwords for factory lock.
     * @param pLockBytes the lockBytes to attempt passwords for
     * @return the new FactoryLock if successful, otherwise null
     */
    GordianFactoryLock attemptKnownPasswordsForFactoryLock(final byte[] pLockBytes) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            final GordianFactoryLock myFactory = attemptPasswordForFactoryLock(pLockBytes, myCurr.array());

            /* If we succeeded */
            if (myFactory != null) {
                /* Add the factory to the list and return it */
                theLocks.add(new GordianLockCache<>(myFactory, myCurr));
                return myFactory;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pLockBytes the Lock to test against
     * @param pPassword the encrypted password
     * @return the new FactoryLock if successful, otherwise null
     */
    private GordianFactoryLock attemptPasswordForFactoryLock(final byte[] pLockBytes,
                                                             final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return GordianBuilder.resolveFactoryLock(pLockBytes, myPasswordChars);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return null;

        } catch (GordianBadCredentialsException e) {
            return null;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Attempt known passwords for keySet lock.
     * @param pLockBytes the lockBytes to attempt passwords for
     * @return the new keySetLock if successful, otherwise null
     */
    GordianKeySetLock attemptKnownPasswordsForKeySetLock(final byte[] pLockBytes) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            final GordianKeySetLock myKeySet = attemptPasswordForKeySetLock(pLockBytes, myCurr.array());

            /* If we succeeded */
            if (myKeySet != null) {
                /* Add the factory to the list and return it */
                theLocks.add(new GordianLockCache<>(myKeySet, myCurr));
                return myKeySet;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pLockBytes the Lock to test against
     * @param pPassword the encrypted password
     * @return the new keySetLock if successful, otherwise null
     */
    private GordianKeySetLock attemptPasswordForKeySetLock(final byte[] pLockBytes,
                                                           final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return GordianBuilder.resolveKeySetLock(theFactory, pLockBytes, myPasswordChars);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return null;

        } catch (GordianBadCredentialsException e) {
            return null;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Attempt known passwords for keyPair lock.
     * @param pLockBytes the lockBytes to attempt passwords for
     * @return the new keyPairLock if successful, otherwise null
     */
    GordianKeyPairLock attemptKnownPasswordsForKeyPairLock(final byte[] pLockBytes,
                                                           final GordianKeyPair pKeyPair) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            final GordianKeyPairLock myKeyPair = attemptPasswordForKeyPairLock(pLockBytes, pKeyPair, myCurr.array());

            /* If we succeeded */
            if (myKeyPair != null) {
                /* Add the factory to the list and return it */
                theLocks.add(new GordianLockCache<>(myKeyPair, myCurr));
                return myKeyPair;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pLockBytes the Lock to test against
     * @param pPassword the encrypted password
     * @return the new keyPairLock if successful, otherwise null
     */
    private GordianKeyPairLock attemptPasswordForKeyPairLock(final byte[] pLockBytes,
                                                             final GordianKeyPair pKeyPair,
                                                             final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return GordianBuilder.resolveKeyPairLock(theFactory, pLockBytes, pKeyPair, myPasswordChars);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return null;

        } catch (GordianBadCredentialsException e) {
            return null;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Create a keySetHash with a previously used password.
     * @param pKeySetHashSpec the new hashSpec
     * @param pPassword the encrypted password
     * @return the new PasswordHash
     * @throws OceanusException on error
     */
    GordianKeySetHash createSimilarHash(final GordianKeySetHashSpec pKeySetHashSpec,
                                        final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the hash and return it */
            final GordianKeySetFactory myKeySets = theFactory.getKeySetFactory();
            final GordianCoreKeySetHash myHash
                    = (GordianCoreKeySetHash) myKeySets.generateKeySetHash(pKeySetHashSpec, myPasswordChars);

            /* Add the entry to the list and return the hash */
            theHashes.add(new GordianKeySetHashCache(myHash, pPassword));
            return myHash;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Create a factoryLock with a previously used password.
     * @param pFactory the new factory
     * @param pPassword the encrypted password
     * @return the new factoryLock
     * @throws OceanusException on error
     */
    GordianFactoryLock createSimilarFactoryLock(final GordianFactory pFactory,
                                                final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the new lock */
            final GordianFactoryLock myLock = GordianBuilder.createFactoryLock(pFactory, theLockSpec, myPasswordChars);

            /* Add the entry to the list and return the hash */
            theLocks.add(new GordianLockCache<>(myLock, pPassword));
            return myLock;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Create a keySetLock with a previously used password.
     * @param pKeySet the new keySet
     * @param pPassword the encrypted password
     * @return the new factoryLock
     * @throws OceanusException on error
     */
    GordianKeySetLock createSimilarKeySetLock(final GordianKeySet pKeySet,
                                              final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the new lock */
            final GordianKeySetLock myLock = GordianBuilder.createKeySetLock(theFactory, pKeySet, theLockSpec, myPasswordChars);

            /* Add the entry to the list and return the hash */
            theLocks.add(new GordianLockCache<>(myLock, pPassword));
            return myLock;

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * Create a zipLock with a previously used password.
     * @param pKeySetHashSpec the new hashSpec
     * @param pPassword the encrypted password
     * @return the new PasswordHash
     * @throws OceanusException on error
     */
    GordianKeyPairLock createSimilarKeyPairLock(final GordianKeyPair pKeyPair,
                                                final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the similar passwordLock and return it */
            return GordianBuilder.createKeyPairLock(theFactory, theLockSpec, pKeyPair, myPasswordChars);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
            if (myPasswordChars != null) {
                Arrays.fill(myPasswordChars, (char) 0);
            }
        }
    }

    /**
     * The keySetHashCache.
     */
    static class GordianKeySetHashCache {
        /**
         * The KeySetHash.
         */
        private final GordianCoreKeySetHash theHash;

        /**
         * The Encrypted password.
         */
        private final ByteBuffer thePassword;

        /**
         * Constructor.
         * @param pHash the Hash
         * @param pPassword the encrypted password
         */
        GordianKeySetHashCache(final GordianCoreKeySetHash pHash,
                               final ByteBuffer pPassword) {
            theHash = pHash;
            thePassword = pPassword;
        }

        /**
         * Obtain the hash.
         * @return the Hash
         */
        GordianCoreKeySetHash getHash() {
            return theHash;
        }

        /**
         * Obtain the encrypted password.
         * @return the password
         */
        ByteBuffer getPassword() {
            return thePassword;
        }
    }

    /**
     * The lockCache.
     * @param <T> the locked object
     *
     */
    static class GordianLockCache<T> {
        /**
         * The FactoryLock.
         */
        private final GordianLock<T> theLock;

        /**
         * The Encrypted password.
         */
        private final ByteBuffer thePassword;

        /**
         * Constructor.
         * @param pLock the Lock
         * @param pPassword the encrypted password
         */
        GordianLockCache(final GordianLock<T> pLock,
                         final ByteBuffer pPassword) {
            theLock = pLock;
            thePassword = pPassword;
        }

        /**
         * Obtain the lock.
         * @return the Lock
         */
        GordianLock<T> getLock() {
            return theLock;
        }

        /**
         * Obtain the encrypted password.
         * @return the password
         */
        ByteBuffer getPassword() {
            return thePassword;
        }
    }
}
