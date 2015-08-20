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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.IconButton;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.SimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.StateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class IconFXButton
        extends Button
        implements IconButton<Node> {
    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected IconFXButton(final IconButtonManager<?, Node> pManager) {
        /* Create style of button */
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);

        /* Handle action */
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                pManager.progressToNextState();
            }
        });
    }

    @Override
    public void setButtonState(final Node pIcon,
                               final String pToolTip) {
        /* Set the icon */
        setGraphic(pIcon);

        /* Set the ToolTip */
        Tooltip myToolTip = pToolTip == null
                                             ? null
                                             : new Tooltip(pToolTip);
        setTooltip(myToolTip);
    }

    /**
     * Simple FX IconButton Manager.
     * @param <T> the object type
     */
    public static class SimpleFXIconButtonManager<T>
            extends SimpleIconButtonManager<T, Node> {
        /**
         * Constructor.
         */
        public SimpleFXIconButtonManager() {
            /* Create and declare the button */
            declareButton(new IconFXButton(this));
        }

        @Override
        public IconFXButton getButton() {
            return (IconFXButton) super.getButton();
        }
    }

    /**
     * State-based FX IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class StateFXIconButtonManager<T, S>
            extends StateIconButtonManager<T, S, Node> {
        /**
         * Constructor.
         */
        public StateFXIconButtonManager() {
            /* Create and declare the button */
            declareButton(new IconFXButton(this));
        }

        @Override
        public IconFXButton getButton() {
            return (IconFXButton) super.getButton();
        }
    }
}
