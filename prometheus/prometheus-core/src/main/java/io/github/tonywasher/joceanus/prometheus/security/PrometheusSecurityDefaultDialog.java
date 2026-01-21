/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.security;

import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIBusySpinner;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIDialogFactory;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIPasswordDialog;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;

/**
 * DialogControl.
 */
public class PrometheusSecurityDefaultDialog
        implements PrometheusSecurityDialogController {
    /**
     * GUI factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * Password dialog.
     */
    private TethysUIPasswordDialog theDialog;

    /**
     * Busy Spinner.
     */
    private TethysUIBusySpinner theBusy;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public PrometheusSecurityDefaultDialog(final TethysUIFactory<?> pFactory) {
        theFactory = pFactory;
    }

    @Override
    public void createTheDialog(final String pTitle,
                                final boolean pNeedConfirm) {
        /* Create the dialog */
        final TethysUIDialogFactory myDialogs = theFactory.dialogFactory();
        theDialog = myDialogs.newPasswordDialog(pTitle, pNeedConfirm);
        theBusy = myDialogs.newBusySpinner();
    }

    @Override
    public boolean showTheDialog() {
        theBusy.closeDialog();
        return theDialog.showDialog();
    }

    @Override
    public void showTheSpinner(final boolean pShow) {
        if (pShow) {
            theBusy.showDialog();
        } else {
            theBusy.closeDialog();
        }
    }

    @Override
    public char[] getPassword() {
        return theDialog.getPassword();
    }

    @Override
    public void reportBadPassword(final String pError) {
        theDialog.reportBadPassword(pError);
    }

    @Override
    public void releaseDialog() {
        theBusy.closeDialog();
        theDialog.release();
        theDialog = null;
    }
}
