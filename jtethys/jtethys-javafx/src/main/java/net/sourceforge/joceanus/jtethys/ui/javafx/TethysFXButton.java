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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Tethys FX Button.
 */
public class TethysFXButton
        extends TethysButton<Node, Node> {
    /**
     * The node.
     */
    private Node theNode;

    /**
     * The button.
     */
    private final Button theButton;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXButton(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theButton = new Button();
        theNode = theButton;
        theButton.setOnAction(e -> handlePressed());
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setText(final String pText) {
        theButton.setText(pText);
    }

    @Override
    public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
        setIcon(TethysFXGuiUtils.getIconAtSize(pId, getIconWidth()));
    }

    @Override
    public void setIcon(final Node pIcon) {
        theButton.setGraphic(pIcon);
    }

    @Override
    public void setToolTip(final String pTip) {
        Tooltip myToolTip = pTip == null
                                         ? null
                                         : new Tooltip(pTip);
        theButton.setTooltip(myToolTip);
    }

    @Override
    public void setNullMargins() {
        theButton.setPadding(Insets.EMPTY);
    }

    @Override
    public void setIconOnly() {
        theButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        theButton.setAlignment(Pos.CENTER);
        theButton.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public void setTextAndIcon() {
        theButton.setContentDisplay(ContentDisplay.RIGHT);
        theButton.setAlignment(Pos.CENTER);
        theButton.setMaxWidth(Double.MAX_VALUE);
        theButton.setMaxHeight(Double.MAX_VALUE);
    }

    @Override
    public void setTextOnly() {
        theButton.setContentDisplay(ContentDisplay.TEXT_ONLY);
        theButton.setAlignment(Pos.CENTER);
        theButton.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        theNode = TethysFXGuiUtils.getTitledPane(pTitle, theButton);
    }
}
