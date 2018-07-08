/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JSlider;

import net.sourceforge.joceanus.jtethys.ui.TethysSlider;

/**
 * Swing Slider.
 */
public class TethysSwingSlider
        extends TethysSlider {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * Slider.
     */
    private final JSlider theSlider;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysSwingSlider(final TethysSwingGuiFactory pFactory) {
        /* Create resources */
        super(pFactory);
        theSlider = new JSlider();
        theSlider.setMinimum(0);
        theSlider.setPaintTicks(true);

        /* Create the node */
        theNode = new TethysSwingNode(theSlider);

        /* Handle events */
        theSlider.addChangeListener(e -> {
            if (!theSlider.getValueIsAdjusting()) {
                handleNewValue();
            }
        });
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSlider.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theSlider.setVisible(pVisible);
    }

    @Override
    public void setMaximum(final Integer pMax) {
        theSlider.setMaximum(pMax);
        determineTickIntervals(pMax);
    }

    @Override
    public void setTickIntervals(final int pMajor,
                                 final int pMinor) {
        theSlider.setMajorTickSpacing(pMajor);
        theSlider.setMinorTickSpacing(pMinor);
    }

    @Override
    public void setValue(final Integer pValue) {
        theSlider.setValue(pValue);
    }

    @Override
    public Integer getValue() {
        return theSlider.getValue();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
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
