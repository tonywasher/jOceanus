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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JCheckBox;

import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;

/**
 * Swing checkBox.
 */
public class TethysSwingCheckBox
        extends TethysCheckBox {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * CheckBox.
     */
    private final JCheckBox theCheckBox;

    /**
     * The adjuster.
     */
    private final TethysSwingDataFieldAdjust theAdjuster;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingCheckBox(final TethysSwingGuiFactory pFactory) {
        super(pFactory);

        /* Create the checkBox */
        theAdjuster = pFactory.getFieldAdjuster();
        theCheckBox = new JCheckBox();
        theCheckBox.addActionListener(e -> handleSelected(theCheckBox.isSelected()));

        /* Create the node */
        theNode = new TethysSwingNode(theCheckBox);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theCheckBox.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setText(final String pText) {
        theCheckBox.setText(pText);
    }

    @Override
    public void setSelected(final boolean pSelected) {
        super.setSelected(pSelected);
        theCheckBox.setSelected(pSelected);
    }

    @Override
    public void setChanged(final boolean pChanged) {
        theAdjuster.adjustCheckBox(this, pChanged);
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
