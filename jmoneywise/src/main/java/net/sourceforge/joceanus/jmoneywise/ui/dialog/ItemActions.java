/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;

import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;

/**
 * Utility panel to handle actions on selected item.
 */
public class ItemActions
        extends JEnablePanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6509682125660951159L;

    /**
     * The edit button.
     */
    private final JIconButton<Boolean> theEditButton;

    /**
     * The delete button.
     */
    private final JIconButton<Boolean> theDeleteButton;

    /**
     * The parent panel.
     */
    private final DataItemPanel<?> theParent;

    /**
     * Constructor.
     * @param pParent the parent panel
     */
    protected ItemActions(final DataItemPanel<?> pParent) {
        /* Store parameters */
        theParent = pParent;

        /* Create the buttons */
        DefaultIconButtonState<Boolean> myEditState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myDeleteState = new DefaultIconButtonState<Boolean>();

        /* Create the buttons */
        theEditButton = new JIconButton<Boolean>(myEditState);
        theDeleteButton = new JIconButton<Boolean>(myDeleteState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theEditButton.setMargin(myInsets);
        theDeleteButton.setMargin(myInsets);

        /* Set the states */
        MoneyWiseIcons.buildEditButton(myEditState);
        MoneyWiseIcons.buildDeleteButton(myDeleteState);

        /* Create the layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theEditButton);
        add(theDeleteButton);

        /* Create the listener */
        ItemListener myListener = new ItemListener();
        theEditButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theDeleteButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
    }

    /**
     * Update state.
     */
    protected void updateState() {
        /* Set the edit value and enable state */
        theEditButton.storeValue(Boolean.TRUE);
        theEditButton.setEnabled(theParent.isEditable());

        /* Set the delete value and enable state */
        theDeleteButton.storeValue(Boolean.TRUE);
        theDeleteButton.setEnabled(theParent.isDeletable());
    }

    /**
     * Item Listener.
     */
    private final class ItemListener
            implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle requested actions */
            if (theEditButton.equals(o)) {
                theParent.requestEdit();
            } else if (theDeleteButton.equals(o)) {
                theParent.requestDelete();
            }
        }
    }
}
