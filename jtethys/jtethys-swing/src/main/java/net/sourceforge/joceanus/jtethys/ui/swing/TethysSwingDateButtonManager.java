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

import java.awt.event.WindowFocusListener;

import javax.swing.SwingConstants;

import net.sourceforge.jdatebutton.swing.JDateButton;
import net.sourceforge.jdatebutton.swing.JDateConfig;
import net.sourceforge.jdatebutton.swing.JDateDialog;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;

/**
 * JavaFX DateButton Manager.
 */
public class TethysSwingDateButtonManager
        extends TethysDateButtonManager<JDateButton> {
    /**
     * The button.
     */
    private final JDateButton theButton;

    /**
     * Constructor.
     */
    public TethysSwingDateButtonManager() {
        this(new TethysDateFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the date formatter
     */
    public TethysSwingDateButtonManager(final TethysDateFormatter pFormatter) {
        this(new JDateConfig(pFormatter), pFormatter);
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     * @param pFormatter the date formatter
     */
    public TethysSwingDateButtonManager(final JDateConfig pConfig,
                                        final TethysDateFormatter pFormatter) {
        /* Initialise the super-class */
        super(pConfig, pFormatter);

        /* Create and declare the button */
        theButton = new JDateButton(pConfig);
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
        theButton.setVerticalAlignment(SwingConstants.CENTER);
        theButton.setHorizontalTextPosition(SwingConstants.LEFT);

        /* Catch the dialog opening/closing */
        JDateDialog myDialog = theButton.getDialog();
        myDialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(final java.awt.event.WindowEvent e) {
                handleDialogRequest();
            }

            @Override
            public void windowLostFocus(final java.awt.event.WindowEvent e) {
                if (myDialog.haveSelected()) {
                    handleNewValue();
                }
            }
        });
    }

    /**
     * Obtain button.
     * @return the button
     */
    @Override
    public JDateButton getButton() {
        return theButton;
    }
}
