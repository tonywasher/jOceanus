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
package net.sourceforge.joceanus.jgordianknot.manager.swing;

import javax.swing.JFrame;

import net.sourceforge.joceanus.jgordianknot.crypto.SecurityParameters;
import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password hashes that were not previously resolved, previously used
 * passwords will be attempted. If no match is found, then the user will be prompted for the password.
 */
public class SwingSecureManager
        extends SecureManager {
    /**
     * Frame to use for password dialog.
     */
    private JFrame theFrame = null;

    /**
     * Password dialog.
     */
    private SwingPasswordDialog theDialog = null;

    /**
     * Constructor for default values.
     * @throws JOceanusException on error
     */
    public SwingSecureManager() throws JOceanusException {
        super();
    }

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @throws JOceanusException on error
     */
    public SwingSecureManager(final SecurityParameters pParameters) throws JOceanusException {
        super(pParameters);
    }

    /**
     * Set the Frame for the Secure Manager.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theFrame = pFrame;
    }

    @Override
    protected void createTheDialog(final String pTitle,
                                   final boolean pNeedConfirm) {
        theDialog = new SwingPasswordDialog(theFrame, pTitle, pNeedConfirm);
    }

    @Override
    protected boolean showTheDialog() {
        return SwingPasswordDialog.showTheDialog(theDialog);
    }

    @Override
    protected char[] getPassword() {
        return theDialog.getPassword();
    }

    @Override
    protected void setError(final String pError) {
        theDialog.setError(pError);
    }

    @Override
    protected void releaseDialog() {
        theDialog.release();
        theDialog = null;
    }
}