/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.dialog;

import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;

/**
 * Child Dialog.
 */
public interface TethysUIChildDialog
    extends OceanusEventProvider<TethysUIEvent> {
    /**
     * Set the title.
     * @param pTitle the title
     */
    void setTitle(String pTitle);

    /**
     * Set the contents.
     * @param pContent the dialog content
     */
    void setContent(TethysUIComponent pContent);

    /**
     * Show the dialog.
     */
    void showDialog();

    /**
     * Is the dialog showing?
     * @return true/false
     */
    boolean isShowing();

    /**
     * Hide the dialog.
     */
    void hideDialog();

    /**
     * Close the dialog.
     */
    void closeDialog();
}
