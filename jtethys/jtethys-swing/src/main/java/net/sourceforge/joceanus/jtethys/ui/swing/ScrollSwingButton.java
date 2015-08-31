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

import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.ui.ScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.ScrollButtonManager.ScrollButton;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 */
public class ScrollSwingButton
        extends JButton
        implements ScrollButton<Icon> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -483012677041020491L;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private ScrollSwingButton(final ScrollButtonManager<?, Icon> pManager) {
        /* Create style of button */
        setIcon(ArrowIcon.DOWN);
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
    public static final class ScrollSwingButtonManager<T>
            extends ScrollButtonManager<T, Icon> {
        /**
         * Constructor.
         */
        public ScrollSwingButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new ScrollSwingButton(this));
            declareMenu(new ScrollSwingContextMenu<T>());

            /* Set context menu listener */
            getMenu().getEventRegistrar().addFilteredActionListener(ScrollSwingContextMenu.ACTION_SELECTED, new JOceanusActionEventListener() {
                @Override
                public void processActionEvent(final JOceanusActionEvent e) {
                    /* Handle the close of the menu */
                    handleMenuClosed();
                }
            });
        }

        @Override
        public ScrollSwingButton getButton() {
            return (ScrollSwingButton) super.getButton();
        }

        @Override
        public ScrollSwingContextMenu<T> getMenu() {
            return (ScrollSwingContextMenu<T>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(getButton(), SwingConstants.BOTTOM);
        }
    }
}
