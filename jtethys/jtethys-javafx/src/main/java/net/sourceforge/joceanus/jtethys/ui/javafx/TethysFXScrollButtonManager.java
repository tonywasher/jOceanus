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
import net.sourceforge.joceanus.jtethys.javafx.TethysFXArrowIcon;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * JavaFX Button which provides a PopUpMenu selection.
 * @param <T> the object type
 */
public final class TethysFXScrollButtonManager<T>
        extends TethysScrollButtonManager<T, Node, Node> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysFXScrollButtonManager(final TethysFXGuiFactory pFactory) {
        /* Initialise the underlying class */
        super(pFactory);

        /* Set down Arrow as the graphic */
        getButton().setIcon(TethysFXArrowIcon.DOWN.getArrow());
    }

    @Override
    protected void registerListeners() {
        /* Set context menu listener */
        TethysFXScrollContextMenu<T> myMenu = getMenu();
        myMenu.addEventHandler(TethysFXContextEvent.MENU_SELECT, e -> handleMenuClosed());
        myMenu.addEventHandler(TethysFXContextEvent.MENU_CANCEL, e -> notifyCancelled());
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
    public <K extends Enum<K> & TethysIconId> void setIcon(final K pId,
                                                           final int pWidth) {
        getButton().setIcon(TethysFXGuiUtils.getIconAtSize(pId, pWidth));
    }
}
