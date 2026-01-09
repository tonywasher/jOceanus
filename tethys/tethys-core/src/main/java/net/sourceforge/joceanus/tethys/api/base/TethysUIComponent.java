/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.base;

/**
 * Component definition.
 */
public interface TethysUIComponent {
    /**
     * Obtain the underlying component (if any).
     * @return the underlying component
     */
    TethysUIComponent getUnderlying();

    /**
     * Obtain the Id.
     * @return the id
     */
    default Integer getId() {
        return getUnderlying().getId();
    }

    /**
     * Obtain the Node.
     * @return the node
     */
    default TethysUINode getNode() {
        return getUnderlying().getNode();
    }

    /**
     * Set Enabled status.
     * @param pEnabled true/false
     */
    default void setEnabled(final boolean pEnabled) {
        getUnderlying().setEnabled(pEnabled);
    }

    /**
     * Set Visible.
     * @param pVisible true/false
     */
    default void setVisible(final boolean pVisible) {
        getUnderlying().setVisible(pVisible);
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    default Integer getBorderPadding() {
        return getUnderlying().getBorderPadding();
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    default String getBorderTitle() {
        return getUnderlying().getBorderTitle();
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    default void setBorderPadding(final Integer pPadding) {
        getUnderlying().setBorderPadding(pPadding);
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    default void setBorderTitle(final String pTitle) {
        getUnderlying().setBorderTitle(pTitle);
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    default void setPreferredWidth(final Integer pWidth) {
        getUnderlying().setPreferredWidth(pWidth);
    }

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    default void setPreferredHeight(final Integer pHeight) {
        getUnderlying().setPreferredHeight(pHeight);
    }
}
