/*******************************************************************************
 * jFieldSet: Java Swing Field Set
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jFieldSet;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jEventManager.JEventObject;
import net.sourceforge.jOceanus.jFieldSet.RenderManager.RenderData;

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
    private final transient RenderManager theRenderMgr;

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
    public JFieldSet(final RenderManager pRenderMgr) {
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
     * @param pLabel the label for the component
     * @param pComponent the component
     */
    public void addFieldElement(final JDataField pField,
                                final DataType pClass,
                                final JComponent pLabel,
                                final JComponent pComponent) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pLabel, pComponent);

        /* Add to the map */
        theMap.put(pField, myElement);
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
        /* Add field element with null label */
        addFieldElement(pField, pClass, null, pComponent);
    }

    /**
     * Add Element to field set.
     * @param <I> ComboBox element type
     * @param pField the field id
     * @param pClass the class of the value
     * @param pLabel the label for the component
     * @param pComboBox the comboBox
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final Class<I> pClass,
                                    final JComponent pLabel,
                                    final JComboBox<I> pComboBox) {
        /* Create the field */
        JFieldElement<T> myElement = new JFieldElement<T>(this, pField, pClass, pLabel, pComboBox);

        /* Add to the map */
        theMap.put(pField, myElement);
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
            RenderData myRender = theRenderMgr.determineRenderData(myEl, pItem);

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
         * @throws JDataException on error
         */
        public <I> I getValue(final Class<I> pClass) throws JDataException {
            try {
                return pClass.cast(theValue);
            } catch (ClassCastException e) {
                throw new JDataException(ExceptionClass.DATA, "Invalid dataType", e);
            }
        }

        /**
         * Obtain the value as String.
         * @return the value
         * @throws JDataException on error
         */
        public String getString() throws JDataException {
            return getValue(String.class);
        }

        /**
         * Obtain the value as Character Array.
         * @return the value
         * @throws JDataException on error
         */
        public char[] getCharArray() throws JDataException {
            return getValue(char[].class);
        }

        /**
         * Obtain the value as Short.
         * @return the value
         * @throws JDataException on error
         */
        public Short getShort() throws JDataException {
            return getValue(Short.class);
        }

        /**
         * Obtain the value as Integer.
         * @return the value
         * @throws JDataException on error
         */
        public Integer getInteger() throws JDataException {
            return getValue(Integer.class);
        }

        /**
         * Obtain the value as Long.
         * @return the value
         * @throws JDataException on error
         */
        public Long getLong() throws JDataException {
            return getValue(Long.class);
        }

        /**
         * Obtain the value as Boolean.
         * @return the value
         * @throws JDataException on error
         */
        public Boolean getBoolean() throws JDataException {
            return getValue(Boolean.class);
        }

        /**
         * Obtain the value as DateDay.
         * @return the value
         * @throws JDataException on error
         */
        public JDateDay getDateDay() throws JDataException {
            return getValue(JDateDay.class);
        }

        /**
         * Obtain the value as Money.
         * @return the value
         * @throws JDataException on error
         */
        public JMoney getMoney() throws JDataException {
            return getValue(JMoney.class);
        }

        /**
         * Obtain the value as Rate.
         * @return the value
         * @throws JDataException on error
         */
        public JRate getRate() throws JDataException {
            return getValue(JRate.class);
        }

        /**
         * Obtain the value as Price.
         * @return the value
         * @throws JDataException on error
         */
        public JPrice getPrice() throws JDataException {
            return getValue(JPrice.class);
        }

        /**
         * Obtain the value as Units.
         * @return the value
         * @throws JDataException on error
         */
        public JUnits getUnits() throws JDataException {
            return getValue(JUnits.class);
        }

        /**
         * Obtain the value as Dilution.
         * @return the value
         * @throws JDataException on error
         */
        public JDilution getDilution() throws JDataException {
            return getValue(JDilution.class);
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
