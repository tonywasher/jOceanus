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

import net.sourceforge.joceanus.jmetis.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.ErrorDisplay;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Error panel.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class PrometheusErrorPanel<N, I>
        implements ErrorDisplay, TethysEventProvider<PrometheusDataEvent>, TethysNode<N> {
    /**
     * Text for Clear Button.
     */
    private static final String NLS_CLEAR = PrometheusUIResource.ERROR_BUTTON_CLEAR.getValue();

    /**
     * Text for Box title.
     */
    private static final String NLS_TITLE = PrometheusUIResource.ERROR_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The error field.
     */
    private final TethysLabel<N, I> theErrorField;

    /**
     * The clear button.
     */
    private final TethysButton<N, I> theClearButton;

    /**
     * The data entry for the error.
     */
    private final MetisViewerEntry theDataError;

    /**
     * The error itself.
     */
    private final DataErrorList<MetisExceptionWrapper> theErrors;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pManager the data manager
     * @param pParent the parent data entry
     */
    public PrometheusErrorPanel(final TethysGuiFactory<N, I> pFactory,
                                final MetisViewerManager pManager,
                                final MetisViewerEntry pParent) {
        /* Create the error debug entry for this view */
        theDataError = pManager.newEntry(DataControl.DATA_ERROR);
        theDataError.addAsChildOf(pParent);
        theDataError.hideEntry();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the error list */
        theErrors = new DataErrorList<>();
        theDataError.setObject(theErrors);

        /* Create the error field */
        theErrorField = pFactory.newLabel();

        /* Create the clear button */
        theClearButton = pFactory.newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(NLS_CLEAR);

        /* Add the listener for item changes */
        theClearButton.getEventRegistrar().addEventListener(e -> clearErrors());

        /* Create the error panel */
        thePanel = pFactory.newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(theClearButton);
        thePanel.addNode(theErrorField);

        /* Set the Error panel to be red and invisible */
        thePanel.setVisible(false);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setVisible(final boolean bVisible) {
        thePanel.setVisible(bVisible);
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
            /* Show the debug */
            theDataError.showEntry();
        }

        /* Record the error */
        theErrors.add(new MetisExceptionWrapper(pException));

        /* Set the error text and display the panel */
        setErrorText(pException.getMessage());

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
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

    @Override
    public void setErrors(final DataErrorList<MetisExceptionWrapper> pExceptions) {
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

            /* Set the error text and display the panel */
            setErrorText(pExceptions.get(0).getMessage());
        }

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
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
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }
}