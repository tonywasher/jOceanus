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
package net.sourceforge.joceanus.jtethys.ui.api.base;

/**
 * Component definition.
 */
public interface TethysUIComponent {
    /**
     * Obtain the Id.
     * @return the id
     */
    Integer getId();

    /**
     * Obtain the Node.
     * @return the node
     */
    TethysUINode getNode();

    /**
     * Set Enabled status.
     * @param pEnabled true/false
     */
    void setEnabled(boolean pEnabled);

    /**
     * Set Visible.
     * @param pVisible true/false
     */
    void setVisible(boolean pVisible);

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    Integer getBorderPadding();

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    String getBorderTitle();

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    void setBorderPadding(Integer pPadding);

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    void setBorderTitle(String pTitle);

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    void setPreferredHeight(Integer pHeight);
}
