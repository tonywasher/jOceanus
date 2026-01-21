/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data.storage;

import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityDialogController;

/**
 * Dialog stub.
 */
public class MoneyWiseNullPasswordDialog
        implements PrometheusSecurityDialogController {
    @Override
    public void createTheDialog(final String pTitle,
                                final boolean pNeedConfirm) {
    }

    @Override
    public boolean showTheDialog() {
        return true;
    }

    @Override
    public void releaseDialog() {
    }

    @Override
    public char[] getPassword() {
        return "DummyPassword1_".toCharArray();
    }

    @Override
    public void reportBadPassword(final String pError) {
    }

    @Override
    public void showTheSpinner(final boolean pShow) {
    }
}
