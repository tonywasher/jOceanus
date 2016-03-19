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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXArrowIcon;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager.TethysScrollButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 */
public final class TethysFXScrollButton
        implements TethysScrollButton<Node, Node> {
    /**
     * Button.
     */
    private final Button theButton;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    private TethysFXScrollButton(final TethysScrollButtonManager<?, Node, Node> pManager) {
        /* Create the button */
        theButton = new Button();

        /* Create style of button */
        theButton.setGraphic(TethysFXArrowIcon.DOWN.getArrow());
        theButton.setAlignment(Pos.CENTER);
        theButton.setContentDisplay(ContentDisplay.RIGHT);
        theButton.setMaxWidth(Double.MAX_VALUE);

        /* Set action handler */
        theButton.setOnAction(e -> pManager.handleMenuRequest());
    }

    @Override
    public void setButtonText(final String pText) {
        theButton.setText(pText);
    }

    @Override
    public void setButtonIcon(final Node pIcon) {
        theButton.setGraphic(pIcon);
    }

    @Override
    public void setButtonToolTip(final String pToolTip) {
        /* Set the ToolTip */
        Tooltip myToolTip = pToolTip == null
                                             ? null
                                             : new Tooltip(pToolTip);
        theButton.setTooltip(myToolTip);
    }

    @Override
    public Button getButton() {
        return theButton;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setDisable(!pEnabled);
    }

    @Override
    public void setNullMargins() {
        theButton.setPadding(Insets.EMPTY);
    }

    /**
     * FX ScrollButton Manager.
     * @param <T> the object type
     */
    public static final class TethysFXScrollButtonManager<T>
            extends TethysScrollButtonManager<T, Node, Node> {
        /**
         * Constructor.
         */
        public TethysFXScrollButtonManager() {
            /* Create and declare the button and menu */
            declareButton(new TethysFXScrollButton(this));
            declareMenu(new TethysFXScrollContextMenu<T>());

            /* Set context menu listener */
            TethysFXScrollContextMenu<T> myMenu = getMenu();
            myMenu.addEventHandler(TethysFXContextEvent.MENU_SELECT, e -> handleMenuClosed());
            myMenu.addEventHandler(TethysFXContextEvent.MENU_CANCEL, e -> handleMenuClosed());
        }

        @Override
        public TethysFXScrollButton getButton() {
            return (TethysFXScrollButton) super.getButton();
        }

        @Override
        public Button getNode() {
            return (Button) super.getNode();
        }

        @Override
        public TethysFXScrollContextMenu<T> getMenu() {
            return (TethysFXScrollContextMenu<T>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(getNode(), Side.BOTTOM);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
            getButton().setButtonIcon(TethysFXGuiUtils.getIconAtSize(pId, TethysIconButtonManager.DEFAULT_ICONWIDTH));
        }
    }
}
