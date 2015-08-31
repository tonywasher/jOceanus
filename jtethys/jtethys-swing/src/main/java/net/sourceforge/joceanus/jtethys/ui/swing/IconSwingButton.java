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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.ui.IconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.IconButton;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.SimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager.StateIconButtonManager;

/**
 * Simple button that displays icon.
 */
public class IconSwingButton
        extends JButton
        implements IconButton<Icon> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -2570568843335400762L;

    /**
     * Constructor.
     * @param pManager the button manager
     */
    protected IconSwingButton(final IconButtonManager<?, Icon> pManager) {
        /* Create style of button */
        setHorizontalAlignment(SwingConstants.CENTER);

        /* Handle action */
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                pManager.progressToNextState();
            }
        });
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
    public static class SimpleSwingIconButtonManager<T>
            extends SimpleIconButtonManager<T, Icon> {
        /**
         * Constructor.
         */
        public SimpleSwingIconButtonManager() {
            /* Create and declare the button */
            declareButton(new IconSwingButton(this));
        }

        @Override
        public IconSwingButton getButton() {
            return (IconSwingButton) super.getButton();
        }
    }

    /**
     * State-based Swing IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     */
    public static class StateSwingIconButtonManager<T, S>
            extends StateIconButtonManager<T, S, Icon> {
        /**
         * Constructor.
         */
        public StateSwingIconButtonManager() {
            /* Create and declare the button */
            declareButton(new IconSwingButton(this));
        }

        @Override
        public IconSwingButton getButton() {
            return (IconSwingButton) super.getButton();
        }
    }
}
