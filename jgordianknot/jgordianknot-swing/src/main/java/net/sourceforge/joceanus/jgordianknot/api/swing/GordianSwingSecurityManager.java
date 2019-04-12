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
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
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
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pParameters the Security parameters
     * @param pKeySetSpec the keySetSpec
     * @throws OceanusException on error
     */
    public GordianSwingSecurityManager(final TethysSwingGuiFactory pFactory,
                                       final GordianParameters pParameters,
                                       final GordianKeySetSpec pKeySetSpec) throws OceanusException {
        super(pParameters, pKeySetSpec, new GordianSwingDialogControl(pFactory));
    }

    /**
     * swing DialogControl.
     */
    private static class GordianSwingDialogControl
            implements GordianDialogController {
        /**
         * GUI factory.
         */
        private final TethysSwingGuiFactory theFactory;

        /**
         * Password dialog.
         */
        private GordianSwingPasswordDialog theDialog;

        /**
         * Constructor.
         * @param pFactory the factory
         */
        GordianSwingDialogControl(final TethysSwingGuiFactory pFactory) {
            theFactory = pFactory;
        }

        @Override
        public void createTheDialog(final String pTitle,
                                    final boolean pNeedConfirm) {
            /* Create the title for the window */
            final String myTitle = pNeedConfirm
                                   ? NLS_TITLENEWPASS + " " + pTitle
                                   : NLS_TITLEPASS + " " + pTitle;
            theDialog = new GordianSwingPasswordDialog(theFactory, myTitle, pNeedConfirm);
        }

        @Override
        public boolean showTheDialog() {
            return GordianSwingPasswordDialog.showTheDialog(theDialog);
        }

        @Override
        public char[] getPassword() {
            return theDialog.getPassword();
        }

        @Override
        public void setError(final String pError) {
            theDialog.setError(pError);
        }

        @Override
        public void releaseDialog() {
            theDialog.release();
            theDialog = null;
        }
    }
}
