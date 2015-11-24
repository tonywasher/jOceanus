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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/swing/JScrollListButton.java $
 * $Revision: 585 $
 * $Author: Tony $
 * $Date: 2015-03-30 06:24:29 +0100 (Mon, 30 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager.TethysListButton;

/**
 * PopUp menu that displays a list of checkMenu items.
 */
public final class TethysSwingListButton
        extends JButton
        implements TethysListButton<Icon> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3039142712320561262L;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysSwingListButton(final TethysListButtonManager<?, Icon> pManager) {
        /* Set standard setup */
        setIcon(TethysSwingArrowIcon.DOWN);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.LEFT);

        /* Set action handler */
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                pManager.handleMenuRequest();
            }
        });
    }

    @Override
    public void setButtonText(final String pText) {
        /* Set the text */
        setText(pText);
    }

    @Override
    public void setButtonIcon(final Icon pIcon) {
        /* Set the icon */
        setIcon(pIcon);
    }

    @Override
    public void setButtonToolTip(final String pToolTip) {
        /* Set the ToolTip */
        setToolTipText(pToolTip);
    }

    /**
     * FXButtonManager.
     * @param <T> the object type
     */
    public static final class TethysSwingListButtonManager<T>
            extends TethysListButtonManager<T, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingListButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysSwingListButton(this));
            declareMenu(new TethysSwingScrollContextMenu<T>());

            /* Set context menu listener */
            getMenu().getEventRegistrar().addFilteredActionListener(TethysSwingScrollContextMenu.ACTION_TOGGLED, new TethysActionEventListener() {
                @Override
                public void processActionEvent(final TethysActionEvent e) {
                    /* Handle the toggle of the item */
                    handleToggleItem();
                }
            });
        }

        @Override
        public TethysSwingListButton getButton() {
            return (TethysSwingListButton) super.getButton();
        }

        @Override
        public TethysSwingScrollContextMenu<T> getMenu() {
            return (TethysSwingScrollContextMenu<T>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(getButton(), SwingConstants.BOTTOM);
        }
    }
}
