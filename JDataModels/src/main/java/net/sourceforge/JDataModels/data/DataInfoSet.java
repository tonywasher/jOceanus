/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataInfo.DataInfoList;
import net.sourceforge.JDataModels.data.DataList.ListStyle;
import net.sourceforge.JDataModels.data.StaticData.StaticInterface;
import net.sourceforge.JDataModels.data.StaticData.StaticList;

/**
 * Representation of an information set extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <E> the Info type class
 */
public abstract class DataInfoSet<T extends DataInfo<T, O, I>, O extends DataItem, I extends StaticData<I, E>, E extends Enum<E> & StaticInterface>
        implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataInfoSet.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Version Field Id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(ValueSet.FIELD_VERSION);

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareLocalField("Owner");

    /**
     * Values Field Id.
     */
    public static final JDataField FIELD_VALUES = FIELD_DEFS.declareLocalField("Values");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }
        if (FIELD_OWNER.equals(pField)) {
            return theOwner;
        }
        if (FIELD_VALUES.equals(pField)) {
            return theMap;
        }
        return JDataFieldValue.UnknownField;
    }

    /**
     * Version # of the values.
     */
    private int theVersion;

    /**
     * The Owner to which this set belongs.
     */
    private final O theOwner;

    /**
     * The Class for the InfoSet.
     */
    private final Class<E> theClass;

    /**
     * The InfoTypes for the InfoSet.
     */
    private final StaticList<I, E> theTypeList;

    /**
     * The DataInfoList for the InfoSet.
     */
    private final DataInfoList<T, O, I> theInfoList;

    /**
     * The Map of the DataInfo.
     */
    private final Map<E, T> theMap;

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pInfoList the infoList for the info values
     * @param pTypeList the infoTypeList for the set
     */
    protected DataInfoSet(final O pOwner,
                          final DataInfoList<T, O, I> pInfoList,
                          final StaticList<I, E> pTypeList) {
        /* Store the Owner and Info List */
        theOwner = pOwner;
        theInfoList = pInfoList;
        theTypeList = pTypeList;
        theClass = theTypeList.getEnumClass();

        /* Create the Map */
        theMap = new EnumMap<E, T>(theClass);
    }

    /**
     * Constructor.
     * @param pSource the InfoSet to clone
     */
    protected DataInfoSet(final DataInfoSet<T, O, I, E> pSource) {
        /* Store the Owner and Info List */
        theOwner = pSource.theOwner;
        theInfoList = pSource.theInfoList.getEmptyList();
        theTypeList = pSource.theTypeList;
        theClass = pSource.theClass;

        /* Mark the InfoList as EDIT */
        theInfoList.setStyle(ListStyle.EDIT);

        /* Create the Map */
        theMap = new EnumMap<E, T>(theClass);

        /* Clone the InfoSet for each Event in the underlying Map */
        for (Entry<E, T> myEntry : pSource.theMap.entrySet()) {
            /* Create the new value */
            T myNew = theInfoList.addNewItem(myEntry.getValue());

            /* Add to the map */
            theMap.put(myEntry.getKey(), myNew);
        }
    }

    /**
     * Obtain the value for the infoClass.
     * @param <X> the infoClass
     * @param pInfoClass the Info Class
     * @param pClass the Value Class
     * @return the value
     */
    protected <X> X getValue(final E pInfoClass,
                             final Class<X> pClass) {
        /* Access existing entry */
        T myValue = theMap.get(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the value */
        return myValue.getValue(pClass);
    }

    /**
     * Set the value for the infoClass.
     * @param pInfoClass the Info Class
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setValue(final E pInfoClass,
                            final Object pValue) throws JDataException {
        /* Determine whether this is a deletion */
        boolean bDelete = (pValue == null);

        /* Obtain the Map value */
        T myValue = theMap.get(pInfoClass);

        /* If we are deleting */
        if (bDelete) {
            /* Mark existing value as deleted */
            if (myValue != null) {
                myValue.markDeleted();
            }

            /* Return to caller */
            return;
        }

        /* If we have no entry */
        if (myValue == null) {
            /* Obtain infoType */
            I myInfoType = theTypeList.findItemByClass(pInfoClass);

            /* Create the entry and add to list */
            myValue = theInfoList.addNewItem(theOwner, myInfoType);

            /* Insert the value into the map */
            theMap.put(pInfoClass, myValue);
        }

        /* Update the value */
        myValue.setValue(pValue);
    }

    /**
     * Set version.
     * @param pVersion the version to set
     */
    public void setVersion(final int pVersion) {
        theInfoList.setVersion(pVersion);
    }

    /**
     * Determine whether the set has changes.
     * @return <code>true/false</code>
     */
    public boolean hasHistory() {
        boolean bChanges = false;

        /* Push history for each existing value */
        for (T myValue : theMap.values()) {
            /* Create the new value */
            bChanges |= myValue.hasHistory();
        }

        /* return result */
        return bChanges;
    }

    /**
     * Push history.
     */
    public void pushHistory() {
        /* Push history for each existing value */
        for (T myValue : theMap.values()) {
            /* Create the new value */
            myValue.pushHistory();
        }
    }

    /**
     * Pop history.
     */
    public void popHistory() {
        /* Push history for each existing value */
        for (T myValue : theMap.values()) {
            /* Create the new value */
            myValue.popHistory();
        }
    }

    /**
     * Check for history.
     * @return <code>true</code> if changes were made, <code>false</code> otherwise
     */
    public boolean checkForHistory() {
        boolean bChanges = false;

        /* Push history for each existing value */
        for (T myValue : theMap.values()) {
            /* Create the new value */
            bChanges |= myValue.checkForHistory();
        }

        /* return result */
        return bChanges;
    }
}
