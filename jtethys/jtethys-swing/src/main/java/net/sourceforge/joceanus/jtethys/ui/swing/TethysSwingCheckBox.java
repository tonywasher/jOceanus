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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;

/**
 * Swing checkBox.
 */
public class TethysSwingCheckBox
        extends TethysCheckBox<JComponent, Icon> {
    /**
     * The node.
     */
    private JComponent theNode;

    /**
     * CheckBox.
     */
    private final JCheckBox theCheckBox;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingCheckBox(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theCheckBox = new JCheckBox();
        theNode = theCheckBox;
        theCheckBox.addActionListener(e -> handleSelected(theCheckBox.isSelected()));
    }

    @Override
    public JComponent getNode() {
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
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theNode.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theNode.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theNode.setPreferredSize(myDim);
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
        theNode = TethysSwingGuiUtils.addPanelBorder(getBorderTitle(), getBorderPadding(), theCheckBox);
    }
}
