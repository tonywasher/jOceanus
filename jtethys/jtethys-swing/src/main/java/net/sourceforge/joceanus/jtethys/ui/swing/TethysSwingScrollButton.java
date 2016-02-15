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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/swing/JScrollButton.java $
 * $Revision: 585 $
 * $Author: Tony $
 * $Date: 2015-03-30 06:24:29 +0100 (Mon, 30 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager.TethysScrollButton;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 */
public final class TethysSwingScrollButton
        implements TethysScrollButton<JComponent, Icon> {
    /**
     * Button.
     */
    private final JButton theButton;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysSwingScrollButton(final TethysScrollButtonManager<?, JComponent, Icon> pManager) {
        /* Create the button */
        theButton = new JButton();

        /* Create style of button */
        theButton.setIcon(TethysSwingArrowIcon.DOWN);
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
        theButton.setVerticalAlignment(SwingConstants.CENTER);
        theButton.setHorizontalTextPosition(SwingConstants.LEFT);

        /* Set action handler */
        theButton.addActionListener(e -> pManager.handleMenuRequest());
    }

    @Override
    public void setButtonText(final String pText) {
        theButton.setText(pText);
    }

    @Override
    public void setButtonIcon(final Icon pIcon) {
        theButton.setIcon(pIcon);
    }

    @Override
    public void setButtonToolTip(final String pToolTip) {
        theButton.setToolTipText(pToolTip);
    }

    @Override
    public JButton getButton() {
        return theButton;
    }

    /**
     * Swing ScrollButton Manager.
     * @param <T> the object type
     */
    public static final class TethysSwingScrollButtonManager<T>
            extends TethysScrollButtonManager<T, JComponent, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingScrollButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysSwingScrollButton(this));
            declareMenu(new TethysSwingScrollContextMenu<T>());

            /* Set context menu listener */
            getMenu().getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleMenuClosed());
            getMenu().getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleMenuClosed());
        }

        @Override
        public TethysSwingScrollButton getButton() {
            return (TethysSwingScrollButton) super.getButton();
        }

        @Override
        public JButton getNode() {
            return (JButton) super.getNode();
        }

        @Override
        public TethysSwingScrollContextMenu<T> getMenu() {
            return (TethysSwingScrollContextMenu<T>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(getNode(), SwingConstants.BOTTOM);
        }
    }
}
