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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class TethysFXIconButton
        extends Button
        implements TethysIconButton<Node> {
    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected TethysFXIconButton(final TethysIconButtonManager<?, Node> pManager) {
        /* Create style of button */
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);

        /* Handle action */
        setOnAction(e -> pManager.progressToNextState());
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
    public static class TethysFXSimpleIconButtonManager<T>
            extends TethysSimpleIconButtonManager<T, Node> {
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
    }

    /**
     * State-based FX IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class TethysFXStateIconButtonManager<T, S>
            extends TethysStateIconButtonManager<T, S, Node> {
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
    }
}
