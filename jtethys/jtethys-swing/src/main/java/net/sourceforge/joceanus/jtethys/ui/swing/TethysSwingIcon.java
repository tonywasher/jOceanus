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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jtethys.ui.TethysIcon;

/**
 * Swing Icon.
 */
public class TethysSwingIcon
        implements TethysIcon {
    /**
     * The icon.
     */
    private final Icon theIcon;

    /**
     * Constructor.
     * @param pIcon the icon
     */
    TethysSwingIcon(final Icon pIcon) {
        theIcon = pIcon;
    }

    /**
     * Obtain the icon that this represents.
     * @return the icon
     */
    Icon getIcon() {
        return theIcon;
    }

    /**
     * Obtain the image that this represents.
     * @return the image
     */
    Image getImage() {
        return theIcon instanceof ImageIcon
               ? ((ImageIcon) theIcon).getImage()
               : null;
    }

    /**
     * Obtain the icon.
     * @param pIcon the Tethys icon
     * @return the Swing icon.
     */
    public static Icon getIcon(final TethysIcon pIcon) {
        return pIcon == null
               ? null
               : ((TethysSwingIcon) pIcon).getIcon();
    }
}
