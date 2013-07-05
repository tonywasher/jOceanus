/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Event group type.
 * @param <T> the event type
 * @author Tony Washer
 */
public class EventGroup<T extends EventBase>
        extends OrderedIdList<Integer, T>
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventGroup.class.getName());

    /**
     * Split Indication.
     */
    protected static final String NAME_SPLIT = NLS_BUNDLE.getString("SplitIndication");

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventGroup.class.getSimpleName());

    /**
     * Parent field id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField("Parent");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_PARENT.equals(pField)) {
            return theParent;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName()
               + "("
               + size()
               + ")";
    }

    /**
     * Parent Event.
     */
    private final T theParent;

    /**
     * Obtain parent.
     * @return the parent
     */
    public T getParent() {
        return theParent;
    }

    /**
     * Constructor.
     * @param pParent the parent.
     * @param pClass the class
     */
    public EventGroup(final T pParent,
                      final Class<T> pClass) {
        /* Call super-constructor */
        super(pClass);

        /* Store parameter */
        theParent = pParent;

        /* Add the parent to the list */
        add(theParent);
    }

    /**
     * Register a child event.
     * @param pChild the child event
     */
    public void registerChild(final T pChild) {
        /* Add the child to the list */
        add(pChild);
    }

    /**
     * Does this group relate to the account?
     * @param pAccount the account
     * @return true/false
     */
    public boolean relatesTo(final Account pAccount) {
        /* Loop through the events */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* If the child relates to the account, say so */
            if (myEvent.relatesTo(pAccount)) {
                return true;
            }
        }

        /* Does not relate */
        return false;
    }
}
