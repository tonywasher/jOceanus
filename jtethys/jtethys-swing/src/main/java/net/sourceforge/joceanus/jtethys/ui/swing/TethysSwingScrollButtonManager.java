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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Swing Button which provides a PopUpMenu selection.
 * @param <T> the object type
 */
public final class TethysSwingScrollButtonManager<T>
        extends TethysScrollButtonManager<T, JComponent, Icon> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysSwingScrollButtonManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise the underlying class */
        super(pFactory);

        /* Set down Arrow as the graphic */
        getButton().setIcon(TethysSwingArrowIcon.DOWN);
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = getMenu().getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleMenuClosed());
        myRegistrar.addEventListener(TethysUIEvent.WINDOWCLOSED, e -> notifyCancelled());
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
    public <K extends Enum<K> & TethysIconId> void setIcon(final K pId,
                                                           final int pWidth) {
        getButton().setIcon(TethysSwingGuiUtils.getIconAtSize(pId, pWidth));
    }
}
