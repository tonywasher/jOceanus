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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager.TethysScrollButton;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 */
public final class TethysSwingScrollButton
        extends JButton
        implements TethysScrollButton<Icon> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -483012677041020491L;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysSwingScrollButton(final TethysScrollButtonManager<?, Icon> pManager) {
        /* Create style of button */
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
     * Swing ScrollButton Manager.
     * @param <T> the object type
     */
    public static final class TethysSwingScrollButtonManager<T>
            extends TethysScrollButtonManager<T, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingScrollButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysSwingScrollButton(this));
            declareMenu(new TethysSwingScrollContextMenu<T>());

            /* Set context menu listener */
            getMenu().getEventRegistrar().addFilteredActionListener(TethysSwingScrollContextMenu.ACTION_SELECTED, new TethysActionEventListener() {
                @Override
                public void processAction(final TethysActionEvent e) {
                    /* Handle the close of the menu */
                    handleMenuClosed();
                }
            });
        }

        @Override
        public TethysSwingScrollButton getButton() {
            return (TethysSwingScrollButton) super.getButton();
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
