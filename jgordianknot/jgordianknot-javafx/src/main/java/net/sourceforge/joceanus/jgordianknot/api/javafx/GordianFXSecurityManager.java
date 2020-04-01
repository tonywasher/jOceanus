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
package net.sourceforge.joceanus.jgordianknot.api.javafx;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.util.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * PasswordHash Manager class which holds a cache of all resolved password hashes. For password
 * hashes that were not previously resolved, previously used passwords will be attempted. If no
 * match is found, then the user will be prompted for the password.
 */
public class GordianFXSecurityManager
        extends GordianSecurityManager {
    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @param pKeySetSpec the keySetSpec
     * @throws OceanusException on error
     */
    public GordianFXSecurityManager(final TethysFXGuiFactory pFactory,
                                    final GordianFactoryType pFactoryType,
                                    final char[] pSecurityPhrase,
                                    final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        super(pFactoryType, pSecurityPhrase, pKeySetSpec, new GordianFXDialogControl(pFactory));
    }

    /**
     * javaFX DialogControl.
     */
    private static class GordianFXDialogControl
            implements GordianDialogController {
        /**
         * GUI factory.
         */
        private final TethysFXGuiFactory theFactory;

        /**
         * Password dialog.
         */
        private GordianFXPasswordDialog theDialog;

        /**
         * Constructor.
         * @param pFactory the factory
         */
        GordianFXDialogControl(final TethysFXGuiFactory pFactory) {
            theFactory = pFactory;
        }

        @Override
        public void createTheDialog(final String pTitle,
                                    final boolean pNeedConfirm) {
            /* Create the title for the window */
            final String myTitle = pNeedConfirm
                                    ? NLS_TITLENEWPASS + " " + pTitle
                                    : NLS_TITLEPASS + " " + pTitle;
            theDialog = GordianFXPasswordDialog.createTheDialog(theFactory, myTitle, pNeedConfirm);
        }

        @Override
        public boolean showTheDialog() {
            return GordianFXPasswordDialog.showTheDialog(theDialog);
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
