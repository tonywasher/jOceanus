/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.javafx.field;

import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;

import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * IconButtonField class.
 *
 * @param <T> the data type
 */
public final class TethysUIFXIconButtonField<T>
        extends TethysUIFXDataTextField<T>
        implements TethysUIIconButtonField<T> {
    /**
     * The icon manager.
     */
    private final TethysUIIconButtonManager<T> theManager;

    /**
     * The button.
     */
    private final Button theButton;

    /**
     * The icon.
     */
    private Node theIcon;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pClazz the value class
     */
    TethysUIFXIconButtonField(final TethysUICoreFactory<?> pFactory,
                              final Class<T> pClazz) {
        this(pFactory, pFactory.buttonFactory().newIconButton(pClazz));
    }

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pManager the manager
     */
    TethysUIFXIconButtonField(final TethysUICoreFactory<?> pFactory,
                              final TethysUIIconButtonManager<T> pManager) {
        /* Initialise underlying class */
        super(pFactory, TethysUIFXNode.getNode(pManager));

        /* Store the manager and button */
        theManager = pManager;
        theButton = getEditControl();

        /* Set padding */
        getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        getLabel().setAlignment(Pos.CENTER);

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(e -> {
            setValue(theManager.getValue());
            fireEvent(TethysUIEvent.NEWVALUE, e.getDetails());
        });
    }

    @Override
    public T getCastValue(final Object pValue) {
        return theManager.getValueClass().cast(pValue);
    }

    @Override
    protected Button getEditControl() {
        return (Button) super.getEditControl();
    }

    @Override
    public void setValue(final T pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        theManager.setValue(pValue);
        theIcon = theButton.getGraphic();

        /* If we are not currently editable */
        if (!isEditable()) {
            /* Switch the icon into the label */
            theButton.setGraphic(null);
            getLabel().setGraphic(theIcon);
        }
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Obtain current setting */
        final boolean isEditable = isEditable();

        /* If we are changing */
        if (pEditable != isEditable) {
            /* If we are setting editable */
            if (pEditable) {
                /* Switch the icon into the button */
                getLabel().setGraphic(null);
                theButton.setGraphic(theIcon);
            } else {
                /* Switch the icon into the label */
                theButton.setGraphic(null);
                getLabel().setGraphic(theIcon);
            }

            /* Pass call on */
            super.setEditable(pEditable);
        }
    }

    @Override
    public void startCellEditing(final Node pCell) {
        Platform.runLater(theButton::fire);
    }

    @Override
    public void setIconMapSet(final Supplier<TethysUIIconMapSet<T>> pSupplier) {
        theManager.setIconMapSet(pSupplier);
    }
}

