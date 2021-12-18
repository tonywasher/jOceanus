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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.control.TethysUIFXControlFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.dialog.TethysUIFXDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.pane.TethysUIFXPaneFactory;

/**
 * javafx Factory.
 */
public class TethysUIFXFactory
        extends TethysUICoreFactory<Color> {
    /**
     * The pane factory.
     */
    private final TethysUIFXPaneFactory thePaneFactory;

    /**
     * The control factory.
     */
    private final TethysUIFXControlFactory theControlFactory;

    /**
     * The dialog factory.
     */
    private final TethysUIFXDialogFactory theDialogFactory;

    /**
     * Constructor.
     */
    TethysUIFXFactory() {
        thePaneFactory = new TethysUIFXPaneFactory(this);
        theControlFactory = new TethysUIFXControlFactory(this);
        theDialogFactory = new TethysUIFXDialogFactory();
    }

    @Override
    public TethysUIPaneFactory paneFactory() {
        return thePaneFactory;
    }

    @Override
    public TethysUIControlFactory controlFactory() {
        return theControlFactory;
    }

    @Override
    public TethysUIDialogFactory<Color> dialogFactory() {
        return theDialogFactory;
    }

    @Override
    public TethysUIIcon resolveIcon(final TethysUIIconId pIconId,
                                    final int pWidth) {
        return pIconId == null
                ? null
                : TethysUIFXUtils.getIconAtSize(pIconId, pWidth);
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theControlFactory.setStage(pStage);
        theDialogFactory.setStage(pStage);
        fireEvent(TethysUIXEvent.NEWSTAGE);
    }
}
