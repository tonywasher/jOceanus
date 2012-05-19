/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

public class SecureManager {
    /**
     * Security generator
     */
    private final SecurityGenerator theGenerator;

    /**
     * List of resolved password hashes
     */
    private final List<PasswordHash> theHashList;

    /**
     * Frame to use for password dialog
     */
    private JFrame theFrame = null;

    /* Constructor */
    public SecureManager() {
        /* Allocate the security generator */
        theGenerator = new SecurityGenerator();

        /* Allocate a new Hash list */
        theHashList = new ArrayList<PasswordHash>();
    }

    /**
     * Set the Frame for the Secure Manager
     * @param pFrame the frame
     */
    public void setFrame(JFrame pFrame) {
        theFrame = pFrame;
    }

    /**
     * Obtain the security generator
     * @return the security generator
     */
    public SecurityGenerator getSecurityGenerator() {
        return theGenerator;
    }

    /**
     * Resolve the password Hash
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the password Hash
     * @throws ModelException
     */
    public PasswordHash resolvePasswordHash(byte[] pHashBytes,
                                            String pSource) throws ModelException {
        PasswordHash myHash = null;

        /* If the hash bytes exist try existing hashes for either absolute or password match */
        if (pHashBytes != null)
            myHash = attemptKnownPasswords(pHashBytes);

        /* If we have resolved the hash, return it */
        if (myHash != null)
            return myHash;

        /* Prepare to prompt for password */
        String myTitle;
        boolean needConfirm = false;
        char[] myPassword = null;

        /* Determine whether we need confirmation */
        if (pHashBytes == null)
            needConfirm = true;

        /* Create the title for the window */
        if (needConfirm)
            myTitle = "Enter New Password for " + pSource;
        else
            myTitle = "Enter Password for " + pSource;

        /* Create a new password dialog */
        PasswordDialog myPass = new PasswordDialog(theFrame, myTitle, needConfirm);

        /* Prompt for the password */
        boolean isPasswordOk = false;
        while (showDialog(myPass)) {
            try {
                /* Access the password */
                myPassword = myPass.getPassword();

                /* Check the password */
                if (needConfirm)
                    myHash = theGenerator.generatePasswordHash(myPassword);
                else
                    myHash = theGenerator.derivePasswordHash(pHashBytes, myPassword);

                /* No exception so we are good to go */
                isPasswordOk = true;

                /* Add the hash to the list and break the loop */
                theHashList.add(myHash);
                break;
            } catch (WrongPasswordException e) {
                myPass.setError("Incorrect password. Please re-enter");
                continue;
            } catch (ModelException e) {
                throw e;
            } finally {
                /* Clear out the password */
                if (myPassword != null)
                    Arrays.fill(myPassword, (char) 0);
            }
        }

        /* If we did not get a password */
        if (!isPasswordOk) {
            /* Throw an exception */
            throw new ModelException(ExceptionClass.DATA, "Invalid Password");
        }

        /* Return the password hash */
        return myHash;
    }

    /**
     * Show the dialog under an invokeAndWait clause
     * @param pDialog the dialog to show
     * @return successful dialog usage true/false
     */
    public boolean showDialog(final PasswordDialog pDialog) {

        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            pDialog.showDialog();
        }

        /* else we must use invokeAndWait */
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        /* invoke the dialog */
                        pDialog.showDialog();
                    }
                });
            }

            catch (Exception e) {
            }
        }

        /* Return to caller */
        return pDialog.isPasswordSet();
    }

    /**
     * clone password hash
     * @param pHash the password hash to clone
     * @return the cloned password hash
     * @throws ModelException
     */
    public PasswordHash clonePasswordHash(PasswordHash pHash) throws ModelException {
        /* clone the hash */
        PasswordHash myHash = pHash.cloneHash();

        /* Add the hash to the list */
        theHashList.add(myHash);

        /* Return the hash */
        return myHash;
    }

    /**
     * Attempt known passwords
     * @param pHashBytes the HashBytes to attempt passwords for
     * @return the new PasswordHash if successful, otherwise null
     */
    private PasswordHash attemptKnownPasswords(byte[] pHashBytes) {
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
            if (Arrays.equals(pHashBytes, myCurr.getHashBytes()))
                return myCurr;

            /* Attempt to initialise the control from this password */
            myPassHash = myCurr.attemptPassword(pHashBytes);

            /* Break loop if we matched */
            if (myPassHash != null)
                return myPassHash;
        }

        /* Return null */
        return null;
    }
}
