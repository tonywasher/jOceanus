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
package net.sourceforge.joceanus.tethys.api.pane;

import java.util.Iterator;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;

/**
 * Box Pane Manager.
 */
public interface TethysUIBoxPaneManager
        extends TethysUIComponent {
    /**
     * Obtain the Gap.
     * @return the Gap.
     */
    Integer getGap();

    /**
     * Set the Gap.
     * @param pGap the Gap.
     */
    void setGap(Integer pGap);

    /**
     * Add spacer.
     */
    void addSpacer();

    /**
     * Add strut.
     */
    void addStrut();

    /**
     * Add Node.
     * @param pNode the node
     */
    void addNode(TethysUIComponent pNode);

    /**
     * Obtain List iterator.
     * @return the iterator
     */
    Iterator<TethysUIComponent> iterator();
}
