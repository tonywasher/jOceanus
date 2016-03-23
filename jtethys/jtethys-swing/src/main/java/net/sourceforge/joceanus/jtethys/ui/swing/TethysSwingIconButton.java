/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class TethysSwingIconButton
        implements TethysIconButton<JComponent, Icon> {
    /**
     * Button.
     */
    private final JButton theButton;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected TethysSwingIconButton(final TethysIconButtonManager<?, JComponent, Icon> pManager) {
        /* Create the button */
        theButton = new JButton();

        /* Create style of button */
        theButton.setHorizontalAlignment(SwingConstants.CENTER);

        /* Handle action */
        theButton.addActionListener(e -> pManager.progressToNextState());
    }

    @Override
    public JButton getButton() {
        return theButton;
    }

    @Override
    public void setButtonState(final Icon pIcon,
                               final String pToolTip) {
        /* Set the icon */
        theButton.setIcon(pIcon);

        /* Set the ToolTip */
        theButton.setToolTipText(pToolTip);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setNullMargins() {
        theButton.setMargin(new Insets(0, 0, 0, 0));
    }

    /**
     * Simple Swing IconButton Manager.
     * @param <T> the object type
     */
    public static class TethysSwingSimpleIconButtonManager<T>
            extends TethysSimpleIconButtonManager<T, JComponent, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingSimpleIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysSwingIconButton(this));
        }

        @Override
        public TethysSwingIconButton getButton() {
            return (TethysSwingIconButton) super.getButton();
        }

        @Override
        public JButton getNode() {
            return (JButton) super.getNode();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            getNode().setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, TethysSwingGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
        }
    }

    /**
     * State-based Swing IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class TethysSwingStateIconButtonManager<T, S>
            extends TethysStateIconButtonManager<T, S, JComponent, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingStateIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysSwingIconButton(this));
        }

        @Override
        public TethysSwingIconButton getButton() {
            return (TethysSwingIconButton) super.getButton();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            getNode().setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, TethysSwingGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
        }
    }
}
