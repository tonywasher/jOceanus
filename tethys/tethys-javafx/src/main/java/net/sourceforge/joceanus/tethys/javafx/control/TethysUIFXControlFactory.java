/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.control;

import javafx.stage.Stage;

import net.sourceforge.joceanus.tethys.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.control.TethysUIPasswordField;
import net.sourceforge.joceanus.tethys.api.control.TethysUIProgressBar;
import net.sourceforge.joceanus.tethys.api.control.TethysUISlider;
import net.sourceforge.joceanus.tethys.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITextArea;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * javaFX Control factory.
 */
public class TethysUIFXControlFactory
    implements TethysUIControlFactory {
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
    public TethysUIFXControlFactory(final TethysUICoreFactory<?> pFactory) {
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

    /**
     * Get the stage.
     * @return the stage
     */
    Stage getStage() {
        return theStage;
    }

    @Override
    public TethysUILabel newLabel() {
        return new TethysUIFXLabel(theFactory);
    }

    @Override
    public TethysUICheckBox newCheckBox() {
        return new TethysUIFXCheckBox(theFactory);
    }

    @Override
    public TethysUITextArea newTextArea() {
        return new TethysUIFXTextArea(theFactory);
    }

    @Override
    public TethysUIPasswordField newPasswordField() {
        return new TethysUIFXPasswordField(theFactory);
    }

    @Override
    public TethysUIProgressBar newProgressBar() {
        return new TethysUIFXProgressBar(theFactory);
    }

    @Override
    public TethysUISlider newSlider() {
        return new TethysUIFXSlider(theFactory);
    }

    @Override
    public TethysUIHTMLManager newHTMLManager() {
        return new TethysUIFXHTMLManager(theFactory, this);
    }

    @Override
    public <T> TethysUITreeManager<T> newTreeManager() {
        return new TethysUIFXTreeManager<>(theFactory);
    }

    @Override
    public <T> TethysUISplitTreeManager<T> newSplitTreeManager() {
        return new TethysUIFXSplitTreeManager<>(theFactory);
    }
}
