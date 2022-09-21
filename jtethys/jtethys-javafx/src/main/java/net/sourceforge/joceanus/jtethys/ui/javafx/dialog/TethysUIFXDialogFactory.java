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
package net.sourceforge.joceanus.jtethys.ui.javafx.dialog;

import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIAlert;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIChildDialog;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIPasswordDialog;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * javaFX Dialog factory.
 */
public class TethysUIFXDialogFactory
        implements TethysUIDialogFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUIFXDialogFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    /**
     * Set the stage.
     * @param pStage the stage
     */
    public void setStage(final Stage pStage) {
        theStage = pStage;
    }

    @Override
    public TethysUIFXFileSelector newFileSelector() {
        return new TethysUIFXFileSelector(theStage);
    }

    @Override
    public TethysUIFXDirectorySelector newDirectorySelector() {
        return new TethysUIFXDirectorySelector(theStage);
    }

    @Override
    public TethysUIFXAboutBox newAboutBox() {
        return new TethysUIFXAboutBox(theFactory, theStage);
    }

    @Override
    public TethysUIPasswordDialog newPasswordDialog(final String pTitle,
                                                    final boolean pNeedConfirm) {
        return TethysUIFXPasswordDialog.createTheDialog(theFactory, theStage, pTitle, pNeedConfirm);
    }

    @Override
    public TethysUIAlert newAlert() {
        return new TethysUIFXAlert(theStage);
    }

    @Override
    public TethysUIChildDialog newChildDialog() {
        return new TethysUIFXChildDialog(theFactory, theStage);
    }
}
