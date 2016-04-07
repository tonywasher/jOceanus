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

import javafx.geometry.Side;
import javafx.scene.Node;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * PopUp menu that displays a list of checkMenu items.
 * @param <T> the item type
 */
public final class TethysFXListButtonManager<T>
        extends TethysListButtonManager<T, Node, Node> {
    /**
     * The node.
     */
    private Node theNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXListButtonManager(final TethysFXGuiFactory pFactory) {
        /* Initialise the underlying class */
        super(pFactory);
        theNode = super.getNode();

        /* Set down Arrow as the graphic */
        getButton().setIcon(TethysFXArrowIcon.DOWN.getArrow());
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        TethysFXScrollContextMenu<T> myMenu = getMenu();
        myMenu.addEventHandler(TethysFXContextEvent.MENU_TOGGLE, e -> handleToggleItem());
        myMenu.addEventHandler(TethysFXContextEvent.MENU_CANCEL, e -> handleMenuClosed());
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
    public void setBorderTitle(final String pTitle) {
        theNode = TethysFXGuiUtils.getTitledPane(pTitle, super.getNode());
    }
}
