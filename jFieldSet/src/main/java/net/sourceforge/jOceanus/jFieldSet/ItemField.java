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

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jFieldSet.Renderer.RendererFieldValue;

/**
 * Extension of ValueField to handle a DataItem field.
 * @author Tony Washer
 */
public class ItemField
        extends ValueField {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -6749487415126864159L;

    /**
     * The field id corresponding to this field.
     */
    private final transient JDataField theField;

    /**
     * Determine whether the field is fixed width or not.
     */
    private final boolean isFixed;

    /**
     * Underlying component.
     */
    private final JComponent theComponent;

    /**
     * Constructor.
     * @param pClass the value class
     * @param pField the field
     */
    private ItemField(final ValueClass pClass,
                      final JDataField pField) {
        /* Call super constructor */
        super(pClass);

        /* Set underlying component to this */
        theComponent = this;

        /* Store the field id */
        theField = pField;

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
     * Constructor for Component.
     * @param pComponent the component
     * @param pField the field
     */
    private ItemField(final JComponent pComponent,
                      final JDataField pField) {
        /* Store the Component */
        theComponent = pComponent;

        /* Store the field id */
        theField = pField;

        /* Set variable width */
        isFixed = false;
    }

    /**
     * Constructor to add ItemField to list.
     * @param pClass the value class
     * @param pField the field
     * @param pSet the set to add to
     */
    public ItemField(final ValueClass pClass,
                     final JDataField pField,
                     final FieldSet pSet) {
        /* Call standard constructor */
        this(pClass, pField);

        /* Add to the set */
        pSet.addItemField(this);
    }

    /**
     * Constructor to add Component to list.
     * @param pComponent the component
     * @param pField the field
     * @param pSet the set to add to
     */
    public ItemField(final JComponent pComponent,
                     final JDataField pField,
                     final FieldSet pSet) {
        /* Call standard constructor */
        this(pComponent, pField);

        /* Add to the set */
        pSet.addItemField(this);
    }

    /**
     * Render the item.
     * @param pRenderMgr the render manager
     * @param pItem the item to use for rendering
     */
    private void renderField(final RenderManager pRenderMgr,
                             final JFieldSetItem pItem) {
        /* Obtain the state */
        RenderState myState = pItem.getRenderState(theField);

        /* Determine the standard colours */
        Color myFore = pRenderMgr.getForeground(myState);
        Color myBack = pRenderMgr.getStandardBackground();

        /* Determine the Font and ToolTip */
        Font myFont = pRenderMgr.determineFont(myState, isFixed);
        String myTip = pRenderMgr.getToolTip(myState, pItem, theField);

        theComponent.setForeground(myFore);
        if (!(theComponent instanceof JButton)) {
            theComponent.setBackground(myBack);
        }
        theComponent.setToolTipText(myTip);
        theComponent.setFont(myFont);

        /* If we have an error */
        if (myState.isError()) {
            /* If the component is an ItemField */
            if (theComponent instanceof ItemField) {
                ItemField myField = (ItemField) theComponent;

                /* If the value is null */
                if (myField.getValue() == null) {
                    /* Set the display text to Error */
                    myField.setDisplay(RendererFieldValue.Error);
                }

                /* else if the component is a ComboBox */
            } else if (theComponent instanceof JComboBox) {
                JComboBox<?> myField = (JComboBox<?>) theComponent;

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
     * FieldSet class.
     */
    public static class FieldSet {
        /**
         * The Render Manager.
         */
        private final transient RenderManager theRenderMgr;

        /**
         * List of ItemFields.
         */
        private final List<ItemField> theList;

        /**
         * Constructor.
         * @param pRenderMgr the render manager
         */
        public FieldSet(final RenderManager pRenderMgr) {
            /* Store the render manager */
            theRenderMgr = pRenderMgr;

            /* Create the list */
            theList = new ArrayList<ItemField>();
        }

        /**
         * Add ItemField to List.
         * @param pField the field to add to list
         */
        public void addItemField(final ItemField pField) {
            /* Add the field */
            theList.add(pField);
        }

        /**
         * Add component to List.
         * @param pComponent the component
         * @param pField the field
         */
        public void addItemField(final JComponent pComponent,
                                 final JDataField pField) {
            /* Add the field */
            theList.add(new ItemField(pComponent, pField));
        }

        /**
         * Render the FieldSet.
         * @param pItem the item to use for rendering
         */
        public void renderSet(final JFieldSetItem pItem) {
            /* List Iterator */
            Iterator<ItemField> myIterator = theList.iterator();
            ItemField myField;

            /* Loop through the fields */
            while (myIterator.hasNext()) {
                /* Access the field */
                myField = myIterator.next();

                /* Render it */
                myField.renderField(theRenderMgr, pItem);
            }
        }
    }
}
