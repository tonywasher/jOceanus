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
package net.sourceforge.joceanus.jgordianknot.api.swing;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public class GordianSwingSecurityManager
        extends GordianSecurityManager {
    /**
     * GUI factory.
     */
    private final TethysSwingGuiFactory theFactory;

    /**
     * Password dialog.
     */
    private GordianSwingPasswordDialog theDialog;

    /**
     * Constructor for default values.
     * @throws OceanusException on error
     */
    public GordianSwingSecurityManager() throws OceanusException {
        this(new TethysSwingGuiFactory());
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @throws OceanusException on error
     */
    public GordianSwingSecurityManager(final TethysSwingGuiFactory pFactory) throws OceanusException {
        this(pFactory, new GordianParameters());
    }

    /**
     * Constructor.
     * @param pParameters the Security parameters
     * @throws OceanusException on error
     */
    public GordianSwingSecurityManager(final GordianParameters pParameters) throws OceanusException {
        this(new TethysSwingGuiFactory(), pParameters);
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pParameters the Security parameters
     * @throws OceanusException on error
     */
    public GordianSwingSecurityManager(final TethysSwingGuiFactory pFactory,
                                       final GordianParameters pParameters) throws OceanusException {
        super(pParameters);
        theFactory = pFactory;
    }

    @Override
    protected void createTheDialog(final String pTitle,
                                   final boolean pNeedConfirm) {
        theDialog = new GordianSwingPasswordDialog(theFactory, pTitle, pNeedConfirm);
    }

    @Override
    protected boolean showTheDialog() {
        return GordianSwingPasswordDialog.showTheDialog(theDialog);
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
