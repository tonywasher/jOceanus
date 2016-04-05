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
import javafx.scene.control.CheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;

/**
 * JavaFX CheckBox.
 */
public class TethysFXCheckBox
        extends TethysCheckBox<Node, Node> {
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
        theCheckBox.selectedProperty().addListener((v, o, n) -> handleSelected(n));
    }

    @Override
    public Node getNode() {
        return theCheckBox;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theCheckBox.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theCheckBox.setVisible(pVisible);
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
}
