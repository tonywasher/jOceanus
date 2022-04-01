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
package net.sourceforge.joceanus.jgordianknot.api.swing;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public final class GordianSwingPasswordManager {
    /**
     * Private Constructor.
     */
    private GordianSwingPasswordManager() {
    }

    /**
     * Create a password Manager.
     * @param pFactory the GUI Factory
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @param pKeySetSpec the keySetSpec
     * @return the password Manager
     * @throws OceanusException on error
     */
    public static GordianPasswordManager newPasswordManager(final TethysSwingGuiFactory pFactory,
                                                            final GordianFactoryType pFactoryType,
                                                            final char[] pSecurityPhrase,
                                                            final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        final GordianFactory myFactory = GordianGenerator.createFactory(pFactoryType, pSecurityPhrase);
        final GordianDialogController myController = new GordianSwingDialogControl(pFactory);
        return GordianGenerator.newPasswordManager(myFactory, pKeySetSpec, myController);
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
            /* Create the dialog */
            theDialog = new GordianSwingPasswordDialog(theFactory, pTitle, pNeedConfirm);
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
        public void reportBadPassword() {
            theDialog.reportBadPassword();
        }

        @Override
        public void releaseDialog() {
            theDialog.release();
            theDialog = null;
        }
    }
}
