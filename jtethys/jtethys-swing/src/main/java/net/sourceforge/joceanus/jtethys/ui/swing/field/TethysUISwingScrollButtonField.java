/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingScrollMenu;

/**
 * ScrollButtonField class.
 * @param <T> the data type
 */
public final class TethysUISwingScrollButtonField<T>
        extends TethysUISwingDataTextField<T>
        implements TethysUIScrollButtonField<T> {
    /**
     * The scroll manager.
     */
    private final TethysUISwingScrollButtonManager<T> theManager;

    /**
     * The configurator.
     */
    private Consumer<TethysUIScrollMenu<T>> theConfigurator;

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
    TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                   final Class<T> pClazz) {
        this(pFactory, pClazz, new JLabel());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pClazz the value class
     * @param pLabel the label
     */
    private TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                           final Class<T> pClazz,
                                           final JLabel pLabel) {
        this(pFactory, pFactory.buttonFactory().newScrollButton(pClazz), pLabel);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pManager the manager
     * @param pLabel the label
     */
    private TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                           final TethysUIScrollButtonManager<T> pManager,
                                           final JLabel pLabel) {
        /* Initialise underlying class */
        super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

        /* Store the manager and button */
        theManager = (TethysUISwingScrollButtonManager<T>) pManager;
        theButton = getEditControl();

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);
        theManager.getMenu().getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> haltCellEditing());

        /* Set configurator */
        theConfigurator = p -> {
        };
    }

    @Override
    public T getCastValue(final Object pValue) {
        return theManager.getValueClass().cast(pValue);
    }

    /**
     * handle Scroll Button event.
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
    public void setMenuConfigurator(final Consumer<TethysUIScrollMenu<T>> pConfigurator) {
        theConfigurator = pConfigurator;
        theManager.setMenuConfigurator(theConfigurator);
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
        getLabel().setText(theButton.getText());
    }

    @Override
    public void startCellEditing(final Rectangle pCell) {
        isCellEditing = true;
        final TethysUISwingScrollMenu<T> myMenu = theManager.getMenu();
        theConfigurator.accept(myMenu);
        if (!myMenu.isEmpty()) {
            myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
        } else {
            haltCellEditing();
        }
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
    public TethysUISwingScrollButtonField<T> cloneField(final JLabel pLabel) {
        return new TethysUISwingScrollButtonField<>(getGuiFactory(), theManager.getValueClass(), pLabel);
    }
}
