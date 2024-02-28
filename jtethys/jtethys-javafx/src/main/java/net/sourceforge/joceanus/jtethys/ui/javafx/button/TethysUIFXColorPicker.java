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
package net.sourceforge.joceanus.jtethys.ui.javafx.button;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import net.sourceforge.joceanus.jtethys.ui.core.dialog.TethysUICoreColorPicker;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;

/**
 * JavaFX Colour Picker.
 */
public class TethysUIFXColorPicker
        extends TethysUICoreColorPicker<Color> {
    /**
     * The node.
     */
    private final TethysUIFXNode theNode;

    /**
     * ColorPicker.
     */
    private final ColorPicker thePicker;

    /**
     * Selected Colour.
     */
    private Color theColour;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysUIFXColorPicker(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);

        /* Initialise class */
        thePicker = new ColorPicker();
        theNode = new TethysUIFXNode(thePicker);

        /* Listen to colour selection */
        thePicker.setOnAction(e -> handleSelection());

        /* Set as button */
        thePicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePicker.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setValue(final String pValue) {
        super.setValue(pValue);
        theColour = Color.web(pValue);
        thePicker.setValue(theColour);
    }
    @Override
    public void setPreferredWidth(final Integer pWidth) {
        thePicker.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        thePicker.setPrefHeight(pHeight);
    }

    @Override
    public Color getColour() {
        return theColour;
    }

    /**
     * Obtain a swatch of the selected colour.
     *
     * @return the swatch
     */
    public Rectangle getSwatch() {
        return new TethysUISwatch(theColour);
    }

    /**
     * handle Selection.
     */
    private void handleSelection() {
        /* Obtain colour selection */
        theColour = thePicker.getValue();
        handleNewValue(TethysUIFXUtils.colorToHexString(theColour));
    }

    /**
     * A Swatch graphic.
     */
    private static final class TethysUISwatch
            extends Rectangle {
        /**
         * The Swatch size.
         */
        private static final int SWATCH_SIZE = 14;

        /**
         * Constructor.
         *
         * @param pColor the colour.
         */
        TethysUISwatch(final Color pColor) {
            /* Specify colours */
            setStroke(Color.BLACK);
            setFill(pColor);
            setWidth(SWATCH_SIZE);
            setHeight(SWATCH_SIZE);
        }
    }
}
