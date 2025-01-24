/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.swing.field;

import java.awt.Rectangle;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateConfig;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.tethys.swing.button.TethysUISwingDateButtonManager;
import net.sourceforge.joceanus.tethys.swing.button.TethysUISwingDateDialog;

/**
 * DateButtonField class.
 */
public class TethysUISwingDateButtonField
        extends TethysUISwingDataTextField<OceanusDate>
        implements TethysUIDateButtonField {
    /**
     * The date manager.
     */
    private final TethysUISwingDateButtonManager theManager;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, new JLabel());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pLabel the label
     */
    private TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory,
                                         final JLabel pLabel) {
        this(pFactory, pFactory.buttonFactory().newDateButton(), pLabel);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pManager the manager
     * @param pLabel the label
     */
    private TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory,
                                         final TethysUIDateButtonManager pManager,
                                         final JLabel pLabel) {
        /* Initialise underlying class */
        super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

        /* Store the manager and button */
        theManager = (TethysUISwingDateButtonManager) pManager;

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);
    }

    /**
     * handle Date Button event.
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
    public JButton getEditControl() {
        return (JButton) super.getEditControl();
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
    public void startCellEditing(final Rectangle pCell) {
        isCellEditing = true;
        final TethysUISwingDateDialog myDialog = theManager.getDialog();

        /* Show the dialog */
        myDialog.showDialogUnderRectangle(pCell);
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

    @Override
    public TethysUISwingDateButtonField cloneField(final JLabel pLabel) {
        return new TethysUISwingDateButtonField(getGuiFactory(), pLabel);
    }
}

