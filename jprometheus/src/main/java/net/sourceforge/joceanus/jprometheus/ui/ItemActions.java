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

import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Utility panel to handle actions on selected item.
 * @param <E> the data type enum class
 */
public class ItemActions<E extends Enum<E>>
        extends JEnablePanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6509682125660951159L;

    /**
     * Strut height.
     */
    protected static final int STRUT_HEIGHT = 5;

    /**
     * The goTo button.
     */
    private final JScrollButton<ActionDetailEvent> theGoToButton;

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
    private final DataItemPanel<?, E> theParent;

    /**
     * Constructor.
     * @param pParent the parent panel
     */
    protected ItemActions(final DataItemPanel<?, E> pParent) {
        /* Store parameters */
        theParent = pParent;

        /* Create the buttons */
        DefaultIconButtonState<Boolean> myEditState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myDeleteState = new DefaultIconButtonState<Boolean>();

        /* Create the buttons */
        theGoToButton = PrometheusIcons.getGotoButton();
        theEditButton = new JIconButton<Boolean>(myEditState);
        theDeleteButton = new JIconButton<Boolean>(myDeleteState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theGoToButton.setMargin(myInsets);
        theEditButton.setMargin(myInsets);
        theDeleteButton.setMargin(myInsets);

        /* Set the states */
        PrometheusIcons.buildEditButton(myEditState);
        PrometheusIcons.buildDeleteButton(myDeleteState);

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(0, STRUT_HEIGHT);

        /* Create the layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theGoToButton);
        add(Box.createRigidArea(myStrutSize));
        add(theEditButton);
        add(Box.createRigidArea(myStrutSize));
        add(theDeleteButton);

        /* Create the listener */
        ItemListener myListener = new ItemListener();
        theGoToButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
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
            implements ChangeListener, PropertyChangeListener {
        /**
         * MenuBuilder.
         */
        private JScrollMenuBuilder<ActionDetailEvent> theMenuBuilder;

        /**
         * Constructor.
         */
        private ItemListener() {
            theMenuBuilder = theGoToButton.getMenuBuilder();
            theMenuBuilder.addChangeListener(this);
            theParent.declareGoToMenuBuilder(theMenuBuilder);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle requested actions */
            if (theEditButton.equals(o)) {
                theParent.requestEdit();
            } else if (theDeleteButton.equals(o)) {
                theParent.requestDelete();
            } else if (theGoToButton.equals(o)) {
                theParent.processGoToRequest(theGoToButton.getValue());
            }
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            theMenuBuilder.clearMenu();
            theParent.buildGoToMenu();
        }
    }
}
