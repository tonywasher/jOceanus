/*
 * Metis: Java Data Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.metis.viewer;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;

/**
 * Viewer Manager class, responsible for displaying the debug view.
 */
public interface MetisViewerWindow
        extends OceanusEventProvider<TethysUIEvent> {
    /**
     * show the dialog.
     */
    void showDialog();

    /**
     * Handle the parent page.
     */
    void handleParentPage();

    /**
     * Handle the next page.
     */
    void handleNextPage();

    /**
     * Handle the previous page.
     */
    void handlePreviousPage();

    /**
     * Handle the explicit page.
     *
     * @param pIndex the index of the page
     */
    void handleExplicitPage(int pIndex);

    /**
     * Handle the mode.
     *
     * @param pMode the new mode
     */
    void handleMode(MetisViewerMode pMode);
}
