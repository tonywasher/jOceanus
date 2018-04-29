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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import net.sourceforge.joceanus.jtethys.ui.TethysTextArea;

/**
 * Swing TextArea.
 */
public class TethysSwingTextArea
        extends TethysTextArea<JComponent, Icon> {
    /**
     * TextArea.
     */
    private final JTextArea theArea;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingTextArea(final TethysSwingGuiFactory pFactory) {
        /* Create resources */
        super(pFactory);
        theArea = new JTextArea();
        theArea.setEditable(false);
    }

    @Override
    public JComponent getNode() {
        return theArea;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theArea.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theArea.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theArea.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theArea.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theArea.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theArea.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), theArea);
    }

    @Override
    public void setText(final String pText) {
        theArea.setText(pText);
    }

    @Override
    public void appendText(final String pText) {
        theArea.append(pText);
    }

    @Override
    public void insertText(final String pText,
                           final int pPos) {
        theArea.insert(pText, pPos);
    }

    @Override
    public void replaceText(final String pText,
                            final int pStart,
                            final int pEnd) {
        theArea.replaceRange(pText, pStart, pEnd);
    }

    @Override
    public void setCaretPosition(final int pPos) {
        theArea.setCaretPosition(pPos);
    }
}
