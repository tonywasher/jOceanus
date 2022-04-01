/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Side;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * PopUp menu that displays a list of checkMenu items.
 *
 * @param <T> the item type
 */
public final class TethysFXListButtonManager<T extends Comparable<T>>
        extends TethysListButtonManager<T> {
    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysFXListButtonManager(final TethysFXGuiFactory pFactory) {
        /* Initialise the underlying class */
        super(pFactory);
    }

    @Override
    public TethysFXNode getNode() {
        return (TethysFXNode) super.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        getNode().setManaged(pVisible);
        getNode().setVisible(pVisible);
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = getMenu().getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleToggleItem());
        myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleMenuClosed());
    }

    @Override
    public TethysFXScrollContextMenu<T> getMenu() {
        return (TethysFXScrollContextMenu<T>) super.getMenu();
    }

    @Override
    protected void showMenu() {
        getMenu().showMenuAtPosition(getNode().getNode(), Side.BOTTOM);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
