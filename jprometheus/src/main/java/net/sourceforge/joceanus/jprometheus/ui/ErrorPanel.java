/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet.ErrorDisplay;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;

/**
 * Error panel.
 * @author Tony Washer
 */
public class ErrorPanel
        extends JEventPanel
        implements ErrorDisplay {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1868069138054965874L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ErrorPanel.class.getName());

    /**
     * Text for Clear Button.
     */
    private static final String NLS_CLEAR = NLS_BUNDLE.getString("ClearButton");

    /**
     * Text for Box title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("BoxTitle");

    /**
     * The error field.
     */
    private final JLabel theErrorField;

    /**
     * The clear button.
     */
    private final JButton theClearButton;

    /**
     * The data entry for the error.
     */
    private final transient JDataEntry theDataError;

    /**
     * The error itself.
     */
    private final transient DataErrorList<JMetisExceptionWrapper> theErrors;

    /**
     * Do we have an error?
     * @return true/false
     */
    public boolean hasError() {
        return !theErrors.isEmpty();
    }

    /**
     * Constructor.
     * @param pManager the data manager
     * @param pParent the parent data entry
     */
    public ErrorPanel(final JDataManager pManager,
                      final JDataEntry pParent) {
        /* Create the error debug entry for this view */
        theDataError = pManager.new JDataEntry(DataControl.DATA_ERROR);
        theDataError.addAsChildOf(pParent);
        theDataError.hideEntry();

        /* Create the error list */
        theErrors = new DataErrorList<JMetisExceptionWrapper>();
        theDataError.setObject(theErrors);

        /* Create the error field */
        theErrorField = new JLabel();

        /* Create the clear button */
        theClearButton = new JButton(NLS_CLEAR);

        /* Add the listener for item changes */
        theClearButton.addActionListener(new ErrorListener());

        /* Create the error panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theClearButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theErrorField);

        /* Set the Error panel to be red and invisible */
        theErrorField.setForeground(Color.red);
        setVisible(false);
    }

    /**
     * Set error indication for window.
     * @param pException the exception
     */
    public void addError(final JOceanusException pException) {
        /* If we do not currently have an error */
        if (!hasError()) {
            /* Show the debug */
            theDataError.showEntry();
        }

        /* Record the error */
        theErrors.add(new JMetisExceptionWrapper(pException));

        /* Set the string for the error field */
        theErrorField.setText(pException.getMessage());

        /* Make the panel visible */
        setVisible(true);

        /* Notify listeners */
        fireStateChanged();
    }

    @Override
    public void setErrors(final DataErrorList<JMetisExceptionWrapper> pExceptions) {
        /* If we currently have an error */
        if (hasError()) {
            /* Clear the error */
            theErrors.clear();
            theDataError.hideEntry();
        }

        /* If we have some exceptions */
        if (!pExceptions.isEmpty()) {
            /* Show the debug */
            theDataError.showEntry();

            /* Add the new errors */
            theErrors.addList(pExceptions);

            /* Set the string for the error field */
            theErrorField.setText(pExceptions.get(0).getMessage());

            /* Make the panel visible */
            setVisible(true);
        }

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Clear error indication for this window.
     */
    public void clearErrors() {
        /* If we currently have an error */
        if (hasError()) {
            /* Clear the error */
            theErrors.clear();
            theDataError.hideEntry();
        }

        /* Make the panel invisible */
        setVisible(false);

        /* Notify listeners */
        fireStateChanged();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theClearButton.setEnabled(bEnabled);
    }

    /**
     * Listener class.
     */
    private class ErrorListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* If this event relates to the Clear box */
            if (evt.getSource() == theClearButton) {
                /* Clear the error */
                clearErrors();
            }
        }
    }
}
