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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;

/**
 * Tethys FX Label.
 */
public class TethysFXLabel
        extends TethysLabel<Node, Node> {
    /**
     * The Node.
     */
    private final Label theLabel;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXLabel(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theLabel = new Label();
        setAlignment(TethysAlignment.LEADING);
    }

    @Override
    public void setText(final String pText) {
        theLabel.setText(pText);
    }

    @Override
    public void setAlignment(final TethysAlignment pAlign) {
        theLabel.setAlignment(determineAlignment(pAlign));
    }

    @Override
    public Node getNode() {
        return theLabel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theLabel.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theLabel.setVisible(pVisible);
    }

    /**
     * Translate alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private Pos determineAlignment(final TethysAlignment pAlign) {
        switch (pAlign) {
            case TRAILING:
                return Pos.CENTER_RIGHT;
            case CENTRE:
                return Pos.CENTER;
            case LEADING:
            default:
                return Pos.CENTER_LEFT;
        }
    }
}
