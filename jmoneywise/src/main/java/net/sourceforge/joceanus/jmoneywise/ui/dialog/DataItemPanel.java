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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;

/**
 * Class to enable display/editing of and individual dataItem.
 * @param <T> the item type
 */
public abstract class DataItemPanel<T extends DataItem<MoneyWiseDataType>>
        extends JPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7514751065536367674L;

    /**
     * Padding size.
     */
    protected static final int PADDING_SIZE = 5;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<T> theFieldSet;

    /**
     * The Update Set.
     */
    private transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The Item.
     */
    private transient T theItem;

    /**
     * Obtain the field Set.
     * @return the FieldSet
     */
    protected JFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain the item.
     * @return the item
     */
    protected T getItem() {
        return theItem;
    }

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    protected DataItemPanel(final JFieldManager pFieldMgr) {
        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<T>(pFieldMgr);

        /* Create listener */
        FieldListener myListener = new FieldListener();
        theFieldSet.addActionListener(myListener);
    }

    /**
     * Set editable item.
     * @param pUpdateSet the base update set
     * @param pItem the item
     */
    public void setEditableItem(final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                final T pItem) {
        /* Clone the update set */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(pUpdateSet);

        /* Store the element */
        theItem = pItem;

        /* adjust fields */
        adjustFields(true);

        /* Render the FieldSet */
        theFieldSet.renderSet(pItem);
    }

    /**
     * Set readOnly item.
     * @param pItem the item
     */
    public void setReadOnlyItem(final T pItem) {
        /* Null the update set */
        theUpdateSet = null;

        /* Store the element */
        theItem = pItem;

        /* adjust Fields */
        adjustFields(false);

        /* Render the FieldSet */
        theFieldSet.renderSet(theItem);
    }

    /**
     * Adjust Fields.
     * @param isEditable is the item editable?
     */
    protected abstract void adjustFields(final boolean isEditable);

    /**
     * Update the field.
     * @param pUpdate the update
     * @throws JOceanusException on error
     */
    protected abstract void updateField(final FieldUpdate pUpdate) throws JOceanusException;

    /**
     * FieldListener class.
     */
    private final class FieldListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If the event relates to the Field Set */
            if ((theFieldSet.equals(o)) && (e instanceof ActionDetailEvent)) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                Object dtl = evt.getDetails();
                if (dtl instanceof FieldUpdate) {
                    /* Update the item */
                    updateItem((FieldUpdate) dtl);
                }
            }
        }

        /**
         * Update item.
         * @param pUpdate the update
         */
        private void updateItem(final FieldUpdate pUpdate) {
            /* Push history */
            theItem.pushHistory();

            /* Protect against exceptions */
            try {
                /* Update the field */
                updateField(pUpdate);

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Reset values */
                theItem.popHistory();

                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to update field", e);

                /* Show the error */
                // theError.addError(myError);
                return;
            }

            /* Check for changes */
            if (theItem.checkForHistory()) {
                /* Increment the update version */
                theUpdateSet.incrementVersion();
            }
        }
    }
}
