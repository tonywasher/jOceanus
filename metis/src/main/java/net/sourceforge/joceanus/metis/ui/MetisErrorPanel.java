/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.metis.ui;

import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerErrorList;
import net.sourceforge.joceanus.metis.viewer.MetisViewerExceptionWrapper;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * Error panel.
 */
public class MetisErrorPanel
        implements OceanusEventProvider<MetisUIEvent>, TethysUIComponent {
    /**
     * Text for Clear Button.
     */
    private static final String NLS_CLEAR = MetisUIResource.ERROR_BUTTON_CLEAR.getValue();

    /**
     * Text for Box title.
     */
    private static final String NLS_TITLE = MetisUIResource.ERROR_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisUIEvent> theEventManager;

    /**
     * The Panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The error field.
     */
    private final TethysUILabel theErrorField;

    /**
     * The clear button.
     */
    private final TethysUIButton theClearButton;

    /**
     * The viewer entry for the error.
     */
    private final MetisViewerEntry theViewerError;

    /**
     * The error itself.
     */
    private final MetisViewerErrorList theErrors;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pViewerMgr the Viewer manager
     * @param pParent the parent viewer entry
     */
    public MetisErrorPanel(final TethysUIFactory<?> pFactory,
                           final MetisViewerManager pViewerMgr,
                           final MetisViewerEntry pParent) {
        /* Create the error viewer entry for this view */
        theViewerError = pViewerMgr.newEntry(pParent, MetisViewerStandardEntry.ERROR.toString());
        theViewerError.setVisible(false);

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the error list */
        theErrors = new MetisViewerErrorList();
        theViewerError.setObject(theErrors);

        /* Create the error field */
        theErrorField = pFactory.controlFactory().newLabel();
        theErrorField.setErrorText();

        /* Create the clear button */
        theClearButton = pFactory.buttonFactory().newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(NLS_CLEAR);

        /* Add the listener for item changes */
        theClearButton.getEventRegistrar().addEventListener(e -> clearErrors());

        /* Create the error panel */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(theClearButton);
        thePanel.addNode(theErrorField);

        /* Set the Error panel to be red and invisible */
        thePanel.setVisible(false);
    }

    @Override
    public OceanusEventRegistrar<MetisUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theClearButton.setEnabled(bEnabled);
    }

    /**
     * Do we have an error?
     * @return true/false
     */
    public boolean hasError() {
        return !theErrors.isEmpty();
    }

    /**
     * Set error indication for window.
     * @param pException the exception
     */
    public void addError(final OceanusException pException) {
        /* If we do not currently have an error */
        if (!hasError()) {
            /* Show the viewer entry */
            theViewerError.setVisible(true);
        }

        /* Record the error */
        theErrors.add(new MetisViewerExceptionWrapper(pException));

        /* Set the error text and display the panel */
        setErrorText(pException.getMessage());

        /* Notify listeners */
        theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
    }

    /**
     * Show validation error.
     * @param pError the error message
     */
    public void showValidateError(final String pError) {
        if (pError != null) {
            setErrorText(pError);
        } else {
            thePanel.setVisible(false);
        }
    }

    /**
     * Set error text for window.
     * @param pText the text
     */
    private void setErrorText(final String pText) {
        /* Set the string for the error field */
        theErrorField.setText(pText);

        /* Make the panel visible */
        thePanel.setVisible(true);
    }

    /**
     * Set error list.
     * @param pExceptions the error list
     */
    public void setErrors(final MetisViewerErrorList pExceptions) {
        /* If we currently have an error */
        if (hasError()) {
            /* Clear the error */
            theErrors.clear();
            theViewerError.setVisible(false);
        }

        /* If we have some exceptions */
        if (!pExceptions.isEmpty()) {
            /* Show the debug */
            theViewerError.setVisible(true);

            /* Add the new errors */
            theErrors.addList(pExceptions);

            /* Set the error text and display the panel */
            setErrorText(pExceptions.getUnderlyingList().get(0).getMessage());
        }

        /* Notify listeners */
        theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
    }

    /**
     * Clear error indication for this window.
     */
    private void clearErrors() {
        /* If we currently have an error */
        if (hasError()) {
            /* Clear the error */
            theErrors.clear();
            theViewerError.setVisible(false);
        }

        /* Make the panel invisible */
        setVisible(false);

        /* Notify listeners */
        theEventManager.fireEvent(MetisUIEvent.VISIBILITY);
    }
}
