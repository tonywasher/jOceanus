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
package net.sourceforge.joceanus.jgordianknot.api.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public class GordianSecurityManager {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianSecurityManager.class);

    /**
     * The ZipFile extension.
     */
    public static final String ZIPFILE_EXT = ".zip";

    /**
     * The Encrypted ZipFile extension.
     */
    public static final String SECUREZIPFILE_EXT = ".gkzip";

    /**
     * Text for Password title.
     */
    protected static final String NLS_TITLEPASS = GordianMgrResource.TITLE_PASSWORD.getValue();

    /**
     * Text for New Password title.
     */
    protected static final String NLS_TITLENEWPASS = GordianMgrResource.TITLE_NEWPASS.getValue();

    /**
     * Text for Bad Password Error.
     */
    private static final String NLS_ERRORPASS = GordianMgrResource.ERROR_BADPASS.getValue();

    /**
     * Security factory.
     */
    private final GordianFactory theFactory;

    /**
     * Dialog controller.
     */
    private final GordianDialogController theDialog;

    /**
     * Local keySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * List of resolved password hashes.
     */
    private final List<GordianKeySetHashCache> theHashCache;

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @param pKeySetSpec the keySetSpec
     * @param pDialog the dialog controller
     * @throws OceanusException on error
     */
    public GordianSecurityManager(final GordianParameters pParameters,
                                  final GordianKeySetSpec pKeySetSpec,
                                  final GordianDialogController pDialog) throws OceanusException {
        /* Allocate the factory */
        final GordianFactoryGenerator myGenerator = new GordianGenerator();
        theFactory = myGenerator.newFactory(pParameters);
        theDialog = pDialog;

        /* Allocate a new Hash cache */
        theHashCache = new ArrayList<>();

        /* Create a keySet */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        theKeySet = myFactory.generateKeySet(pKeySetSpec);
    }

    /**
     * Obtain the security factory.
     * @return the security factory
     */
    public GordianFactory getSecurityFactory() {
        return theFactory;
    }

    /**
     * Obtain the keySetSpec.
     * @return the keySetSpec
     */
    public GordianKeySetSpec getKeySetSpec() {
        return theKeySet.getKeySetSpec();
    }

    /**
     * Create a new keySet Hash.
     * @param pKeySetSpec the keySetSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash newKeySetHash(final GordianKeySetSpec pKeySetSpec,
                                           final String pSource) throws OceanusException {
        return requestPassword(pKeySetSpec, pSource);
    }

    /**
     * Create a new keySet Hash.
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash newKeySetHash(final String pSource) throws OceanusException {
        return requestPassword(null, pSource);
    }

    /**
     * Resolve the keySet Hash.
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash resolveKeySetHash(final byte[] pHashBytes,
                                               final String pSource) throws OceanusException {
        /* Look up resolved hash */
        GordianKeySetHash myHash = lookUpResolvedHash(pHashBytes);

        /* If we have not seen the hash then attempt known passwords */
        if (myHash == null) {
            myHash = attemptKnownPasswords(pHashBytes);
        }

        /* If we have not resolved the hash */
        if (myHash == null) {
            myHash = requestPassword(pHashBytes, pSource);
        }

        /* Return the resolved hash */
        return myHash;
    }

    /**
     * Request password for the Hash.
     * @param pQualifier either the hash, or the ketSetSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash requestPassword(final Object pQualifier,
                                             final String pSource) throws OceanusException {
        /* Allocate variables */
        final GordianKeySetFactory myKeySets = theFactory.getKeySetFactory();
        GordianCoreKeySetHash myHash = null;

        /* Process qualifier */
        final byte[] myHashBytes = pQualifier instanceof byte[]
                                     ? (byte[]) pQualifier
                                     : null;
        final GordianKeySetSpec myKeySetSpec = pQualifier instanceof GordianKeySetSpec
                                                ? (GordianKeySetSpec) pQualifier
                                                : theKeySet.getKeySetSpec();
        final boolean needConfirm = myHashBytes == null;

        /* Create a new password dialog */
        theDialog.createTheDialog(pSource, needConfirm);

        /* Prompt for the password */
        boolean isPasswordOk = false;
        char[] myPassword = null;
        while (theDialog.showTheDialog()) {
            try {
                /* Access the password */
                myPassword = theDialog.getPassword();

                /* Check the password */
                if (needConfirm) {
                    myHash = (GordianCoreKeySetHash) myKeySets.generateKeySetHash(myKeySetSpec, myPassword);
                } else {
                    myHash = (GordianCoreKeySetHash) myKeySets.deriveKeySetHash(myHashBytes, myPassword);
                }

                /* No exception so we are good to go */
                isPasswordOk = true;

                /* Add the hash to the list and break the loop */
                addResolvedHash(myHash, myPassword);
                break;

            } catch (GordianBadCredentialsException e) {
                theDialog.setError(NLS_ERRORPASS);
                Arrays.fill(myPassword, (char) 0);

            } finally {
                if (myPassword != null) {
                    Arrays.fill(myPassword, (char) 0);
                    myPassword = null;
                }
            }
        }

        /* release password resources */
        theDialog.releaseDialog();

        /* If we did not get a password */
        if (!isPasswordOk) {
            /* Throw an exception */
            throw new GordianDataException("Invalid Password");
        }

        /* Return the password hash */
        return myHash;
    }

    /**
     * obtain similar (same password) hash.
     * @param pHash the keySetHash to clone
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash similarKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
        /* Use configured keySetSpec */
        return similarKeySetHash(theKeySet.getKeySetSpec(), pHash);
    }

    /**
     * obtain similar (same password) hash.
     * @param pKeySetSpec the keySetSpec
     * @param pHash the keySetHash to clone
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash similarKeySetHash(final GordianKeySetSpec pKeySetSpec,
                                               final GordianKeySetHash pHash) throws OceanusException {
        /* LookUp the hash */
        final ByteBuffer myPassword = lookUpResolvedPassword(pHash);
        if (myPassword == null) {
            throw new GordianDataException("Hash not known");
        }

        /* Create a similar hash */
        return createSimilarHash(myPassword);
    }

    /**
     * Obtain Maximum KeyWrapLength.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeyWrapLength() {
        return GordianCoreKeySet.getDataWrapLength(GordianLength.LEN_256.getByteLength(),
                                                   GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
    }

    /**
     * Obtain Maximum KeyWrapLength.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeySetWrapLength() {
        final int my128 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_128,
                GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
        final int my256 = GordianCoreKeySet.getKeySetWrapLength(GordianLength.LEN_256,
                GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
        return Math.max(my128, my256);
    }

    /**
     * Obtain HashLength.
     * @return the maximum keyWrap size
     */
    public static int getKeySetHashLen() {
        return GordianKeySetHashASN1.getEncodedLength();
    }

    /**
     * Obtain Encryption length.
     *
     * @param pDataLength the length of data to be encrypted
     * @param pAEAD true/false is AEAD in use?
     * @return the length of encrypted data
     */
    public static int getKeySetEncryptionLength(final int pDataLength,
                                                final boolean pAEAD) {
        return GordianCoreKeySet.getEncryptionLength(pDataLength, pAEAD);
    }

    /**
     * Add resolved Hash to cache.
     * @param pHash the Hash to attempt passwords for
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void addResolvedHash(final GordianCoreKeySetHash pHash,
                                 final char[] pPassword) throws OceanusException {
        byte[] myPasswordBytes = null;
        try {
            /* Encrypt the password */
            myPasswordBytes = TethysDataConverter.charsToByteArray(pPassword);
            final byte[] myEncrypted = theKeySet.encryptBytes(myPasswordBytes);

            /* Add the entry to the list */
            theHashCache.add(new GordianKeySetHashCache(pHash, ByteBuffer.wrap(myEncrypted)));

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
    private GordianCoreKeySetHash lookUpResolvedHash(final byte[] pHashBytes) {
        /* Look for the hash in the list */
        for (GordianKeySetHashCache myCurr : theHashCache) {
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
     * @param pHash the Hash to search for
     * @return the encrypted password
     */
    private ByteBuffer lookUpResolvedPassword(final GordianKeySetHash pHash) {
        /* Look for the hash in the list */
        for (GordianKeySetHashCache myCurr : theHashCache) {
            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(pHash.getHash(), myCurr.getHash().getHash())) {
                return myCurr.getPassword();
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Attempt known passwords.
     * @param pHashBytes the HashBytes to attempt passwords for
     * @return the new PasswordHash if successful, otherwise null
     */
    private GordianCoreKeySetHash attemptKnownPasswords(final byte[] pHashBytes) {
        /* Create a list of passwords that we have tried */
        final List<ByteBuffer> mySeen = new ArrayList<>();

        /* Look for the hash in the list */
        for (GordianKeySetHashCache myCurr : theHashCache) {
            /* Access the password and re-loop if we have seen this password */
            final ByteBuffer myEncrypted = myCurr.getPassword();
            if (mySeen.contains(myEncrypted)) {
                continue;
            }

            /* Attempt the password */
            final GordianCoreKeySetHash myHash = (GordianCoreKeySetHash) attemptPasswordForHash(pHashBytes, myEncrypted.array());

            /* If we succeeded */
            if (myHash != null) {
                /* Add the hash to the list and return it */
                theHashCache.add(new GordianKeySetHashCache(myHash, myEncrypted));
                return myHash;
            }

            /* Add the password to the list of those that we have seen */
            mySeen.add(myEncrypted);
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
            LOGGER.error("Password attempt failed", e);
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
     * Create a hash with a previously used password.
     * @param pPassword the encrypted password
     * @return the new PasswordHash
     * @throws OceanusException on error
     */
    private GordianKeySetHash createSimilarHash(final ByteBuffer pPassword) throws OceanusException {
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
                    = (GordianCoreKeySetHash) myKeySets.generateKeySetHash(theKeySet.getKeySetSpec(), myPasswordChars);

            /* Add the entry to the list and return the hash */
            theHashCache.add(new GordianKeySetHashCache(myHash, pPassword));
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
