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
package io.github.tonywasher.joceanus.tethys.javafx.control;

import javafx.scene.control.Slider;

import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.core.control.TethysUICoreSlider;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXNode;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXUtils;

/**
 * JavaFX Slider.
 */
public class TethysUIFXSlider
        extends TethysUICoreSlider {
    /**
     * The slider style.
     */
    private static final String STYLE_SLIDER = TethysUIFXUtils.CSS_STYLE_BASE + "-slider";

    /**
     * The node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The Slider.
     */
    private final Slider theSlider;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysUIFXSlider(final TethysUICoreFactory<?> pFactory) {
        /* Create resources */
        super(pFactory);
        theSlider = new Slider();
        theNode = new TethysUIFXNode(theSlider);
        theSlider.setMin(0);
        theSlider.setShowTickMarks(true);
        theSlider.getStyleClass().add(STYLE_SLIDER);

        /* Handle events */
        theSlider.valueProperty().addListener((v, o, n) -> handleNewValue());
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSlider.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
