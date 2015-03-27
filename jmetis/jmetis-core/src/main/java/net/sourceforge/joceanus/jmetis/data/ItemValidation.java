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
package net.sourceforge.joceanus.jmetis.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;

/**
 * Provides the implementation of a error buffer for an object that implements JDataContents. Each element represents an error that relates to a field.
 */
public class ItemValidation
        implements JDataContents {
    /**
     * The local fields.
     */
    private JDataFields theLocalFields = null;

    /**
     * The first error in the list.
     */
    private final List<ErrorElement> theErrors;

    /**
     * Constructor.
     */
    public ItemValidation() {
        /* Store details */
        theErrors = new ArrayList<ErrorElement>();
        allocateNewFields();
    }

    /**
     * Allocate new DataFields.
     */
    private void allocateNewFields() {
        theLocalFields = new JDataFields(ItemValidation.class.getSimpleName());
    }

    @Override
    public JDataFields getDataFields() {
        return theLocalFields;
    }

    @Override
    public String formatObject() {
        int mySize = theErrors.size();
        if (mySize != 1) {
            return mySize
                   + " Errors";
        }
        ErrorElement myError = theErrors.get(0);
        return myError.formatError();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle out of range */
        int iIndex = pField.getIndex();
        if ((iIndex < 0)
            || iIndex >= theErrors.size()) {
            return JDataFieldValue.UNKNOWN;
        }

        /* Access the element */
        ErrorElement myError = theErrors.get(iIndex);
        return myError.getError();
    }

    /**
     * Get the first error in the list.
     * @return the first error or <code>null</code>
     */
    public ErrorElement getFirst() {
        return (theErrors.isEmpty())
                                    ? null
                                    : theErrors.get(0);
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

        /* Declare error field */
        theLocalFields.declareIndexField(pField.getName());

        /* Add to the end of the list */
        theErrors.add(myEl);
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
            if (myCurr.getField().equals(pField)) {
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
        StringBuilder myErrors = new StringBuilder();

        /* Loop through the elements */
        Iterator<ErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element */
            ErrorElement myCurr = myIterator.next();

            /* If the field matches */
            if (myCurr.getField().equals(pField)) {
                /* Add the error */
                addErrorText(myErrors, myCurr.getError());
            }
        }

        /* If we have errors */
        if (myErrors.length() > 0) {
            /* Complete the format and return it */
            endErrors(myErrors);
            return myErrors.toString();
        }

        /* Return null */
        return null;
    }

    /**
     * Get the error text for fields outside a set of fields.
     * @param aFields the set of fields
     * @return the error text
     */
    public String getFieldErrors(final JDataField[] aFields) {
        StringBuilder myErrors = new StringBuilder();

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
                if ((field != null) && field.equals(myField)) {
                    bFound = true;
                    break;
                }
            }

            /* Skip error if the field was found */
            if (bFound) {
                continue;
            }

            /* Add the error */
            addErrorText(myErrors, myCurr.getError());
        }

        /* If we have errors */
        if (myErrors.length() > 0) {
            /* Complete the format and return it */
            endErrors(myErrors);
            return myErrors.toString();
        }

        /* Return null */
        return null;
    }

    /**
     * Add error text.
     * @param pBuilder the string builder
     * @param pError new error text
     */
    private static void addErrorText(final StringBuilder pBuilder,
                                     final String pError) {
        /* Add relevant prefix */
        pBuilder.append((pBuilder.length() == 0)
                                                ? "<html>"
                                                : "<br>");

        /* Add error text */
        pBuilder.append(pError);
    }

    /**
     * End error text.
     * @param pErrors the error builder
     */
    public void endErrors(final StringBuilder pErrors) {
        pErrors.append("</html>");
    }

    /**
     * Clear errors.
     */
    public void clearErrors() {
        /* Remove all errors */
        theErrors.clear();
        allocateNewFields();
    }

    /**
     * represents an instance of an error for an object.
     */
    public static final class ErrorElement {
        /**
         * The text of the error.
         */
        private final String theError;

        /**
         * The field for the error.
         */
        private final JDataField theField;

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

        /**
         * Get the text for the error.
         * @return the text
         */
        public String getError() {
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
         * Format the error.
         * @return the formatted error
         */
        private String formatError() {
            return theField.getName()
                   + ": "
                   + theError;
        }
    }
}
