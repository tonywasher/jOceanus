/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.api.dialog;

/**
 * Dialog Factory.
 */
public interface TethysUIDialogFactory {
    /**
     * Obtain a new fileSelector.
     * @return the new selector
     */
    TethysUIFileSelector newFileSelector();

    /**
     * Obtain a new directorySelector.
     * @return the new selector
     */
    TethysUIDirectorySelector newDirectorySelector();

    /**
     * Obtain a new aboutBox.
     * @return the new box
     */
    TethysUIAboutBox newAboutBox();

    /**
     * Obtain a new passwordDialog.
     * @param pTitle       the title
     * @param pNeedConfirm true/false
     * @return the new dialog
     */
    TethysUIPasswordDialog newPasswordDialog(String pTitle,
                                             boolean pNeedConfirm);

    /**
     * Obtain a new alert.
     * @return the new alert
     */
    TethysUIAlert newAlert();

    /**
     * Obtain a new child dialog.
     * @return the new child dialog
     */
    TethysUIChildDialog newChildDialog();
}
