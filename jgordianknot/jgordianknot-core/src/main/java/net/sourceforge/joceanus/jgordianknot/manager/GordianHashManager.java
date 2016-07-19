/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaFactory;
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
    private final List<GordianKeySetHash> theHashList;

    /**
     * Constructor for default values.
     * @throws OceanusException on error
     */
    protected GordianHashManager() throws OceanusException {
        /* Access with defaults */
        this(new GordianParameters());
    }

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @throws OceanusException on error
     */
    protected GordianHashManager(final GordianParameters pParameters) throws OceanusException {
        /* Allocate the factory */
        theFactory = (pParameters.getFactoryType() == GordianFactoryType.BC)
                                                                             ? new BouncyFactory(pParameters)
                                                                             : new JcaFactory(pParameters);

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
     * Resolve the keySet Hash.
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the password Hash
     * @throws OceanusException on error
     */
    public GordianKeySetHash resolveKeySetHash(final byte[] pHashBytes,
                                               final String pSource) throws OceanusException {
        GordianKeySetHash myHash = null;

        /* If the hash bytes exist try existing hashes for either absolute or password match */
        if (pHashBytes != null) {
            myHash = attemptKnownPasswords(pHashBytes);
        }

        /* If we have resolved the hash, return it */
        if (myHash != null) {
            return myHash;
        }

        /* Prepare to prompt for password */
        String myTitle;
        boolean needConfirm = false;
        char[] myPassword;

        /* Determine whether we need confirmation */
        if (pHashBytes == null) {
            needConfirm = true;
        }

        /* Create the title for the window */
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
        while (showTheDialog()) {
            try {
                /* Access the password */
                myPassword = getPassword();

                /* Check the password */
                if (needConfirm) {
                    myHash = theFactory.generateKeySetHash(myPassword);
                } else {
                    myHash = theFactory.deriveKeySetHash(pHashBytes, myPassword);
                }

                /* No exception so we are good to go */
                isPasswordOk = true;

                /* Add the hash to the list and break the loop */
                theHashList.add(myHash);
                break;
            } catch (GordianBadCredentialsException e) {
                setError(NLS_ERRORPASS);
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
    protected abstract void createTheDialog(final String pTitle,
                                            final boolean pNeedConfirm);

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
    protected abstract void setError(final String pError);

    /**
     * obtain similar (same password) hash.
     * @param pHash the keySetHash to clone
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    public GordianKeySetHash similarKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
        /* clone the hash */
        GordianKeySetHash myHash = pHash.similarHash();

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
    private GordianKeySetHash attemptKnownPasswords(final byte[] pHashBytes) {
        /* Look for the has in the list */
        Iterator<GordianKeySetHash> myIterator = theHashList.listIterator();
        while (myIterator.hasNext()) {
            /* Access hash */
            GordianKeySetHash myCurr = myIterator.next();

            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(pHashBytes, myCurr.getHash())) {
                return myCurr;
            }
        }

        /* Loop through the security controls */
        myIterator = theHashList.listIterator();
        while (myIterator.hasNext()) {
            /* Access hash */
            GordianKeySetHash myCurr = myIterator.next();

            /* Attempt to initialise the control from this password */
            GordianKeySetHash myPassHash = myCurr.attemptPassword(pHashBytes);

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
     * Obtain Digest predicate.
     * @param pFactory the factory type
     * @return the predicate
     */
    public static Predicate<GordianDigestType> getDigestPredicate(final GordianFactoryType pFactory) {
        return GordianFactoryType.BC.equals(pFactory)
                                                      ? BouncyFactory.generateDigestPredicate()
                                                      : JcaFactory.generateDigestPredicate();
    }

    /**
     * Obtain Maximum CipherSteps.
     * @param pFactory the factory type
     * @param pRestricted are the keys restricted
     * @return the maximum
     */
    public static int getMaximumCipherSteps(final GordianFactoryType pFactory,
                                            final boolean pRestricted) {
        return GordianFactoryType.BC.equals(pFactory)
                                                      ? BouncyFactory.getMaximumCipherSteps(pRestricted)
                                                      : JcaFactory.getMaximumCipherSteps(pRestricted);
    }
}
