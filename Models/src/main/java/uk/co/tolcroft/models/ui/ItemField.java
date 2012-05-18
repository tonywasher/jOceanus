/*******************************************************************************
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
package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.JDataWalker.ReportFields.ReportField;
import uk.co.tolcroft.models.data.DataItem;

public class ItemField extends ValueField {
    /* Serial Id */
    private static final long serialVersionUID = -6749487415126864159L;

    /**
     * The field id corresponding to this field
     */
    private final ReportField theField;

    /**
     * Determine whether the field is fixed width or not
     */
    private final boolean isFixed;

    /**
     * Underlying component
     */
    private final JComponent theComponent;

    /**
     * Constructor
     * @param pClass the value class
     * @param iField the field number
     */
    public ItemField(ValueClass pClass, ReportField iField) {
        /* Call super constructor */
        super(pClass);

        /* Set underlying component to this */
        theComponent = this;

        /* Store the field id */
        theField = iField;

        /* Switch on the class */
        switch (pClass) {
            case Money:
            case Rate:
            case Price:
            case Units:
            case Dilution:
                /* Note that we are fixed width and right aligned */
                setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                isFixed = true;
                break;
            default:
                /* Note that we are variable width and left aligned */
                setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                isFixed = false;
                break;
        }
    }

    /**
     * Constructor for Component
     * @param pComponent the component
     * @param iField the field number
     */
    public ItemField(JComponent pComponent, ReportField iField) {
        /* Store the Component */
        theComponent = pComponent;

        /* Store the field id */
        theField = iField;

        /* Set variable width */
        isFixed = false;
    }

    /**
     * Constructor to add ItemField to list
     * @param pClass the value class
     * @param iField the field number
     * @param pSet the set to add to
     */
    public ItemField(ValueClass pClass, ReportField iField, FieldSet pSet) {
        /* Call standard constructor */
        this(pClass, iField);

        /* Add to the set */
        pSet.addItemField(this);
    }

    /**
     * Constructor to add Component to list
     * @param pComponent the component
     * @param iField the field number
     * @param pSet the set to add to
     */
    public ItemField(JComponent pComponent, ReportField iField, FieldSet pSet) {
        /* Call standard constructor */
        this(pComponent, iField);

        /* Add to the set */
        pSet.addItemField(this);
    }

    /**
     * Render the item
     * @param pItem the item to use for rendering
     */
    public void renderField(DataItem<?> pItem) {
        Font myFont;
        Color myBack;
        Color myFore;
        String myTip;

        /* Determine the standard colours */
        myFore = RenderData.getForeground(pItem, theField);
        myBack = RenderData.getBackground();

        /* Determine the Font and ToolTip */
        myFont = RenderData.getFont(pItem, theField, isFixed);
        myTip = RenderData.getToolTip(pItem, theField);

        theComponent.setForeground(myFore);
        if (!(theComponent instanceof JButton))
            theComponent.setBackground(myBack);
        theComponent.setToolTipText(myTip);
        theComponent.setFont(myFont);

        /* If we have an error */
        if (pItem.hasErrors(theField)) {
            /* If the component is an ItemField */
            if (theComponent instanceof ItemField) {
                ItemField myField = (ItemField) theComponent;

                /* If the value is null */
                if (myField.getValue() == null) {
                    /* Set the display text to Error */
                    myField.setDisplay(Renderer.theError);
                }
            }

            /* else if the component is a ComboBox */
            else if (theComponent instanceof JComboBox) {
                JComboBox myField = (JComboBox) theComponent;

                /* If no value is selected */
                if (myField.getSelectedIndex() == -1) {
                    /* Reverse background and foreground */
                    myField.setForeground(myBack);
                    myField.setBackground(myFore);
                }
            }
        }
    }

    /**
     * FieldSet class
     */
    public static class FieldSet {
        /**
         * List of ItemFields
         */
        private List<ItemField> theList = null;

        /**
         * Constructor
         */
        public FieldSet() {
            /* Create the list */
            theList = new ArrayList<ItemField>();
        }

        /**
         * Add ItemField to List
         * @param pField the field to add to list
         */
        public void addItemField(ItemField pField) {
            /* Add the field */
            theList.add(pField);
        }

        /**
         * Render the FieldSet
         * @param pItem the item to use for rendering
         */
        public void renderSet(DataItem<?> pItem) {
            /* List Iterator */
            Iterator<ItemField> myIterator = theList.iterator();
            ItemField myField;

            /* Loop through the fields */
            while (myIterator.hasNext()) {
                /* Access the field */
                myField = myIterator.next();

                /* Render it */
                myField.renderField(pItem);
            }
        }
    }
}
