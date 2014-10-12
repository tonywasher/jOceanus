/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.field;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.event.JEventObject;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollListButton;

/**
 * Field Set. This handles a set of fields for an item, populating the fields rendering and parsing the data.
 * @param <T> the Data Item type
 */
public class JFieldSet<T extends JFieldSetItem>
        extends JEventObject {
    /**
     * The map of fields.
     */
    private final Map<JDataField, JFieldElement<T>> theMap;

    /**
     * The Render Manager.
     */
    private final transient JFieldManager theRenderMgr;

    /**
     * The Data Formatter.
     */
    private final transient JDataFormatter theFormatter;

    /**
     * Is the data being refreshed?
     */
    private transient boolean isRefreshing = false;

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Set refreshing data flag.
     * @param refreshingData true/false
     */
    public void setRefreshingData(final boolean refreshingData) {
        /* Record flag */
        isRefreshing = refreshingData;
    }

    /**
     * Constructor.
     * @param pRenderMgr the render manager
     */
    public JFieldSet(final JFieldManager pRenderMgr) {
        /* Store the render manager */
        theRenderMgr = pRenderMgr;
        theFormatter = pRenderMgr.getDataFormatter();

        /* Create the map */
        theMap = new HashMap<JDataField, JFieldElement<T>>();
    }

    /**
     * Add Element to field set.
     * @param pField the field id
     * @param pClass the class of the value
     * @param pComponent the component
     */
    public void addFieldElement(final JDataField pField,
                                final DataType pClass,
                                final JComponent pComponent) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pComponent);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param <I> ComboBox element type
     * @param pField the field id
     * @param pClass the class of the value
     * @param pComboBox the comboBox
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final Class<I> pClass,
                                    final JComboBox<I> pComboBox) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pComboBox);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param <I> Scroll button element type
     * @param pField the field id
     * @param pClass the class of the value
     * @param pButton the button
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final Class<I> pClass,
                                    final JScrollButton<I> pButton) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pButton);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param <I> List button element type
     * @param pField the field id
     * @param pButton the button
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final JScrollListButton<I> pButton) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pButton);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param <I> Icon button element type
     * @param pField the field id
     * @param pClass the class of the value
     * @param pButton the button
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final Class<I> pClass,
                                    final JIconButton<I> pButton) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pButton);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add field to panel.
     * @param pField the field to add
     * @param pPanel to add to
     */
    public void addFieldToPanel(final JDataField pField,
                                final JPanel pPanel) {
        /* Access element */
        JFieldElement<T> myEl = theMap.get(pField);
        /* If the field exists */
        if (myEl != null) {
            /* Add it to the panel */
            myEl.addToPanel(pPanel);
        }
    }

    /**
     * Set a field to be visible.
     * @param pField the field id
     * @param setVisible true/false
     */
    public void setVisibility(final JDataField pField,
                              final boolean setVisible) {
        /* Access element */
        JFieldElement<T> myEl = theMap.get(pField);

        /* If the field exists */
        if (myEl != null) {
            /* Set the visibility of the element */
            myEl.setVisibility(setVisible);
        }
    }

    /**
     * Set a field to be editable.
     * @param pField the field id
     * @param setEditable true/false
     */
    public void setEditable(final JDataField pField,
                            final boolean setEditable) {
        /* Access element */
        JFieldElement<T> myEl = theMap.get(pField);

        /* If the field exists */
        if (myEl != null) {
            /* Set the edit-ability of the element */
            myEl.setEditable(setEditable);
        }
    }

    /**
     * Set a set to be editable.
     * @param setEditable true/false
     */
    public void setEditable(final boolean setEditable) {
        /* Loop through all the map entries */
        for (JFieldElement<T> myEl : theMap.values()) {
            /* Set the edit-ability of the element */
            myEl.setEditable(setEditable);
        }
    }

    /**
     * Set the assumed currency for a field.
     * @param pField the field id
     * @param pCurrency the assumed currency
     */
    public void setAssumedCurrency(final JDataField pField,
                                   final Currency pCurrency) {
        /* Access element */
        JFieldElement<T> myEl = theMap.get(pField);

        /* If the field exists */
        if (myEl != null) {
            /* Set the assumed currency of the element */
            myEl.setAssumedCurrency(pCurrency);
        }
    }

    /**
     * Populate and render the fields.
     * @param pItem the item to render with
     */
    public void renderSet(final T pItem) {
        /* If the item is null */
        if (pItem == null) {
            /* Pass call to renderNullSet */
            renderNullSet();
            return;
        }

        /* Loop through all the map entries */
        for (JFieldElement<T> myEl : theMap.values()) {
            /* Determine the renderData */
            JFieldData myRender = theRenderMgr.determineRenderData(myEl, pItem);

            /* Render the element */
            myEl.renderData(myRender, pItem);
        }
    }

    /**
     * Populate and render the fields for a null set.
     */
    public void renderNullSet() {
        /* Loop through all the map entries */
        for (JFieldElement<T> myEl : theMap.values()) {
            /* Render the element */
            myEl.renderNullData();
        }
    }

    /**
     * Notify that a field update has occurred.
     * @param pField the source field
     * @param pNewValue the new Value
     */
    protected void notifyUpdate(final JDataField pField,
                                final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            FieldUpdate myUpdate = new FieldUpdate(pField, pNewValue);

            /* Fire the notification */
            fireActionEvent(ActionEvent.ACTION_PERFORMED, myUpdate);
        }
    }

    /**
     * Field Update Notification.
     */
    public static final class FieldUpdate {
        /**
         * The field.
         */
        private final JDataField theField;

        /**
         * The new value.
         */
        private final Object theValue;

        /**
         * Obtain the source field.
         * @return the field
         */
        public JDataField getField() {
            return theField;
        }

        /**
         * Obtain the value as specific type.
         * @param <I> the value class
         * @param pClass the required class
         * @return the value
         * @throws JOceanusException on error
         */
        public <I> I getValue(final Class<I> pClass) throws JOceanusException {
            try {
                return pClass.cast(theValue);
            } catch (ClassCastException e) {
                throw new JMetisDataException("Invalid dataType", e);
            }
        }

        /**
         * Obtain the value as String.
         * @return the value
         * @throws JOceanusException on error
         */
        public String getString() throws JOceanusException {
            return getValue(String.class);
        }

        /**
         * Obtain the value as Character Array.
         * @return the value
         * @throws JOceanusException on error
         */
        public char[] getCharArray() throws JOceanusException {
            return getValue(char[].class);
        }

        /**
         * Obtain the value as Short.
         * @return the value
         * @throws JOceanusException on error
         */
        public Short getShort() throws JOceanusException {
            return getValue(Short.class);
        }

        /**
         * Obtain the value as Integer.
         * @return the value
         * @throws JOceanusException on error
         */
        public Integer getInteger() throws JOceanusException {
            return getValue(Integer.class);
        }

        /**
         * Obtain the value as Long.
         * @return the value
         * @throws JOceanusException on error
         */
        public Long getLong() throws JOceanusException {
            return getValue(Long.class);
        }

        /**
         * Obtain the value as Boolean.
         * @return the value
         * @throws JOceanusException on error
         */
        public Boolean getBoolean() throws JOceanusException {
            return getValue(Boolean.class);
        }

        /**
         * Obtain the value as DateDay.
         * @return the value
         * @throws JOceanusException on error
         */
        public JDateDay getDateDay() throws JOceanusException {
            return getValue(JDateDay.class);
        }

        /**
         * Obtain the value as Money.
         * @return the value
         * @throws JOceanusException on error
         */
        public JMoney getMoney() throws JOceanusException {
            return getValue(JMoney.class);
        }

        /**
         * Obtain the value as Rate.
         * @return the value
         * @throws JOceanusException on error
         */
        public JRate getRate() throws JOceanusException {
            return getValue(JRate.class);
        }

        /**
         * Obtain the value as Price.
         * @return the value
         * @throws JOceanusException on error
         */
        public JPrice getPrice() throws JOceanusException {
            return getValue(JPrice.class);
        }

        /**
         * Obtain the value as Units.
         * @return the value
         * @throws JOceanusException on error
         */
        public JUnits getUnits() throws JOceanusException {
            return getValue(JUnits.class);
        }

        /**
         * Obtain the value as Dilution.
         * @return the value
         * @throws JOceanusException on error
         */
        public JDilution getDilution() throws JOceanusException {
            return getValue(JDilution.class);
        }

        /**
         * Obtain the value as ItemEvent.
         * @return the value
         * @throws JOceanusException on error
         */
        public ItemEvent getItemEvent() throws JOceanusException {
            return getValue(ItemEvent.class);
        }

        /**
         * Constructor.
         * @param pField the source field
         * @param pNewValue the new Value
         */
        protected FieldUpdate(final JDataField pField,
                              final Object pNewValue) {
            theField = pField;
            theValue = pNewValue;
        }
    }
}
