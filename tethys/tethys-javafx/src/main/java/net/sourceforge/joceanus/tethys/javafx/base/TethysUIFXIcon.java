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
package net.sourceforge.joceanus.tethys.javafx.base;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;

/**
 * javaFX Icon.
 */
public class TethysUIFXIcon
        implements TethysUIIcon {
    /**
     * The icon.
     */
    private final Node theIcon;

    /**
     * Constructor.
     *
     * @param pIcon the icon
     */
    TethysUIFXIcon(final Node pIcon) {
        theIcon = pIcon;
    }

    /**
     * Obtain the icon that this represents.
     *
     * @return the icon
     */
    public Node getIcon() {
        return theIcon;
    }

    /**
     * Obtain the icon that this represents.
     *
     * @return the icon
     */
    public Image getImage() {
        return theIcon instanceof ImageView myView
                ? myView.getImage()
                : null;
    }

    /**
     * Obtain the icon that this represents.
     *
     * @return the icon
     */
    ImageView getImageView() {
        return theIcon instanceof ImageView myView
                ? myView
                : null;
    }

    /**
     * Obtain the icon.
     *
     * @param pIcon the Tethys icon
     * @return the javaFX icon.
     */
    public static Node getIcon(final TethysUIIcon pIcon) {
        return pIcon == null
                ? null
                : ((TethysUIFXIcon) pIcon).getIcon();
    }
}
