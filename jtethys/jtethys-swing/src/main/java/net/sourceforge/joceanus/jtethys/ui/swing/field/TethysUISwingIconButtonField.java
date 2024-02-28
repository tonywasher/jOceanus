/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.field;

import java.awt.Rectangle;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * IconButtonField class.
 * @param <T> the data type
 */
public final class TethysUISwingIconButtonField<T>
        extends TethysUISwingDataTextField<T>
        implements TethysUIIconButtonField<T> {
    /**
     * The icon manager.
     */
    private final TethysUIIconButtonManager<T> theManager;

    /**
     * The button.
     */
    private final JButton theButton;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pClazz the value class
     */
    TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory,
                                 final Class<T> pClazz) {
        this(pFactory, pClazz, new JLabel());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pClazz the value class
     * @param pLabel the label
     */
    private TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory,
                                         final Class<T> pClazz,
                                         final JLabel pLabel) {
        this(pFactory, pFactory.buttonFactory().newIconButton(pClazz), pLabel);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pManager the manager
     * @param pLabel the label
     */
    private TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory,
                                         final TethysUIIconButtonManager<T> pManager,
                                         final JLabel pLabel) {
        /* Initialise underlying class */
        super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

        /* Store the manager and button */
        theManager = pManager;
        theButton = getEditControl();
        pLabel.setHorizontalAlignment(SwingConstants.CENTER);

        /* Set listener on manager */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = pManager.getEventRegistrar();
        myRegistrar.addEventListener(this::handleEvent);
    }

    @Override
    public T getCastValue(final Object pValue) {
        return theManager.getValueClass().cast(pValue);
    }

    /**
     * handle Icon Button event.
     * @param pEvent the even
     */
    private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setValue(theManager.getValue());
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
    public JButton getEditControl() {
        return (JButton) super.getEditControl();
    }

    @Override
    public void setValue(final T pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        theManager.setValue(pValue);
        getLabel().setIcon(theButton.getIcon());
    }

    @Override
    public void startCellEditing(final Rectangle pCell) {
        isCellEditing = true;
        SwingUtilities.invokeLater(theButton::doClick);
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
    public void setIconMapSet(final Supplier<TethysUIIconMapSet<T>> pSupplier) {
        theManager.setIconMapSet(pSupplier);
    }

    @Override
    public TethysUISwingIconButtonField<T> cloneField(final JLabel pLabel) {
        final TethysUIIconButtonManager<T> myClone = getGuiFactory().buttonFactory().newIconButton(theManager.getValueClass());
        return new TethysUISwingIconButtonField<>(getGuiFactory(), myClone, pLabel);
    }
}

