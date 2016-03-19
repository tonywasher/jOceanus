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

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager.TethysListButton;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * PopUp menu that displays a list of checkMenu items.
 */
public final class TethysSwingListButton
        implements TethysListButton<JComponent, Icon> {
    /**
     * Button.
     */
    private final JButton theButton;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysSwingListButton(final TethysListButtonManager<?, JComponent, Icon> pManager) {
        /* Create the button */
        theButton = new JButton();

        /* Set standard setup */
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

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    /**
     * SwingButtonManager.
     * @param <T> the object type
     */
    public static final class TethysSwingListButtonManager<T>
            extends TethysListButtonManager<T, JComponent, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingListButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysSwingListButton(this));
            declareMenu(new TethysSwingScrollContextMenu<T>());

            /* Set context menu listener */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = getMenu().getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleToggleItem());
            myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleMenuClosed());
        }

        @Override
        public TethysSwingListButton getButton() {
            return (TethysSwingListButton) super.getButton();
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
