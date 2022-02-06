/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.button;

import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.core.button.TethysUICoreScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingUtils;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingScrollMenu;

/**
 * Swing Button which provides a PopUpMenu selection.
 * @param <T> the object type
 */
public final class TethysUISwingScrollButtonManager<T>
        extends TethysUICoreScrollButtonManager<T> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysUISwingScrollButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise the underlying class */
        super(pFactory);
    }

    @Override
    public TethysUISwingNode getNode() {
        return (TethysUISwingNode) super.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        getNode().setVisible(pVisible);
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        final TethysEventRegistrar<TethysUIXEvent> myRegistrar = getMenu().getEventRegistrar();
        myRegistrar.addEventListener(TethysUIXEvent.NEWVALUE, e -> handleMenuClosed());
        myRegistrar.addEventListener(TethysUIXEvent.WINDOWCLOSED, e -> handleMenuClosed());
    }

    @Override
    public TethysUISwingScrollMenu<T> getMenu() {
        return (TethysUISwingScrollMenu<T>) super.getMenu();
    }

    @Override
    protected void showMenu() {
        getMenu().showMenuAtPosition(getNode().getNode(), SwingConstants.BOTTOM);
    }

    @Override
    public void setIcon(final TethysUIIconId pId,
                        final int pWidth) {
        getButton().setIcon(TethysUISwingUtils.getIconAtSize(pId, pWidth));
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