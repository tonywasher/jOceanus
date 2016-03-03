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

import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.jdatebutton.javafx.JDateConfig;
import net.sourceforge.jdatebutton.javafx.JDateDialog;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;

/**
 * JavaFX DateButton Manager.
 */
public class TethysFXDateButtonManager
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
    public TethysFXDateButtonManager() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysFXDateButtonManager(final TethysDataFormatter pFormatter) {
        this(new JDateConfig(pFormatter.getDateFormatter()), pFormatter);
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     * @param pFormatter the data formatter
     */
    public TethysFXDateButtonManager(final JDateConfig pConfig,
                                     final TethysDataFormatter pFormatter) {
        /* Initialise the super-class */
        super(pConfig, pFormatter);

        /* Create and declare the button */
        theButton = new JDateButton(pConfig);
        theButton.setMaxWidth(Double.MAX_VALUE);

        /* Catch the dialog opening/closing */
        theDialog = theButton.getDialog();
        theDialog.setOnShowing(e -> handleDialogRequest());
        theDialog.setOnHidden(e -> handleDialogClosure());
    }

    /**
     * Obtain button.
     * @return the button
     */
    @Override
    public JDateButton getButton() {
        return theButton;
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
