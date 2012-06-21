/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataElement;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;

/**
 * Provides the implementation of a error buffer for a DataItem. Each element represents an error that relates
 * to a field.
 */
public class ItemValidation implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ItemValidation.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Errors Field Id.
     */
    private static final JDataField FIELD_ERRORS = FIELD_DEFS.declareLocalField("Errors");

    @Override
    public String formatObject() {
        return theErrors.size() + "Errors";
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ERRORS.equals(pField)) {
            return theErrors.iterator();
        }
        return JDataFieldValue.UnknownField;
    }

    /**
     * The list to which this Validation Control belongs.
     */
    private final DataList<?, ?> theList;

    /**
     * The first error in the list.
     */
    private final List<ErrorElement> theErrors;

    /**
     * Constructor.
     * @param pItem the item to which this validation control belongs
     */
    public ItemValidation(final DataItem pItem) {
        /* Store details */
        theList = pItem.getList();
        theErrors = new ArrayList<ErrorElement>();
    }

    /**
     * Get the first error in the list.
     * @return the first error or <code>null</code>
     */
    public ErrorElement getFirst() {
        return (theErrors.size() > 0) ? theErrors.get(0) : null;
    }

    /**
     * add error to the list.
     * @param pText the text for the error
     * @param pField the field for the error
     */
    public void addError(final String pText,
                         final JDataField pField) {
        /* Create a new error element */
        ErrorElement myEl = new ErrorElement(pText, pField);

        /* Add to the end of the list */
        theErrors.add(myEl);

        /* Note that the list has errors */
        theList.setEditState(EditState.ERROR);
    }

    /**
     * Determine whether there are any errors for a particular field.
     * @param pField - the field number to check
     * @return <code>true</code> if there are any errors <code>false</code> otherwise
     */
    public boolean hasErrors(final JDataField pField) {
        /* Loop through the elements */
        Iterator<ErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element and return if related to required field */
            ErrorElement myCurr = myIterator.next();
            if (myCurr.getField() == pField) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the error details for a particular field.
     * @param pField - the field number to check
     * @return the error text
     */
    public String getFieldErrors(final JDataField pField) {
        String myErrors = null;

        /* Loop through the elements */
        Iterator<ErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element */
            ErrorElement myCurr = myIterator.next();

            /* If the field matches */
            if (myCurr.getField() == pField) {
                /* Add the error */
                myErrors = addErrorText(myErrors, myCurr.getError());
            }
        }

        /* Return the details */
        return (myErrors == null) ? null : myErrors + "</html>";
    }

    /**
     * Get the error text for fields outside a set of fields.
     * @param aFields the set of fields
     * @return the error text
     */
    public String getFieldErrors(final JDataField[] aFields) {
        String myErrors = null;

        /* Loop through the elements */
        Iterator<ErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element and field */
            ErrorElement myCurr = myIterator.next();
            JDataField myField = myCurr.getField();

            /* Search the field set */
            boolean bFound = false;
            for (JDataField field : aFields) {
                /* If we have found the field note it and break loop */
                if (field == myField) {
                    bFound = true;
                    break;
                }
            }

            /* Skip error if the field was found */
            if (bFound) {
                continue;
            }

            /* Add the error */
            myErrors = addErrorText(myErrors, myCurr.getError());
        }

        /* Return errors */
        return (myErrors == null) ? null : myErrors + "</html>";
    }

    /**
     * Add error text.
     * @param pCurrent existing error text
     * @param pError new error text
     * @return the text
     */
    private static String addErrorText(final String pCurrent,
                                       final String pError) {
        /* Return text if current is null */
        if (pCurrent == null) {
            return "<html>" + pError;
        }

        /* return with error appended */
        return pCurrent + "<br>" + pError;
    }

    /**
     * Clear errors.
     */
    public void clearErrors() {
        /* Remove all errors */
        theErrors.clear();
    }

    /**
     * represents an instance of an error for an object.
     */
    public static final class ErrorElement implements JDataElement {
        /**
         * The text of the error.
         */
        private final String theError;

        /**
         * The field for the error.
         */
        private final JDataField theField;

        /**
         * Get the text for the error.
         * @return the text
         */
        public String getError() {
            return theError;
        }

        @Override
        public String getElementName() {
            return theField.getName();
        }

        @Override
        public Object getElementValue() {
            return theError;
        }

        /**
         * Get the field for the error.
         * @return the field
         */
        public JDataField getField() {
            return theField;
        }

        /**
         * Constructor for the error.
         * @param pError the error text
         * @param pField the field
         */
        private ErrorElement(final String pError,
                             final JDataField pField) {
            theError = pError;
            theField = pField;
        }
    }
}
