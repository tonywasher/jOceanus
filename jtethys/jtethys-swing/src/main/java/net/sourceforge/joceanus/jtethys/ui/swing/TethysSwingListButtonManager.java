/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * PopUp menu that displays a list of checkMenu items.
 * @param <T> the item type
 */
public final class TethysSwingListButtonManager<T>
        extends TethysListButtonManager<T, JComponent, Icon> {
    /**
     * The node.
     */
    private JComponent theNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysSwingListButtonManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise the underlying class */
        super(pFactory);
        theNode = super.getNode();
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = getMenu().getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleToggleItem());
        myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleMenuClosed());
    }

    @Override
    public TethysSwingScrollContextMenu<T> getMenu() {
        return (TethysSwingScrollContextMenu<T>) super.getMenu();
    }

    @Override
    protected void showMenu() {
        getMenu().showMenuAtPosition(getNode(), SwingConstants.BOTTOM);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theNode.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theNode.setPreferredSize(myDim);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), super.getNode());
    }
}
