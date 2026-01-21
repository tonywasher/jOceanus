/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.swing.button;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.core.button.TethysUICoreListButtonManager;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingNode;
import io.github.tonywasher.joceanus.tethys.swing.menu.TethysUISwingScrollMenu;

import javax.swing.SwingConstants;

/**
 * PopUp menu that displays a list of checkMenu items.
 *
 * @param <T> the item type
 */
public final class TethysUISwingListButtonManager<T extends Comparable<? super T>>
        extends TethysUICoreListButtonManager<T> {
    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUISwingListButtonManager(final TethysUICoreFactory<?> pFactory) {
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
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = getMenu().getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleToggleItem());
        myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> handleMenuClosed());
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
