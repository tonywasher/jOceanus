/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingScrollMenu;

/**
 * ListButtonField class.
 * @param <T> the data type
 */
public class TethysUISwingListButtonField<T extends Comparable<T>>
        extends TethysUISwingDataTextField<List<T>>
        implements TethysUIListButtonField<T> {
    /**
     * The icon manager.
     */
    private final TethysUISwingListButtonManager<T> theManager;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, new JLabel());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pLabel the label
     */
    private TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory,
                                         final JLabel pLabel) {
        this(pFactory, pFactory.buttonFactory().newListButton(), pLabel);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pManager the manager
     * @param pLabel the label
     */
    private TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory,
                                         final TethysUIListButtonManager<T> pManager,
                                         final JLabel pLabel) {
        /* Initialise underlying class */
        super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

        /* Store the manager and button */
        theManager = (TethysUISwingListButtonManager<T>) pManager;

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);
        theManager.getMenu().getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> haltCellEditing());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getCastValue(final Object pValue) {
        return (List<T>) pValue;
    }

    /**
     * handle List Button event.
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
    public void setValue(final List<T> pValue) {
        super.setValue(pValue);
        theManager.setValue(pValue);
        updateText();
    }

    /**
     * Update the button text.
     */
    private void updateText() {
        getLabel().setText(theManager.getText());
    }

    @Override
    public void startCellEditing(final Rectangle pCell) {
        isCellEditing = true;
        theManager.buildMenu();
        final TethysUISwingScrollMenu<T> myMenu = theManager.getMenu();
        myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
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
    public TethysUISwingListButtonField<T> cloneField(final JLabel pLabel) {
        return new TethysUISwingListButtonField<>(getGuiFactory(), pLabel);
    }

    @Override
    public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
        theManager.setSelectables(pSelectables);
    }
}

