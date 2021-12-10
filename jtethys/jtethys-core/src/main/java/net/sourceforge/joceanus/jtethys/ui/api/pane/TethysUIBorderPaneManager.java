/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.pane;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;

/**
 * Border Pane Manager.
 */
public interface TethysUIBorderPaneManager
        extends TethysUIComponent {
    /**
     * Obtain the Horizontal Grid Gap.
     * @return the GridGap.
     */
    Integer getHGap();

    /**
     * Obtain the Vertical Grid Gap.
     * @return the GridGap.
     */
    Integer getVGap();

    /**
     * Set the Horizontal Grid Gap.
     * @param pGap the GridGap.
     */
    void setHGap(final Integer pGap);

    /**
     * Set the Vertical Grid Gap.
     * @param pGap the GridGap.
     */
    void setVGap(final Integer pGap);

    /**
     * Set the Centre Node.
     * @param pNode the node
     */
    void setCentre(TethysUIComponent pNode);

    /**
     * Set the North Node.
     * @param pNode the node
     */
    void setNorth(TethysUIComponent pNode);

    /**
     * Set the South Node.
     * @param pNode the node
     */
    void setSouth(TethysUIComponent pNode);

    /**
     * Set the West Node.
     * @param pNode the node
     */
    void setWest(TethysUIComponent pNode);

    /**
     * Set the East Node.
     * @param pNode the node
     */
    void setEast(TethysUIComponent pNode);
}
