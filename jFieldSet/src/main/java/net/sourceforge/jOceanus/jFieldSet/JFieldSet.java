package net.sourceforge.jOceanus.jFieldSet;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
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
public class JFieldSet<T extends JFieldItem>
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
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
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
     * @throws JDataException on error
     */
    public void addFieldElement(final JDataField pField,
                                final DataType pClass,
                                final JComponent pLabel,
                                final JComponent pComponent) throws JDataException {
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
     * @throws JDataException on error
     */
    public void addFieldElement(final JDataField pField,
                                final DataType pClass,
                                final JComponent pComponent) throws JDataException {
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
     * @throws JDataException on error
     */
    public <I> void addFieldElement(final JDataField pField,
                                    final Class<I> pClass,
                                    final JComponent pLabel,
                                    final JComboBox<I> pComboBox) throws JDataException {
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
     * Notify that a field update has occurred
     * @param pField the source field
     * @param pNewValue the new Value
     */
    protected void notifyUpdate(final JDataField pField,
                                final Object pNewValue) {
        /* Create the notification */
        FieldUpdate myUpdate = new FieldUpdate(pField, pNewValue);

        /* Fire the notification */
        fireActionEvent(ActionEvent.ACTION_PERFORMED, myUpdate);
    }

    /**
     * Field Update Notification.
     */
    protected static final class FieldUpdate {
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
         */
        public <I> I getValue(final Class<I> pClass) {
            return pClass.cast(theValue);
        }

        /**
         * Obtain the value as String.
         * @return the value
         */
        public String getString() {
            return getValue(String.class);
        }

        /**
         * Obtain the value as Character Array.
         * @return the value
         */
        public char[] getCharArray() {
            return getValue(char[].class);
        }

        /**
         * Obtain the value as Short.
         * @return the value
         */
        public Short getShort() {
            return getValue(Short.class);
        }

        /**
         * Obtain the value as Integer.
         * @return the value
         */
        public Integer getInteger() {
            return getValue(Integer.class);
        }

        /**
         * Obtain the value as Long.
         * @return the value
         */
        public Long getLong() {
            return getValue(Long.class);
        }

        /**
         * Obtain the value as Boolean.
         * @return the value
         */
        public Boolean getBoolean() {
            return getValue(Boolean.class);
        }

        /**
         * Obtain the value as DateDay.
         * @return the value
         */
        public JDateDay getDateDay() {
            return getValue(JDateDay.class);
        }

        /**
         * Obtain the value as Money.
         * @return the value
         */
        public JMoney getMoney() {
            return getValue(JMoney.class);
        }

        /**
         * Obtain the value as Rate.
         * @return the value
         */
        public JRate getRate() {
            return getValue(JRate.class);
        }

        /**
         * Obtain the value as Price.
         * @return the value
         */
        public JPrice getPrice() {
            return getValue(JPrice.class);
        }

        /**
         * Obtain the value as Units.
         * @return the value
         */
        public JUnits getUnits() {
            return getValue(JUnits.class);
        }

        /**
         * Obtain the value as Dilution.
         * @return the value
         */
        public JDilution getDilution() {
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
