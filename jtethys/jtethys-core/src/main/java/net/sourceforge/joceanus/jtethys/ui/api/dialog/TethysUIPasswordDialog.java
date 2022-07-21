/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.api.dialog;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public interface TethysUIPasswordDialog {
    /**
     * Show the dialog under an invokeAndWait clause.
     *
     * @return successful dialog usage true/false
     */
    boolean showDialog();

    /**
     * Obtain the password.
     * @return the password
     */
    char[] getPassword();

    /**
     * Is the password set.
     * @return true/false
     */
    boolean isPasswordSet();

    /**
     * Release resources.
     */
    void release();

    /**
     * report a bad password.
     */
    void reportBadPassword();
}
