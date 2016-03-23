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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.SwingConstants;

import net.sourceforge.jdatebutton.swing.JDateButton;
import net.sourceforge.jdatebutton.swing.JDateConfig;
import net.sourceforge.jdatebutton.swing.JDateDialog;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
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
     * The dialog.
     */
    private final JDateDialog theDialog;

    /**
     * Constructor.
     */
    public TethysSwingDateButtonManager() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysSwingDateButtonManager(final TethysDataFormatter pFormatter) {
        this(new JDateConfig(pFormatter.getDateFormatter()), pFormatter);
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     * @param pFormatter the data formatter
     */
    public TethysSwingDateButtonManager(final JDateConfig pConfig,
                                        final TethysDataFormatter pFormatter) {
        /* Initialise the super-class */
        super(pConfig, pFormatter);

        /* Create and declare the button */
        theButton = new JDateButton(pConfig);
        theButton.setHorizontalAlignment(SwingConstants.CENTER);
        theButton.setVerticalAlignment(SwingConstants.CENTER);
        theButton.setHorizontalTextPosition(SwingConstants.LEFT);

        /* Catch the dialog opening/closing */
        theDialog = theButton.getDialog();
        theDialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(final WindowEvent e) {
                handleDialogRequest();
            }

            @Override
            public void windowLostFocus(final WindowEvent e) {
                handleDialogClosure();
            }
        });
    }

    @Override
    public JDateButton getNode() {
        return theButton;
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        theButton.setEnabled(pEnable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    /**
     * Handle dialog closure.
     */
    private void handleDialogClosure() {
        if (theDialog.haveSelected()) {
            handleNewValue();
        } else {
            handleDialogClosed();
        }
    }
}
