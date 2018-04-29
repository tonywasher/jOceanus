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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;

/**
 * JavaFX CheckBox.
 */
public class TethysFXCheckBox
        extends TethysCheckBox<Node, Node> {
    /**
     * The dataField style.
     */
    private static final String STYLE_CHECKBOX = TethysFXGuiFactory.CSS_STYLE_BASE + "-checkbox";

    /**
     * The changed style class.
     */
    private static final String STYLE_CHANGED = STYLE_CHECKBOX + "-changed";

    /**
     * The node.
     */
    private Region theNode;

    /**
     * CheckBox.
     */
    private final CheckBox theCheckBox;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXCheckBox(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theCheckBox = new CheckBox();
        theNode = theCheckBox;
        theCheckBox.selectedProperty().addListener((v, o, n) -> handleSelected(n));

        /* Declare the CheckBox style */
        theCheckBox.getStyleClass().add(STYLE_CHECKBOX);
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theCheckBox.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
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
        final List<String> myStyles = theCheckBox.getStyleClass();
        myStyles.remove(STYLE_CHANGED);
        if (pChanged) {
            myStyles.add(STYLE_CHANGED);
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theCheckBox);
    }
}
