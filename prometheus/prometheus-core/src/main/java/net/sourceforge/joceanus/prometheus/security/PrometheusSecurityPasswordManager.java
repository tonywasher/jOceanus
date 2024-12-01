/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.security;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.PrometheusLogicException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Security Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public class PrometheusSecurityPasswordManager {
    /**
     * Security factory.
     */
    private final GordianFactory theFactory;

    /**
     * Lock factory.
     */
    private final GordianLockFactory theLockFactory;

    /**
     * PasswordLockSpec.
     */
    private final GordianPasswordLockSpec theLockSpec;

    /**
     * The Cache.
     */
    private final PrometheusSecurityPasswordCache theCache;

    /**
     * Dialog controller.
     */
    private PrometheusSecurityDialogController theDialog;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pDialog the dialog controller
     * @throws OceanusException on error
     */
    public PrometheusSecurityPasswordManager(final GordianFactory pFactory,
                                             final PrometheusSecurityDialogController pDialog) throws OceanusException {
        this(pFactory, new GordianPasswordLockSpec(), pDialog);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pLockSpec the lockSpec
     * @param pDialog the dialog controller
     * @throws OceanusException on error
     */
    public PrometheusSecurityPasswordManager(final GordianFactory pFactory,
                                             final GordianPasswordLockSpec pLockSpec,
                                             final PrometheusSecurityDialogController pDialog) throws OceanusException {
        /* Allocate the factory */
        theFactory = pFactory;
        theLockFactory = theFactory.getLockFactory();
        theDialog = pDialog;
        theLockSpec = pLockSpec;

        /* Allocate a new cache */
        theCache = new PrometheusSecurityPasswordCache(this, theLockSpec);
    }

    /**
     * Obtain the security factory.
     * @return the factory
     */
    public GordianFactory getSecurityFactory() {
        return theFactory;
    }

    /**
     * Set the dialog controller.
     * @param pDialog the controller
     */
    public void setDialogController(final PrometheusSecurityDialogController pDialog) {
        theDialog = pDialog;
    }

    /**
     * Create a new factoryLock.
     * @param pSource the description of the secured resource
     * @return the factoryLock
     * @throws OceanusException on error
     */
    public GordianFactoryLock newFactoryLock(final String pSource) throws OceanusException {
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        return (GordianFactoryLock) requestPassword(pSource, true, p -> createFactoryLock(myFactory, p));
    }

    /**
     * Create a new factoryLock.
     * @param pFactory the factory to lock
     * @param pSource the description of the secured resource
     * @return the factoryLock
     * @throws OceanusException on error
     */
    public GordianFactoryLock newFactoryLock(final GordianFactory pFactory,
                                             final String pSource) throws OceanusException {
        return (GordianFactoryLock) requestPassword(pSource, true, p -> createFactoryLock(pFactory, p));
    }

    /**
     * Resolve the factoryLock bytes.
     * @param pLockBytes the lock bytes to resolve
     * @param pSource the description of the secured resource
     * @return the factoryLock
     * @throws OceanusException on error
     */
    public GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                 final String pSource) throws OceanusException {
        /* Look up resolved factory */
        GordianFactoryLock myFactory = theCache.lookUpResolvedFactoryLock(pLockBytes);

        /* If we have not seen the lock then attempt known passwords */
        if (myFactory == null) {
            myFactory = theCache.attemptKnownPasswordsForFactoryLock(pLockBytes);
        }

        /* If we have not resolved the lock */
        if (myFactory == null) {
            myFactory = (GordianFactoryLock) requestPassword(pSource, false, p -> resolveFactoryLock(pLockBytes, p));
        }

        /* Return the resolved factoryLock */
        return myFactory;
    }

    /**
     * obtain new locked factory (same password).
     * @param pReference the reference to clone password from
     * @return the similar factoryLock
     * @throws OceanusException on error
     */
    public GordianFactoryLock similarFactoryLock(final Object pReference) throws OceanusException {
        /* Create a new random factory */
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(GordianFactoryType.BC);

        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar factoryLock */
        return theCache.createSimilarFactoryLock(myFactory, myPassword);
    }

    /**
     * Create a new keySetLock.
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    public GordianKeySetLock newKeySetLock(final String pSource) throws OceanusException {
        final GordianKeySet myKeySet = theFactory.getKeySetFactory().generateKeySet(theLockSpec.getKeySetSpec());
        return (GordianKeySetLock) requestPassword(pSource, true, p -> createKeySetLock(myKeySet, p));
    }

    /**
     * Create a new keySetLock.
     * @param pKeySet the keySet to lock
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    public GordianKeySetLock newKeySetLock(final GordianKeySet pKeySet,
                                           final String pSource) throws OceanusException {
        return (GordianKeySetLock) requestPassword(pSource, true, p -> createKeySetLock(pKeySet, p));
    }

    /**
     * Resolve the keySetLock bytes.
     * @param pLockBytes the lock bytes to resolve
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    public GordianKeySetLock resolveKeySetLock(final byte[] pLockBytes,
                                               final String pSource) throws OceanusException {
        /* Look up resolved keySet */
        GordianKeySetLock myKeySet = theCache.lookUpResolvedKeySetLock(pLockBytes);

        /* If we have not seen the lock then attempt known passwords */
        if (myKeySet == null) {
            myKeySet = theCache.attemptKnownPasswordsForKeySetLock(pLockBytes);
        }

        /* If we have not resolved the lock */
        if (myKeySet == null) {
            myKeySet = (GordianKeySetLock) requestPassword(pSource, false, p -> resolveKeySetLock(pLockBytes, p));
        }

        /* Return the resolved keySetLock */
        return myKeySet;
    }

    /**
     * obtain new locked keySet (same password).
     * @param pReference the reference to clone password from
     * @return the similar keySetLock
     * @throws OceanusException on error
     */
    public GordianKeySetLock similarKeySetLock(final Object pReference) throws OceanusException {
        /* Create a new random keySet */
        final GordianKeySet myKeySet = theFactory.getKeySetFactory().generateKeySet(theLockSpec.getKeySetSpec());

        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar keySetLock */
        return theCache.createSimilarKeySetLock(myKeySet, myPassword);
    }

    /**
     * Create a new keyPairLock.
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the keyPairLock
     * @throws OceanusException on error
     */
    public GordianKeyPairLock newKeyPairLock(final GordianKeyPair pKeyPair,
                                             final String pSource) throws OceanusException {
        return (GordianKeyPairLock) requestPassword(pSource, true, p -> createKeyPairLock(pKeyPair, p));
    }

    /**
     * Resolve the keyPairLock.
     * @param pLockBytes the LockBytes to resolve
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the keyPairLock
     * @throws OceanusException on error
     */
    public GordianKeyPairLock resolveKeyPairLock(final byte[] pLockBytes,
                                                 final GordianKeyPair pKeyPair,
                                                 final String pSource) throws OceanusException {
        /* Look up resolved keySet */
        GordianKeyPairLock myKeyPair = theCache.lookUpResolvedKeyPairLock(pLockBytes, pKeyPair);

        /* If we have not seen the lock then attempt known passwords */
        if (myKeyPair == null) {
            myKeyPair = theCache.attemptKnownPasswordsForKeyPairLock(pLockBytes, pKeyPair);
        }

        /* If we have not resolved the lock */
        if (myKeyPair == null) {
            myKeyPair = (GordianKeyPairLock) requestPassword(pSource, false, p -> resolveKeyPairLock(pLockBytes, pKeyPair, p));
        }

        /* Return the resolved keyPairLock */
        return myKeyPair;
    }

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the reference to clone password from
     * @return the similar keyPairLock
     * @throws OceanusException on error
     */
    public GordianKeyPairLock similarKeyPairLock(final GordianKeyPair pKeyPair,
                                                 final Object pReference) throws OceanusException {
        /* LookUp the password */
        final ByteBuffer myPassword = theCache.lookUpResolvedPassword(pReference);

        /* Create a similar keyPairLock */
        return theCache.createSimilarKeyPairLock(pKeyPair, myPassword);
    }

    /**
     * Resolve the zipLock.
     * @param pZipLock the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    public void resolveZipLock(final GordianZipLock pZipLock,
                               final String pSource) throws OceanusException {
        switch (pZipLock.getLockType()) {
            case KEYSET_PASSWORD:
                resolveKeySetZipLock(pZipLock, pSource);
                break;
            case FACTORY_PASSWORD:
                resolveFactoryZipLock(pZipLock, pSource);
                break;
            case KEYPAIR_PASSWORD:
            default:
                throw new PrometheusLogicException("KeyPair zipLock not supported yet");
        }
    }

    /**
     * Resolve a keySet ZipLock.
     * @param pZipLock the zipLock
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    private void resolveKeySetZipLock(final GordianZipLock pZipLock,
                                      final String pSource) throws OceanusException {
        /* Access lockBytes */
        final byte[] myLockBytes = pZipLock.getLockBytes();

        /* Look up resolved keySet */
        final GordianKeySetLock myLock = resolveKeySetLock(myLockBytes, pSource);

        /* If we resolved the lock */
        if (myLock != null) {
            pZipLock.unlock(myLock);
        }
    }

    /**
     * Resolve a factory ZipLock.
     * @param pZipLock the zipLock
     * @param pSource the description of the secured resource
     * @throws OceanusException on error
     */
    private void resolveFactoryZipLock(final GordianZipLock pZipLock,
                                       final String pSource) throws OceanusException {
        /* Access lockBytes */
        final byte[] myLockBytes = pZipLock.getLockBytes();

        /* Look up resolved keySet */
        final GordianFactoryLock myLock = resolveFactoryLock(myLockBytes, pSource);

        /* If we resolved the lock */
        if (myLock != null) {
            pZipLock.unlock(myLock);
        }
    }

    /**
     * Request password.
     * @param pSource the description of the secured resource
     * @param pNeedConfirm do we need confirmation
     * @param pProcessor the password processor
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public Object requestPassword(final String pSource,
                                  final boolean pNeedConfirm,
                                  final PrometheusProcessPassword pProcessor) throws OceanusException {
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
            throw new PrometheusDataException("Invalid Password");
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Process password interface.
     */
    @FunctionalInterface
    public interface PrometheusProcessPassword {
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
     * Create new factoryLock.
     * @param pFactory the factory
     * @param pPassword the password
     * @return the new lock
     * @throws OceanusException on error
     */
    private GordianFactoryLock createFactoryLock(final GordianFactory pFactory,
                                                 final char[] pPassword) throws OceanusException {
        final GordianFactoryLock myLock = theLockFactory.newFactoryLock(pFactory, theLockSpec, pPassword);
        theCache.addResolvedFactory(myLock, pPassword);
        return myLock;
    }

    /**
     * Resolve password for factoryLock.
     * @param pLockBytes the lock bytes
     * @param pPassword the password
     * @return the resolved lock
     * @throws OceanusException on error
     */
    private GordianFactoryLock resolveFactoryLock(final byte[] pLockBytes,
                                                  final char[] pPassword) throws OceanusException {
        final GordianFactoryLock myFactory = theLockFactory.resolveFactoryLock(pLockBytes, pPassword);
        theCache.addResolvedFactory(myFactory, pPassword);
        return myFactory;
    }

    /**
     * Create new keySetLock.
     * @param pKeySet the keySet
     * @param pPassword the password
     * @return the new lock
     * @throws OceanusException on error
     */
    private GordianKeySetLock createKeySetLock(final GordianKeySet pKeySet,
                                               final char[] pPassword) throws OceanusException {
        final GordianKeySetLock myLock = theLockFactory.newKeySetLock(pKeySet, theLockSpec, pPassword);
        theCache.addResolvedKeySet(myLock, pPassword);
        return myLock;
    }

    /**
     * Resolve password for keySetLock.
     * @param pLockBytes the lock bytes
     * @param pPassword the password
     * @return the resolved lock
     * @throws OceanusException on error
     */
    private GordianKeySetLock resolveKeySetLock(final byte[] pLockBytes,
                                                final char[] pPassword) throws OceanusException {
        final GordianKeySetLock myKeySet = theLockFactory.resolveKeySetLock(pLockBytes, pPassword);
        theCache.addResolvedKeySet(myKeySet, pPassword);
        return myKeySet;
    }

    /**
     * Create new keyPairLock.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the new lock
     * @throws OceanusException on error
     */
    private GordianKeyPairLock createKeyPairLock(final GordianKeyPair pKeyPair,
                                                 final char[] pPassword) throws OceanusException {
        final GordianKeyPairLock myLock = theLockFactory.newKeyPairLock(theLockSpec, pKeyPair, pPassword);
        theCache.addResolvedKeyPair(myLock, pPassword);
        return myLock;
    }

    /**
     * Resolve password for keyPairLock.
     * @param pLockBytes the lock bytes
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the resolved lock
     * @throws OceanusException on error
     */
    private GordianKeyPairLock resolveKeyPairLock(final byte[] pLockBytes,
                                                  final GordianKeyPair pKeyPair,
                                                  final char[] pPassword) throws OceanusException {
        final GordianKeyPairLock myKeyPair = theLockFactory.resolveKeyPairLock(pLockBytes, pKeyPair, pPassword);
        theCache.addResolvedKeyPair(myKeyPair, pPassword);
        return myKeyPair;
    }
}
