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

import javafx.scene.Node;
import javafx.scene.control.Slider;
import net.sourceforge.joceanus.jtethys.ui.TethysSlider;

/**
 * JavaFX Slider.
 */
public class TethysFXSlider
        extends TethysSlider<Node, Node> {
    /**
     * The node.
     */
    private Node theNode;

    /**
     * The Slider.
     */
    private final Slider theSlider;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXSlider(final TethysFXGuiFactory pFactory) {
        /* Create resources */
        super(pFactory);
        theSlider = new Slider();
        theNode = theSlider;
        theSlider.setMin(0);
        theSlider.setShowTickMarks(true);

        /* Handle events */
        theSlider.valueProperty().addListener(e -> {
            if (!theSlider.isValueChanging()) {
                handleNewValue();
            }
        });
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSlider.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setMaximum(final Integer pMax) {
        theSlider.setMax(pMax);
        determineTickIntervals(pMax);
    }

    @Override
    public void setTickIntervals(final int pMajor,
                                 final int pMinor) {
        theSlider.setMajorTickUnit(pMajor);
        theSlider.setMinorTickCount(pMinor);
    }

    @Override
    public void setValue(final Integer pValue) {
        theSlider.setValue(pValue);
    }

    @Override
    public Integer getValue() {
        return Integer.valueOf((int) theSlider.getValue());
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theSlider.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theSlider.setPrefHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theSlider);
    }
}
