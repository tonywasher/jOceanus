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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class TethysSwingIconButton
        extends JButton
        implements TethysIconButton<Icon> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -2570568843335400762L;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected TethysSwingIconButton(final TethysIconButtonManager<?, Icon> pManager) {
        /* Create style of button */
        setHorizontalAlignment(SwingConstants.CENTER);

        /* Handle action */
        addActionListener(e -> pManager.progressToNextState());
    }

    @Override
    public void setButtonState(final Icon pIcon,
                               final String pToolTip) {
        /* Set the icon */
        setIcon(pIcon);

        /* Set the ToolTip */
        setToolTipText(pToolTip);
    }

    /**
     * Simple Swing IconButton Manager.
     * @param <T> the object type
     */
    public static class TethysSwingSimpleIconButtonManager<T>
            extends TethysSimpleIconButtonManager<T, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingSimpleIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysSwingIconButton(this));
        }

        @Override
        public TethysSwingIconButton getButton() {
            return (TethysSwingIconButton) super.getButton();
        }
    }

    /**
     * State-based Swing IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class TethysSwingStateIconButtonManager<T, S>
            extends TethysStateIconButtonManager<T, S, Icon> {
        /**
         * Constructor.
         */
        public TethysSwingStateIconButtonManager() {
            /* Create and declare the button */
            declareButton(new TethysSwingIconButton(this));
        }

        @Override
        public TethysSwingIconButton getButton() {
            return (TethysSwingIconButton) super.getButton();
        }
    }
}
