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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Class to enable display/editing of and individual dataItem.
 * @param <T> the item type
 */
public abstract class DataItemPanel<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        extends JEnablePanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7514751065536367674L;

    /**
     * Padding size.
     */
    protected static final int PADDING_SIZE = 5;

    /**
     * Field Height.
     */
    protected static final int FIELD_HEIGHT = 20;

    /**
     * Character Width.
     */
    protected static final int CHAR_WIDTH = 15;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<T> theFieldSet;

    /**
     * The Update Set.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The ErrorPanel.
     */
    private final ErrorPanel theError;

    /**
     * The Item.
     */
    private transient T theItem;

    /**
     * The EditVersion.
     */
    private transient int theEditVersion;

    /**
     * Obtain the field Set.
     * @return the FieldSet
     */
    protected JFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain the Update Set.
     * @return the UpdateSet
     */
    protected UpdateSet<MoneyWiseDataType> getUpdateSet() {
        return theUpdateSet;
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
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected DataItemPanel(final JFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final ErrorPanel pError) {
        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<T>(pFieldMgr);
        theUpdateSet = pUpdateSet;
        theError = pError;

        /* Create listener */
        FieldListener myListener = new FieldListener();
        theFieldSet.addActionListener(myListener);
    }

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    public void setEditable(final boolean isEditable) {
        /* If we have an item */
        if (theItem != null) {
            /* Determine EditVersion */
            theEditVersion = isEditable
                                       ? theUpdateSet.getVersion()
                                       : -1;

            /* adjust fields */
            setVisible(true);
            theFieldSet.setEditable(isEditable);
            adjustFields(isEditable);

            /* Render the FieldSet */
            theFieldSet.renderSet(theItem);
        } else {
            /* Set EditVersion */
            theEditVersion = -1;

            /* Set visibility */
            setVisible(false);
        }
    }

    /**
     * Set readOnly item.
     * @param pItem the item
     */
    public void setItem(final T pItem) {
        /* Store the element */
        theItem = pItem;

        /* Set readOnly */
        setEditable(false);
    }

    /**
     * Refresh data.
     */
    public abstract void refreshData();

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
     * Obtain the list for a class in base updateSet.
     * @param <L> the list type
     * @param <X> the object type
     * @param pDataType the data type
     * @param pClass the list class
     * @return the list
     */
    public <L extends DataList<X, MoneyWiseDataType>, X extends DataItem<MoneyWiseDataType> & Comparable<? super X>>
            L
            findDataList(final MoneyWiseDataType pDataType,
                         final Class<L> pClass) {
        /* Look up the base list */
        return theUpdateSet.findDataList(pDataType, pClass);
    }

    /**
     * Restrict field.
     * @param pComponent the component to restrict
     * @param pWidth field width in characters
     */
    protected void restrictField(final JComponent pComponent,
                                 final int pWidth) {
        /* Allocate Dimension */
        Dimension myPrefDims = new Dimension(pWidth * CHAR_WIDTH, FIELD_HEIGHT);
        Dimension myMaxDims = new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT);

        /* Restrict the field */
        pComponent.setPreferredSize(myPrefDims);
        pComponent.setMaximumSize(myMaxDims);
    }

    /**
     * Do we have any updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theEditVersion != -1
               && theEditVersion < theUpdateSet.getVersion();
    }

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
                theError.addError(myError);
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
