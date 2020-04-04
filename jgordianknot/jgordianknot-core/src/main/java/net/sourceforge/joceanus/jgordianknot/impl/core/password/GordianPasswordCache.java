/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.password;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
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
     * List of successful passwords.
     */
    private final List<ByteBuffer> thePasswords;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * Local keySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pManager the password manager
     * @throws OceanusException on error
     */
    GordianPasswordCache(final GordianPasswordManager pManager) throws OceanusException {
        /* Store factory */
        theFactory = pManager.getSecurityFactory();

        /* Create a keySet */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianKeySetSpec mySpec = pManager.getKeySetSpec();
        theKeySet = myFactory.generateKeySet(mySpec);

        /* Create the lists */
        theHashes = new ArrayList<>();
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
     * LookUp previously resolved Password.
     * @param pReference the Reference to search for
     * @return the encrypted password
     * @throws OceanusException on error
     */
    ByteBuffer lookUpResolvedPassword(final GordianKeySetHash pReference) throws OceanusException {
        /* Look for the hash in the list */
        final byte[] myHashBytes = pReference.getHash();
        for (GordianKeySetHashCache myCurr : theHashes) {
            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(myHashBytes, myCurr.getHash().getHash())) {
                return myCurr.getPassword();
            }
        }

        /* Throw error */
        throw new GordianDataException("Referenced Hash not known");
    }

    /**
     * Attempt known passwords.
     * @param pHashBytes the HashBytes to attempt passwords for
     * @return the new PasswordHash if successful, otherwise null
     */
    GordianCoreKeySetHash attemptKnownPasswords(final byte[] pHashBytes) {
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
     * Attempt known passwords.
     * @param pLock the zipLock to attempt passwords for
     * @return successful true/false
     */
    boolean attemptKnownPasswords(final GordianZipLock pLock) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            if (attemptPasswordForLock(pLock, myCurr.array())) {
                return true;
            }
        }

        /* No success */
        return false;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pLock the Lock to test against
     * @param pPassword the encrypted password
     * @return successful true/false
     */
    private boolean attemptPasswordForLock(final GordianZipLock pLock,
                                           final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the hash and return it */
            pLock.unlock(myPasswordChars);
            return true;

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return false;

        } catch (GordianBadCredentialsException e) {
            return false;

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
     * Attempt known passwords.
     * @param pKeyPair the keyPair
     * @param pLock the zipLock to attempt passwords for
     * @return successful true/false
     */
    boolean attemptKnownPasswords(final GordianKeyPair pKeyPair,
                                  final GordianZipLock pLock) {
        /* Loop through the passwords */
        for (ByteBuffer myCurr : thePasswords) {
            /* Attempt the password */
            if (attemptPasswordForLock(pKeyPair, pLock, myCurr.array())) {
                return true;
            }
        }

        /* No success */
        return false;
    }

    /**
     * Attempt the cached password against the passed lock.
     * @param pKeyPair the keyPair
     * @param pLock the Lock to test against
     * @param pPassword the encrypted password
     * @return successful true/false
     */
    private boolean attemptPasswordForLock(final GordianKeyPair pKeyPair,
                                           final GordianZipLock pLock,
                                           final byte[] pPassword) {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword);
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Try to resolve the hash and return it */
            pLock.unlock(pKeyPair, myPasswordChars);
            return true;

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error(PASSWORD_FAIL, e);
            return false;

        } catch (GordianBadCredentialsException e) {
            return false;

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
     * Create a zipLock with a previously used password.
     * @param pKeySetHashSpec the new hashSpec
     * @param pPassword the encrypted password
     * @return the new PasswordHash
     * @throws OceanusException on error
     */
    GordianZipLock createSimilarZipLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                        final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the similar ZipLock and return it */
            final GordianZipFactory myZips = theFactory.getZipFactory();
            return myZips.createZipLock(pKeySetHashSpec, myPasswordChars);

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
     * @param pKeySetHashSpec the new hashSpec
     * @param pPassword the encrypted password
     * @return the new PasswordHash
     * @throws OceanusException on error
     */
    GordianZipLock createSimilarZipLock(final GordianKeyPair pKeyPair,
                                        final GordianKeySetHashSpec pKeySetHashSpec,
                                        final ByteBuffer pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPasswordBytes = null;
        char[] myPasswordChars = null;
        try {
            /* Access the original password */
            myPasswordBytes = theKeySet.decryptBytes(pPassword.array());
            myPasswordChars = TethysDataConverter.bytesToCharArray(myPasswordBytes);

            /* Create the similar ZipLock and return it */
            final GordianZipFactory myZips = theFactory.getZipFactory();
            return myZips.createZipLock(pKeyPair, pKeySetHashSpec, myPasswordChars);

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
}
