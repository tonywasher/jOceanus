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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;

import net.sourceforge.joceanus.jtethys.ui.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.DefaultIconButtonState;

/**
 * Utility panel to handle actions on selected item.
 * @param <E> the data type enum class
 */
public class ItemEditActions<E extends Enum<E>>
        extends JEnablePanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 9142612057288386005L;

    /**
     * The commit button.
     */
    private final JIconButton<Boolean> theCommitButton;

    /**
     * The undo button.
     */
    private final JIconButton<Boolean> theUndoButton;

    /**
     * The reset button.
     */
    private final JIconButton<Boolean> theResetButton;

    /**
     * The cancel button.
     */
    private final JIconButton<Boolean> theCancelButton;

    /**
     * The parent panel.
     */
    private final DataItemPanel<?, E> theParent;

    /**
     * Constructor.
     * @param pParent the parent panel
     */
    protected ItemEditActions(final DataItemPanel<?, E> pParent) {
        /* Store parameters */
        theParent = pParent;

        /* Create the button states */
        DefaultIconButtonState<Boolean> myCommitState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myUndoState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myResetState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myCancelState = new DefaultIconButtonState<Boolean>();

        /* Create the buttons */
        theCommitButton = new JIconButton<Boolean>(myCommitState);
        theUndoButton = new JIconButton<Boolean>(myUndoState);
        theResetButton = new JIconButton<Boolean>(myResetState);
        theCancelButton = new JIconButton<Boolean>(myCancelState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theCommitButton.setMargin(myInsets);
        theUndoButton.setMargin(myInsets);
        theResetButton.setMargin(myInsets);
        theCancelButton.setMargin(myInsets);

        /* Set the states */
        PrometheusIcons.buildCommitButton(myCommitState);
        PrometheusIcons.buildUndoButton(myUndoState);
        PrometheusIcons.buildResetButton(myResetState);
        PrometheusIcons.buildCancelButton(myCancelState);

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(0, ItemActions.STRUT_HEIGHT);

        /* Create the layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theCommitButton);
        add(Box.createRigidArea(myStrutSize));
        add(theUndoButton);
        add(Box.createRigidArea(myStrutSize));
        add(theResetButton);
        add(Box.createRigidArea(myStrutSize));
        add(theCancelButton);

        /* Create the listener */
        ItemListener myListener = new ItemListener();
        theCommitButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theUndoButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theResetButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theCancelButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
    }

    /**
     * Update state.
     */
    protected void updateState() {
        /* Set the values */
        theCommitButton.storeValue(Boolean.TRUE);
        theUndoButton.storeValue(Boolean.TRUE);
        theResetButton.storeValue(Boolean.TRUE);
        theCancelButton.storeValue(Boolean.TRUE);

        /* Determine whether we have changes */
        boolean hasUpdates = theParent.hasUpdates();
        theUndoButton.setEnabled(hasUpdates);
        theResetButton.setEnabled(hasUpdates);

        /* Check for no errors */
        boolean hasErrors = theParent.hasErrors();
        hasUpdates |= theParent.isNew();
        theCommitButton.setEnabled(hasUpdates && !hasErrors);
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
            if (theCommitButton.equals(o)) {
                theParent.requestCommit();
            } else if (theUndoButton.equals(o)) {
                theParent.requestUndo();
            } else if (theResetButton.equals(o)) {
                theParent.requestReset();
            } else if (theCancelButton.equals(o)) {
                theParent.requestCancel();
            }
        }
    }
}
