/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.security;

/**
 * Dialogue Controller class.
 */
public interface PrometheusSecurityDialogController {
    /**
     * Create the dialog.
     * @param pTitle the title
     * @param pNeedConfirm true/false
     */
    void createTheDialog(String pTitle,
                         boolean pNeedConfirm);

    /**
     * Show the dialog under an invokeAndWait clause.
     * @return successful dialog usage true/false
     */
    boolean showTheDialog();

    /**
     * Show the spinner.
     * @param pShow show spinner true/false
     */
    void showTheSpinner(boolean pShow);

    /**
     * Release dialog.
     */
    void releaseDialog();

    /**
     * Obtain the password.
     * @return the password
     */
    char[] getPassword();

    /**
     * report bad password.
     * @param pError the error
     */
    void reportBadPassword(String pError);
}
