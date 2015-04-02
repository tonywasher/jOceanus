/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.CryptoResource;
import net.sourceforge.joceanus.jgordianknot.crypto.InvalidCredentialsException;
import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityParameters;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password hashes that were not previously resolved, previously used
 * passwords will be attempted. If no match is found, then the user will be prompted for the password.
 */
public abstract class SecureManager {
    /**
     * Text for Password title.
     */
    private static final String NLS_TITLEPASS = CryptoResource.TITLE_PASSWORD.getValue();

    /**
     * Text for New Password title.
     */
    private static final String NLS_TITLENEWPASS = CryptoResource.TITLE_NEWPASS.getValue();

    /**
     * Text for Bad Password Error.
     */
    private static final String NLS_ERRORPASS = CryptoResource.ERROR_BADPASS.getValue();

    /**
     * Security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * List of resolved password hashes.
     */
    private final List<PasswordHash> theHashList;

    /**
     * Constructor for default values.
     * @throws JOceanusException on error
     */
    protected SecureManager() throws JOceanusException {
        /* Access with defaults */
        this(new SecurityParameters());
    }

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @throws JOceanusException on error
     */
    protected SecureManager(final SecurityParameters pParameters) throws JOceanusException {
        /* Allocate the security generator */
        theGenerator = new SecurityGenerator(pParameters);

        /* Allocate a new Hash list */
        theHashList = new ArrayList<PasswordHash>();
    }

    /**
     * Obtain the security generator.
     * @return the security generator
     */
    public SecurityGenerator getSecurityGenerator() {
        return theGenerator;
    }

    /**
     * Resolve the password Hash.
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the password Hash
     * @throws JOceanusException on error
     */
    public PasswordHash resolvePasswordHash(final byte[] pHashBytes,
                                            final String pSource) throws JOceanusException {
        PasswordHash myHash = null;

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
        char[] myPassword = null;

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
                    myHash = theGenerator.generatePasswordHash(myPassword);
                } else {
                    myHash = theGenerator.derivePasswordHash(pHashBytes, myPassword);
                }

                /* No exception so we are good to go */
                isPasswordOk = true;

                /* Add the hash to the list and break the loop */
                theHashList.add(myHash);
                break;
            } catch (InvalidCredentialsException e) {
                setError(NLS_ERRORPASS);
            }
        }

        /* release password resources */
        releaseDialog();

        /* If we did not get a password */
        if (!isPasswordOk) {
            /* Throw an exception */
            throw new JGordianDataException("Invalid Password");
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
     * clone password hash.
     * @param pHash the password hash to clone
     * @return the cloned password hash
     * @throws JOceanusException on error
     */
    public PasswordHash clonePasswordHash(final PasswordHash pHash) throws JOceanusException {
        /* clone the hash */
        PasswordHash myHash = pHash.cloneHash();

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
    private PasswordHash attemptKnownPasswords(final byte[] pHashBytes) {
        Iterator<PasswordHash> myIterator;
        PasswordHash myCurr;
        PasswordHash myPassHash;

        /* Access the iterator */
        myIterator = theHashList.listIterator();

        /* Loop through the security controls */
        while (myIterator.hasNext()) {
            /* Access hash */
            myCurr = myIterator.next();

            /* If this is the hash we are looking for, return it */
            if (Arrays.equals(pHashBytes, myCurr.getHashBytes())) {
                return myCurr;
            }

            /* Attempt to initialise the control from this password */
            myPassHash = myCurr.attemptPassword(pHashBytes);

            /* Break loop if we matched */
            if (myPassHash != null) {
                return myPassHash;
            }
        }

        /* Return null */
        return null;
    }
}
