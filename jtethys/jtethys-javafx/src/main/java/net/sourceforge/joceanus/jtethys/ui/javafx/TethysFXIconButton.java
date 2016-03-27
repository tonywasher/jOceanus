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
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class TethysFXIconButton
        implements TethysIconButton<Node, Node> {
    /**
     * Button.
     */
    private final Button theButton;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected TethysFXIconButton(final TethysIconButtonManager<?, Node, Node> pManager) {
        /* Create the button */
        theButton = new Button();

        /* Create style of button */
        theButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        theButton.setAlignment(Pos.CENTER);
        theButton.setMaxWidth(Double.MAX_VALUE);

        /* Handle action */
        theButton.setOnAction(e -> pManager.progressToNextState());
    }

    @Override
    public Button getButton() {
        return theButton;
    }

    @Override
    public void setButtonState(final Node pIcon,
                               final String pToolTip) {
        /* Set the icon */
        theButton.setGraphic(pIcon);

        /* Set the ToolTip */
        Tooltip myToolTip = pToolTip == null
                                             ? null
                                             : new Tooltip(pToolTip);
        theButton.setTooltip(myToolTip);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setDisable(!pEnabled);
    }

    @Override
    public void setNullMargins() {
        theButton.setPadding(Insets.EMPTY);
    }

    /**
     * Simple FX IconButton Manager.
     * @param <T> the object type
     */
    public static class TethysFXSimpleIconButtonManager<T>
            extends TethysSimpleIconButtonManager<T, Node, Node> {
        /**
         * Constructor.
         */
        public TethysFXSimpleIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysFXIconButton(this));
        }

        @Override
        public TethysFXIconButton getButton() {
            return (TethysFXIconButton) super.getButton();
        }

        @Override
        public Button getNode() {
            return (Button) super.getNode();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            getNode().setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, pId == null
                                                ? null
                                                : TethysFXGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
        }
    }

    /**
     * State-based FX IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class TethysFXStateIconButtonManager<T, S>
            extends TethysStateIconButtonManager<T, S, Node, Node> {
        /**
         * Constructor.
         */
        public TethysFXStateIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysFXIconButton(this));
        }

        @Override
        public TethysFXIconButton getButton() {
            return (TethysFXIconButton) super.getButton();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            getNode().setVisible(pVisible);
        }

        @Override
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId,
                                                                          final String pToolTip) {
            setNewValueForValue(pValue, pNext);
            setIconForValue(pValue, pId == null
                                                ? null
                                                : TethysFXGuiUtils.getIconAtSize(pId, getWidth()));
            setTooltipForValue(pValue, pToolTip);
        }
    }
}
