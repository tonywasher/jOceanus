/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;

/**
 * Utility class to manage status icon buttons.
 */
public abstract class PrometheusIcons {
    /**
     * Default icon extra size.
     */
    protected static final int ICON_XTRA_SIZE = 8;

    /**
     * The active icon.
     */
    private static final Icon ICON_ACTIVE = resizeImage(new ImageIcon(JDataTable.class.getResource("Active.png")), ICON_XTRA_SIZE);

    /**
     * The delete icon.
     */
    private static final Icon ICON_DELETE = resizeImage(new ImageIcon(JDataTable.class.getResource("RedDelete.png")), ICON_XTRA_SIZE);

    /**
     * The disabled icon.
     */
    private static final Icon ICON_DISABLED = resizeImage(new ImageIcon(StaticDataTable.class.getResource("Disabled.png")));

    /**
     * Resize an icon to the row height.
     * @param pSource the source icon
     * @return the resized icon
     */
    protected static Icon resizeImage(final ImageIcon pSource) {
        return resizeImage(pSource, 0);
    }

    /**
     * Resize an icon to the row height.
     * @param pSource the source icon
     * @param pExtraSize the extra size
     * @return the resized icon
     */
    protected static Icon resizeImage(final ImageIcon pSource,
                                      final int pExtraSize) {
        Image myImage = pSource.getImage();
        Image myNewImage = myImage.getScaledInstance(JDataTable.ROW_HEIGHT + pExtraSize,
                JDataTable.ROW_HEIGHT + pExtraSize,
                Image.SCALE_SMOOTH);
        return new ImageIcon(myNewImage);
    }

    /**
     * Build status button state.
     * @param pState the button state
     */
    public static void buildStatusButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the status iconButton */
        pState.setIconForValue(Boolean.FALSE, ICON_DELETE);
        pState.setIconForValue(Boolean.TRUE, ICON_ACTIVE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
    }

    /**
     * Build enabled button state.
     * @param pState the button state
     */
    public static void buildEnabledButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the status iconButton */
        pState.setIconForValue(Boolean.FALSE, ICON_DISABLED);
        pState.setIconForValue(Boolean.TRUE, ICON_ACTIVE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }
}
