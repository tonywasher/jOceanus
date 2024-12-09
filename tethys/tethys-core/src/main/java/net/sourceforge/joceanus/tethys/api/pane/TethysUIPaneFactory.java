/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.api.pane;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;

/**
 * Pane Factory.
 */
public interface TethysUIPaneFactory {
    /**
     * Obtain a new borderPane manager.
     * @return the new manager
     */
    TethysUIBorderPaneManager newBorderPane();

    /**
     * Obtain a new horizontal boxPane manager.
     * @return the new manager
     */
    TethysUIBoxPaneManager newHBoxPane();

    /**
     * Obtain a new vertical boxPane manager.
     * @return the new manager
     */
    TethysUIBoxPaneManager newVBoxPane();

    /**
     * Obtain a new cardPane manager.
     * @param <P> the card panel type
     * @return the new manager
     */
    <P extends TethysUIComponent> TethysUICardPaneManager<P> newCardPane();

    /**
     * Obtain a new flowPane manager.
     * @return the new manager
     */
    TethysUIFlowPaneManager newFlowPane();

    /**
     * Obtain a new gridPane manager.
     * @return the new manager
     */
    TethysUIGridPaneManager newGridPane();

    /**
     * Obtain a new scrollPane manager.
     * @return the new manager
     */
    TethysUIScrollPaneManager newScrollPane();

    /**
     * Obtain a new tabPane manager.
     * @return the new manager
     */
    TethysUITabPaneManager newTabPane();
}
