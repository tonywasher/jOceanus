/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetHash;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public abstract class GordianHashManager {
    /**
     * Text for Password title.
     */
    private static final String NLS_TITLEPASS = GordianMgrResource.TITLE_PASSWORD.getValue();

    /**
     * Text for New Password title.
     */
    private static final String NLS_TITLENEWPASS = GordianMgrResource.TITLE_NEWPASS.getValue();

    /**
     * Text for Bad Password Error.
     */
    private static final String NLS_ERRORPASS = GordianMgrResource.ERROR_BADPASS.getValue();

    /**
     * Security factory.
     */
    private final GordianFactory theFactory;

    /**
     * List of resolved password hashes.
     */
    private final List<GordianCoreKeySetHash> theHashList;

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @throws OceanusException on error
     */
    protected GordianHashManager(final GordianParameters pParameters) throws OceanusException {
        /* Allocate the factory */
        final GordianFactoryGenerator myGenerator = new GordianGenerator();
        theFactory = myGenerator.newFactory(pParameters);

        /* Allocate a new Hash list */
        theHashList = new ArrayList<>();
    }

    /**
     * Obtain the security factory.
     * @return the security factory
     */
    public GordianFactory getSecurityFactory() {
        return theFactory;
    }

    /**
     * Create a new keySet Hash.
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash newKeySetHash(final String pSource) throws OceanusException {
        return resolveKeySetHash(null, pSource);
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
        GordianCoreKeySetHash myHash = null;
        final GordianKeySetFactory myKeySets = theFactory.getKeySetFactory();

        /* If the hash bytes exist try existing hashes for either absolute or password match */
        if (pHashBytes != null) {
            myHash = attemptKnownPasswords(pHashBytes);
        }

        /* If we have resolved the hash, return it */
        if (myHash != null) {
            return myHash;
        }

        /* Determine whether we need confirmation */
        boolean needConfirm = false;
        if (pHashBytes == null) {
            needConfirm = true;
        }

        /* Create the title for the window */
        final String myTitle;
        if (needConfirm) {
            myTitle = NLS_TITLENEWPASS
                    + " " + pSource;
        } else {
            myTitle = NLS_TITLEPASS
                    + " " + pSource;
        }

        /* Create a new password dialog */
        createTheDialog(myTitle, needConfirm);

        /* Prompt for the password */
        boolean isPasswordOk = false;
        char[] myPassword = null;
        while (showTheDialog()) {
            try {
                /* Access the password */
                myPassword = getPassword();

                /* Check the password */
                if (needConfirm) {
                    myHash = (GordianCoreKeySetHash) myKeySets.generateKeySetHash(myPassword);
                } else {
                    myHash = (GordianCoreKeySetHash) myKeySets.deriveKeySetHash(pHashBytes, myPassword);
                }

                /* No exception so we are good to go */
                isPasswordOk = true;

                /* Add the hash to the list and break the loop */
                theHashList.add(myHash);
                break;
            } catch (GordianBadCredentialsException e) {
                setError(NLS_ERRORPASS);
            } finally {
                if (myPassword != null) {
                    Arrays.fill(myPassword, (char) 0);
                    myPassword = null;
                }
            }
        }

        /* release password resources */
        releaseDialog();

        /* If we did not get a password */
        if (!isPasswordOk) {
            /* Throw an exception */
            throw new GordianDataException("Invalid Password");
        }

        /* Return the password hash */
        return myHash;
    }

    /**
     * Create the dialog.
     * @param pTitle the title
     * @param pNeedConfirm true/false
     */
    protected abstract void createTheDialog(String pTitle,
                                            boolean pNeedConfirm);

    /**
     * Show the dialog under an invokeAndWait clause.
     * @return successful dialog usage true/false
     */
    protected abstract boolean showTheDialog();

    /**
     * Release dialog.
     */
    protected abstract void releaseDialog();

    /**
     * Obtain the password.
     * @return the password
     */
    protected abstract char[] getPassword();

    /**
     * set the error for the dialog.
     * @param pError the error to display
     */
    protected abstract void setError(String pError);

    /**
     * obtain similar (same password) hash.
     * @param pHash the keySetHash to clone
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash similarKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
        /* clone the hash */
        final GordianCoreKeySetHash myHash = (GordianCoreKeySetHash) pHash.similarHash();

        /* Add the hash to the list */
        theHashList.add(myHash);

        /* Return the hash */
        return myHash;
    }

    /**
     * Attempt known passwords.
     * @param pHashBytes the HashBytes to attempt passwords for
     * @return the new PasswordHash if successful, otherwise null
     */
    private GordianCoreKeySetHash attemptKnownPasswords(final byte[] pHashBytes) {
        /* Look for the hash in the list */
        Iterator<GordianCoreKeySetHash> myIterator = theHashList.listIterator();
        while (myIterator.hasNext()) {
            /* Access hash */
            final GordianCoreKeySetHash myCurr = myIterator.next();

            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(pHashBytes, myCurr.getHash())) {
                return myCurr;
            }
        }

        /* Loop through the security controls */
        myIterator = theHashList.listIterator();
        while (myIterator.hasNext()) {
            /* Access hash */
            final GordianCoreKeySetHash myCurr = myIterator.next();

            /* Attempt to initialise the control from this password */
            final GordianCoreKeySetHash myPassHash = (GordianCoreKeySetHash) myCurr.attemptPasswordForHash(pHashBytes);

            /* If we succeeded */
            if (myPassHash != null) {
                /* Add the hash to the list and return it */
                theHashList.add(myPassHash);
                return myPassHash;
            }
        }

        /* Return null */
        return null;
    }

    /**
     * Obtain Maximum CipherSteps.
     * @param pFactory the factory type
     * @param pRestricted are the keys restricted
     * @return the maximum
     */
    public static int getMaximumCipherSteps(final GordianFactoryType pFactory,
                                            final boolean pRestricted) {
        return 6;
        //return GordianFactoryType.BC.equals(pFactory)
        //       ? BouncyFactory.getMaximumCipherSteps(pRestricted)
        //       : JcaFactory.getMaximumCipherSteps(pRestricted);
    }

    /**
     * Obtain KeyWrapSize.
     * @return the maximum keyWrap size
     */
    public static int getMaximumKeyWrapSize() {
        final int myMaxCipherSteps = getMaximumCipherSteps(GordianFactoryType.BC, false);
        return GordianCoreKeySet.getKeyWrapExpansion(myMaxCipherSteps)
                + GordianCoreFactory.getKeyLength(false) / Byte.SIZE;
    }
}
