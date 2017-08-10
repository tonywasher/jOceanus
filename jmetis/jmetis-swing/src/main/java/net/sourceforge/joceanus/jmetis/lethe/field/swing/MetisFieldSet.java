/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.field.swing;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;

/**
 * Field Set. This handles a set of fields for an item, populating the fields rendering and parsing
 * the data.
 * @param <T> the Data Item type
 */
public class MetisFieldSet<T extends MetisFieldSetItem>
        extends MetisFieldSetBase {
    /**
     * The map of fields.
     */
    private final Map<MetisField, MetisFieldElement<T>> theMap;

    /**
     * The Render Manager.
     */
    private final MetisFieldManager theRenderMgr;

    /**
     * Constructor.
     * @param pRenderMgr the render manager
     */
    public MetisFieldSet(final MetisFieldManager pRenderMgr) {
        /* Call super constructor */
        super(pRenderMgr.getDataFormatter());

        /* Store the render manager */
        theRenderMgr = pRenderMgr;

        /* Create the map */
        theMap = new HashMap<>();
    }

    /**
     * Add textField Element to field set.
     * @param pField the field id
     * @param pClass the class of the value
     * @param pTextField the textField
     */
    public void addFieldElement(final MetisField pField,
                                final MetisDataType pClass,
                                final TethysSwingStringTextField pTextField) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pClass, pTextField);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add scrollPane Element to field set.
     * @param pField the field id
     * @param pClass the class of the value
     * @param pScrollPane the scrollPane
     */
    public void addFieldElement(final MetisField pField,
                                final MetisDataType pClass,
                                final TethysSwingScrollPaneManager pScrollPane) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pClass, pScrollPane);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param pField the field id
     * @param pButton the button
     */
    public void addFieldElement(final MetisField pField,
                                final TethysSwingDateButtonManager pButton) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pButton);

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
    public <I> void addFieldElement(final MetisField pField,
                                    final Class<I> pClass,
                                    final TethysSwingScrollButtonManager<I> pButton) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pClass, pButton);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add Element to field set.
     * @param <I> List button element type
     * @param pField the field id
     * @param pButton the button
     */
    public <I> void addFieldElement(final MetisField pField,
                                    final TethysSwingListButtonManager<I> pButton) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pButton);

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
    public <I> void addFieldElement(final MetisField pField,
                                    final Class<I> pClass,
                                    final TethysSwingIconButtonManager<I> pButton) {
        /* Create the field */
        final MetisFieldElement<T> myElement = new MetisFieldElement<>(this, pField, pClass, pButton);

        /* Add to the map */
        theMap.put(pField, myElement);
    }

    /**
     * Add field to panel.
     * @param pField the field to add
     * @param pPanel to add to
     */
    public void addFieldToPanel(final MetisField pField,
                                final JPanel pPanel) {
        /* Access element */
        final MetisFieldElement<T> myEl = theMap.get(pField);
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
    public void setVisibility(final MetisField pField,
                              final boolean setVisible) {
        /* Access element */
        final MetisFieldElement<T> myEl = theMap.get(pField);

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
    public void setEditable(final MetisField pField,
                            final boolean setEditable) {
        /* Access element */
        final MetisFieldElement<T> myEl = theMap.get(pField);

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
        for (MetisFieldElement<T> myEl : theMap.values()) {
            /* Set the edit-ability of the element */
            myEl.setEditable(setEditable);
        }
    }

    /**
     * Set the assumed currency for a field.
     * @param pField the field id
     * @param pCurrency the assumed currency
     */
    public void setAssumedCurrency(final MetisField pField,
                                   final Currency pCurrency) {
        /* Access element */
        final MetisFieldElement<T> myEl = theMap.get(pField);

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
        for (MetisFieldElement<T> myEl : theMap.values()) {
            /* Determine the renderData */
            final MetisFieldData myRender = theRenderMgr.determineRenderData(myEl, pItem);

            /* Render the element */
            myEl.renderData(myRender, pItem);
        }
    }

    /**
     * Populate and render the fields for a null set.
     */
    public void renderNullSet() {
        /* Loop through all the map entries */
        for (MetisFieldElement<T> myEl : theMap.values()) {
            /* Render the element */
            myEl.renderNullData();
        }
    }
}
