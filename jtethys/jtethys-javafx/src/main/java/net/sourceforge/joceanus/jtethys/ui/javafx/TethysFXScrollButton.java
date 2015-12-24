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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager.TethysScrollButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 */
public final class TethysFXScrollButton
        extends Button
        implements TethysScrollButton<Node> {
    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysFXScrollButton(final TethysScrollButtonManager<?, Node> pManager) {
        /* Create style of button */
        setGraphic(TethysFXArrowIcon.DOWN.getArrow());
        setAlignment(Pos.CENTER);
        setContentDisplay(ContentDisplay.RIGHT);
        setMaxWidth(Double.MAX_VALUE);

        /* Set action handler */
        setOnAction(e -> pManager.handleMenuRequest());
    }

    @Override
    public void setButtonText(final String pText) {
        /* Set the text */
        setText(pText);
    }

    @Override
    public void setButtonIcon(final Node pIcon) {
        /* Set the icon */
        setGraphic(pIcon);
    }

    @Override
    public void setButtonToolTip(final String pToolTip) {
        /* Set the ToolTip */
        Tooltip myToolTip = pToolTip == null
                                             ? null
                                             : new Tooltip(pToolTip);
        setTooltip(myToolTip);
    }

    /**
     * FX ScrollButton Manager.
     * @param <T> the object type
     */
    public static final class TethysFXScrollButtonManager<T>
            extends TethysScrollButtonManager<T, Node> {
        /**
         * Constructor.
         */
        public TethysFXScrollButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysFXScrollButton(this));
            declareMenu(new TethysFXScrollContextMenu<T>());

            /* Set context menu listener */
            getMenu().addEventHandler(TethysFXContextEvent.MENU_SELECT, e -> handleMenuClosed());
        }

        @Override
        public TethysFXScrollButton getButton() {
            return (TethysFXScrollButton) super.getButton();
        }

        @Override
        public TethysFXScrollContextMenu<T> getMenu() {
            return (TethysFXScrollContextMenu<T>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(getButton(), Side.BOTTOM);
        }
    }
}
