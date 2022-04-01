/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public class GordianCorePasswordManager
    implements GordianPasswordManager {
    /**
     * Security factory.
     */
    private final GordianFactory theFactory;

    /**
     * Dialog controller.
     */
    private final GordianDialogController theDialog;

    /**
     * keySetHashSpec.
     */
    private final GordianKeySetHashSpec theKeySetHashSpec;

    /**
     * The Cache.
     */
    private final GordianPasswordCache theCache;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pDialog the dialog controller
     * @throws OceanusException on error
     */
    public GordianCorePasswordManager(final GordianFactory pFactory,
                                      final GordianKeySetHashSpec pKeySetHashSpec,
                                      final GordianDialogController pDialog) throws OceanusException {
        /* Allocate the factory */
        theFactory = pFactory;
        theDialog = pDialog;
        theKeySetHashSpec = pKeySetHashSpec;

        /* Allocate a new cache */
        theCache = new GordianPasswordCache(this);
    }

    @Override
    public GordianFactory getSecurityFactory() {
        return theFactory;
    }

    @Override
    public GordianKeySetSpec getKeySetSpec() {
        return theKeySetHashSpec.getKeySetSpec();
    }

    @Override
    public GordianKeySetHash newKeySetHash(final GordianKeySetHashSpec pKeySetHashSpec,
                                           final String pSource) throws OceanusException {
        return (GordianKeySetHash) requestPassword(pSource, true, p -> createKeySetHash(pKeySetHashSpec, p));
    }

    @Override
    public GordianKeySetHash resolveKeySetHash(final byte[] pHashBytes,
                                               final String pSource) throws OceanusException {
        /* Look up resolved hash */
        GordianKeySetHash myHash = theCache.lookUpResolvedHash(pHashBytes);

        /* If we have not seen the hash then attempt known passwords */
        if (myHash == null) {
            myHash = theCache.attemptKnownPasswords(pHashBytes);
        }

        /* If we have not resolved the hash */
        if (myHash == null) {
            myHash = (GordianKeySetHash) requestPassword(pSource, false, p -> resolveKeySetHash(pHashBytes, p));
        }

        /* Return the resolved hash */
        return myHash;
    }

    @Override
    public GordianKeySetHash similarKeySetHash(final GordianKeySetHashSpec pKeySetHashSpec,
                                               final GordianKeySetHash pReference) throws OceanusException {
        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar hash */
        return theCache.createSimilarHash(pKeySetHashSpec, myPassword);
    }

    @Override
    public GordianLock newZipLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                  final String pSource) throws OceanusException {
        return (GordianLock) requestPassword(pSource, true, p -> createZipLock(pKeySetHashSpec, p));
    }

    @Override
    public GordianLock newZipLock(final GordianKeyPair pKeyPair,
                                  final GordianKeySetHashSpec pKeySetHashSpec,
                                  final String pSource) throws OceanusException {
        return (GordianLock) requestPassword(pSource, true, p -> createZipLock(pKeyPair, pKeySetHashSpec, p));
    }

    @Override
    public void resolveZipLock(final GordianLock pZipLock,
                               final String pSource) throws OceanusException {
        /* attempt known passwords */
        if (!theCache.attemptKnownPasswords(pZipLock)) {
            requestPassword(pSource, false, p -> resolveZipLock(pZipLock, p));
        }
    }

    @Override
    public void resolveZipLock(final GordianKeyPair pKeyPair,
                               final GordianLock pZipLock,
                               final String pSource) throws OceanusException {
        /* attempt known passwords */
        if (!theCache.attemptKnownPasswords(pKeyPair, pZipLock)) {
            requestPassword(pSource, false, p -> resolveZipLock(pKeyPair, pZipLock, p));
        }
    }

    @Override
    public GordianLock similarZipLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                      final GordianKeySetHash pReference) throws OceanusException {
        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar zipLock */
        return theCache.createSimilarZipLock(pKeySetHashSpec, myPassword);
    }

    @Override
    public GordianLock similarZipLock(final GordianKeyPair pKeyPair,
                                      final GordianKeySetHashSpec pKeySetHashSpec,
                                      final GordianKeySetHash pReference) throws OceanusException {
        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar zipLock */
        return theCache.createSimilarZipLock(pKeyPair, pKeySetHashSpec, myPassword);
    }

    @Override
    public GordianLock similarZipLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                      final GordianLock pReference) throws OceanusException {
        /* Access hash and create similar zipLock */
        final GordianKeySetHash myHash = ((GordianCoreLock) pReference).getKeySetHash();
        return similarZipLock(pKeySetHashSpec, myHash);
   }

    @Override
    public GordianLock similarZipLock(final GordianKeyPair pKeyPair,
                                      final GordianKeySetHashSpec pKeySetHashSpec,
                                      final GordianLock pReference) throws OceanusException {
        /* Access hash and create similar zipLock */
        final GordianKeySetHash myHash = ((GordianCoreLock) pReference).getKeySetHash();
        return similarZipLock(pKeyPair, pKeySetHashSpec, myHash);
    }

    /**
     * Request password for a hash.
     * @param pSource the description of the secured resource
     * @param pNeedConfirm do we need confirmation
     * @param pProcessor the password processor
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public Object requestPassword(final String pSource,
                                  final boolean pNeedConfirm,
                                  final GordianProcessPassword pProcessor) throws OceanusException {
        /* Allocate variables */
        Object myResult = null;

        /* Create a new password dialog */
        theDialog.createTheDialog(pSource, pNeedConfirm);

        /* Prompt for the password */
        boolean isPasswordOk = false;
        char[] myPassword = null;
        while (theDialog.showTheDialog()) {
            try {
                /* Access the password */
                myPassword = theDialog.getPassword();

                /* Process the password */
                myResult = pProcessor.processPassword(myPassword);

                /* No exception so we are good to go */
                isPasswordOk = true;
                break;

            } catch (GordianBadCredentialsException e) {
                theDialog.reportBadPassword();
                if (myPassword != null) {
                    Arrays.fill(myPassword, (char) 0);
                }

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

        /* Return the result */
        return myResult;
    }

    @FunctionalInterface
    public interface GordianProcessPassword {
        /**
         * Process password.
         * @param pPassword the password
         * @return the result
         * @throws OceanusException on error
         * @throws GordianBadCredentialsException if password does not match
         */
        Object processPassword(char[] pPassword) throws OceanusException;
    }

    /**
     * Create new keySetHash.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pPassword the password
     * @return the new hash
     * @throws OceanusException on error
     */
    private GordianKeySetHash createKeySetHash(final GordianKeySetHashSpec pKeySetHashSpec,
                                               final char[] pPassword) throws OceanusException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianCoreKeySetHash myKeySetHash = (GordianCoreKeySetHash) myFactory.generateKeySetHash(pKeySetHashSpec, pPassword);
        theCache.addResolvedHash(myKeySetHash, pPassword);
        return myKeySetHash;
    }

    /**
     * Resolve password for keySetHash.
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the resolved hash
     * @throws OceanusException on error
     */
    private GordianKeySetHash resolveKeySetHash(final byte[] pHashBytes,
                                                final char[] pPassword) throws OceanusException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final GordianCoreKeySetHash myKeySetHash = (GordianCoreKeySetHash) myFactory.deriveKeySetHash(pHashBytes, pPassword);
        theCache.addResolvedHash(myKeySetHash, pPassword);
        return myKeySetHash;
    }

    /**
     * Create new zipLock.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pPassword the password
     * @return the new hash
     * @throws OceanusException on error
     */
    private GordianLock createZipLock(final GordianKeySetHashSpec pKeySetHashSpec,
                                      final char[] pPassword) throws OceanusException {
        final GordianZipFactory myFactory = theFactory.getZipFactory();
        final GordianLock myZipLock = myFactory.createPasswordLock(pKeySetHashSpec, pPassword);
        theCache.addResolvedPassword(pPassword);
        return myZipLock;
    }

    /**
     * Create new zipLock.
     * @param pKeyPair the keyPair
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pPassword the password
     * @return the new hash
     * @throws OceanusException on error
     */
    private GordianLock createZipLock(final GordianKeyPair pKeyPair,
                                      final GordianKeySetHashSpec pKeySetHashSpec,
                                      final char[] pPassword) throws OceanusException {
        final GordianZipFactory myFactory = theFactory.getZipFactory();
        final GordianLock myZipLock = myFactory.createKeyPairLock(pKeyPair, pKeySetHashSpec, pPassword);
        theCache.addResolvedPassword(pPassword);
        return myZipLock;
    }

    /**
     * Resolve password for zipLock.
     * @param pLock the zipLock
     * @param pPassword the password
     * @return null
     * @throws OceanusException on error
     */
    private Object resolveZipLock(final GordianLock pLock,
                                final char[] pPassword) throws OceanusException {
        pLock.unlock(pPassword);
        theCache.addResolvedPassword(pPassword);
        return null;
    }

    /**
     * Resolve password for zipLock.
     * @param pKeyPair the keyPair
     * @param pLock the zipLock
     * @param pPassword the password
     * @return null
     * @throws OceanusException on error
     */
    private Object resolveZipLock(final GordianKeyPair pKeyPair,
                                  final GordianLock pLock,
                                  final char[] pPassword) throws OceanusException {
        pLock.unlock(pKeyPair, pPassword);
        theCache.addResolvedPassword(pPassword);
        return null;
    }
}
