/*******************************************************************************
 * GordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIPasswordDialog;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * DialogControl.
 */
public class GordianBaseDialogControl
        implements GordianDialogController {
    /**
     * GUI factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * Password dialog.
     */
    private TethysUIPasswordDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianBaseDialogControl(final TethysUIFactory<?> pFactory) {
        theFactory = pFactory;
    }

    @Override
    public void createTheDialog(final String pTitle,
                                final boolean pNeedConfirm) {
        /* Create the dialog */
        theDialog = theFactory.dialogFactory().newPasswordDialog(pTitle, pNeedConfirm);
    }

    @Override
    public boolean showTheDialog() {
        return theDialog.showDialog();
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
