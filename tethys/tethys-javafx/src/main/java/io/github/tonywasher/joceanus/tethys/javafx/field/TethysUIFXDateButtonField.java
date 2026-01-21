/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.tethys.javafx.field;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateConfig;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIDateButtonManager;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXNode;
import io.github.tonywasher.joceanus.tethys.javafx.button.TethysUIFXDateButtonManager;
import io.github.tonywasher.joceanus.tethys.javafx.button.TethysUIFXDateDialog;

import java.util.function.Consumer;

/**
 * DateButtonField class.
 */
public class TethysUIFXDateButtonField
        extends TethysUIFXDataTextField<OceanusDate>
        implements TethysUIDateButtonField {
    /**
     * The date manager.
     */
    private final TethysUIFXDateButtonManager theManager;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXDateButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, pFactory.buttonFactory().newDateButton());
    }

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pManager the manager
     */
    private TethysUIFXDateButtonField(final TethysUICoreFactory<?> pFactory,
                                      final TethysUIDateButtonManager pManager) {
        /* Initialise underlying class */
        super(pFactory, TethysUIFXNode.getNode(pManager));

        /* Store the manager */
        theManager = (TethysUIFXDateButtonManager) pManager;

        /* Set padding */
        getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);
    }

    /**
     * handle Date Button event.
     *
     * @param pEvent the even
     */
    private void handleEvent(final OceanusEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setValue(theManager.getSelectedDate());
                fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                break;
            case EDITFOCUSLOST:
                haltCellEditing();
                break;
            default:
                break;
        }
    }

    @Override
    public void setDateConfigurator(final Consumer<OceanusDateConfig> pConfigurator) {
        theManager.setDateConfigurator(pConfigurator);
    }

    @Override
    protected Button getEditControl() {
        return (Button) super.getEditControl();
    }

    @Override
    public void setValue(final OceanusDate pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        theManager.setSelectedDate(pValue);
        getLabel().setText(theManager.getText());
    }

    @Override
    public void startCellEditing(final Node pCell) {
        /* Note editing */
        isCellEditing = true;
        final TethysUIFXDateDialog myDialog = theManager.getDialog();

        /* Show the dialog */
        myDialog.showDialogUnderNode(pCell);
    }

    /**
     * haltCellEditing.
     */
    private void haltCellEditing() {
        if (isCellEditing) {
            fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
        }
        isCellEditing = false;
    }
}

