/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import net.sourceforge.joceanus.jtethys.ui.TethysColorPicker;

/**
 * JavaFX Colour Picker.
 */
public class TethysFXColorPicker
        extends TethysColorPicker {
    /**
     * The node.
     */
    private final TethysFXNode theNode;

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
     *
     * @param pFactory the GUI factory
     */
    TethysFXColorPicker(final TethysFXGuiFactory pFactory) {
        /* Initialise class */
        super(pFactory);
        thePicker = new ColorPicker();
        theNode = new TethysFXNode(thePicker);

        /* Listen to colour selection */
        thePicker.setOnAction(e -> handleSelection());

        /* Set as button */
        thePicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);
    }

    @Override
    public TethysFXNode getNode() {
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

    /**
     * Obtain the colour.
     *
     * @return the colour
     */
    public Color getColour() {
        return theColour;
    }

    /**
     * Obtain a swatch of the selected colour.
     *
     * @return the swatch
     */
    public Rectangle getSwatch() {
        return new TethysSwatch(theColour);
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
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    /**
     * handle Selection.
     */
    private void handleSelection() {
        /* Obtain colour selection */
        theColour = thePicker.getValue();
        handleNewValue(TethysFXGuiUtils.colorToHexString(theColour));
    }

    /**
     * A Swatch graphic.
     */
    private static final class TethysSwatch
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
        TethysSwatch(final Color pColor) {
            /* Specify colours */
            setStroke(Color.BLACK);
            setFill(pColor);
            setWidth(SWATCH_SIZE);
            setHeight(SWATCH_SIZE);
        }
    }
}
