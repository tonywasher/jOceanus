/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.security;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.oceanus.convert.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.exc.PrometheusSecurityException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Password Cache.
 */
public class PrometheusSecurityPasswordCache {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(PrometheusSecurityPasswordCache.class);

    /**
     * Password failed message.
     */
    private static final String PASSWORD_FAIL = "Password attempt failed";

    /**
     * List of resolved Locks.
     */
    private final List<PrometheusLockCache<?>> theLocks;

    /**
     * List of successful passwords.
     */
    private final List<ByteBuffer> thePasswords;

    /**
     * The KeySet Factory.
     */
    private final GordianKeySetFactory theKeySetFactory;

    /**
     * The lockFactory.
     */
    private final GordianLockFactory theLockFactory;

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
    PrometheusSecurityPasswordCache(final PrometheusSecurityPasswordManager pManager,
                                    final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Store factory and lockSpec*/
            final GordianFactory myFactory = pManager.getSecurityFactory();
            theKeySetFactory = myFactory.getKeySetFactory();
            theLockFactory = myFactory.getLockFactory();
            theLockSpec = pLockSpec;

            /* Create a keySet */
            theKeySet = theKeySetFactory.generateKeySet(pLockSpec.getKeySetSpec());

            /* Create the lists */
            theLocks = new ArrayList<>();
            thePasswords = new ArrayList<>();

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
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
            myPasswordBytes = OceanusDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new PrometheusLockCache<>(pFactory, myBuffer));
            thePasswords.add(myBuffer);


        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

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
            myPasswordBytes = OceanusDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new PrometheusLockCache<>(pKeySet, myBuffer));
            thePasswords.add(myBuffer);

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

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
            myPasswordBytes = OceanusDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the lists */
            final ByteBuffer myBuffer = ByteBuffer.wrap(myEncrypted);
            theLocks.add(new PrometheusLockCache<>(pKeyPair, myBuffer));
            thePasswords.add(myBuffer);

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

        } finally {
            /* Clear out password */
            if (myPasswordBytes != null) {
                Arrays.fill(myPasswordBytes, (byte) 0);
            }
        }
    }

    /**
     * LookUp previously resolved Factory.
     * @param pLockBytes the LockBytes to search for
     * @return the previous factoryLock if found, otherwise null
     */
    GordianFactoryLock lookUpResolvedFactoryLock(final byte[] pLockBytes) {
        /* Look for the factory in the list */
        for (PrometheusLockCache<?> myCurr : theLocks) {
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
        /* Look for the keySet in the list */
        for (PrometheusLockCache<?> myCurr : theLocks) {
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
     * LookUp previously resolved keyPair.
     * @param pLockBytes the LockBytes to search for
     * @param pKeyPair the keyPair
     * @return the previous keySetLock if found, otherwise null
     */
    GordianKeyPairLock lookUpResolvedKeyPairLock(final byte[] pLockBytes,
                                                 final GordianKeyPair pKeyPair) {
        /* Look for the keyPair in the list */
        for (PrometheusLockCache<?> myCurr : theLocks) {
            /* If this is the keyPairLock we are looking for, return it */
            if (myCurr.getLock() instanceof GordianKeyPairLock
                    && Arrays.equals(pLockBytes, myCurr.getLock().getLockBytes())
                    && pKeyPair.equals(((GordianKeyPairLock) myCurr.getLock()).getKeyPair())) {
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
        /* If the reference is a lock */
        if (pReference instanceof GordianLock) {
            /* Look for the lock in the list */
            final GordianLock<?> myReference = (GordianLock<?>) pReference;
            for (PrometheusLockCache<?> myCurr : theLocks) {
                /* If this is the lock are looking for, return it */
                if (Objects.equals(myReference, myCurr.getLock())) {
                    return myCurr.getPassword();
                }
            }
        }

        /* Throw error */
        throw new PrometheusDataException("Referenced Object not known");
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
                theLocks.add(new PrometheusLockCache<>(myFactory, myCurr));
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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return theLockFactory.resolveFactoryLock(pLockBytes, myPasswordChars);

            /* Catch Exceptions */
        } catch (GordianException
                | OceanusException e) {
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
                theLocks.add(new PrometheusLockCache<>(myKeySet, myCurr));
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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return theLockFactory.resolveKeySetLock(pLockBytes, myPasswordChars);

            /* Catch Exceptions */
        } catch (GordianException
                | OceanusException e) {
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
     * @param pKeyPair the keyPair
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
                theLocks.add(new PrometheusLockCache<>(myKeyPair, myCurr));
                return myKeyPair;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pLockBytes the Lock to test against
     * @param pKeyPair the keyPair
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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the lock and return it */
            return theLockFactory.resolveKeyPairLock(pLockBytes, pKeyPair, myPasswordChars);

            /* Catch Exceptions */
        } catch (GordianException
                | OceanusException e) {
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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the new lock */
            final GordianFactoryLock myLock = theLockFactory.newFactoryLock(pFactory, theLockSpec, myPasswordChars);

            /* Add the entry to the list and return the hash */
            theLocks.add(new PrometheusLockCache<>(myLock, pPassword));
            return myLock;

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the new lock */
            final GordianKeySetLock myLock = theLockFactory.newKeySetLock(pKeySet, theLockSpec, myPasswordChars);

            /* Add the entry to the list and return the hash */
            theLocks.add(new PrometheusLockCache<>(myLock, pPassword));
            return myLock;

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

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
     * @param pKeyPair the keyPair
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
            myPasswordChars = OceanusDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the similar passwordLock and return it */
            return theLockFactory.newKeyPairLock(theLockSpec, pKeyPair, myPasswordChars);

        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);

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
     * The lockCache.
     * @param <T> the locked object
     *
     */
    static class PrometheusLockCache<T> {
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
        PrometheusLockCache(final GordianLock<T> pLock,
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
