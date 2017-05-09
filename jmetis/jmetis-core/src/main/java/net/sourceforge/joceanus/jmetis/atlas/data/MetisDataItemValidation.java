/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;

/**
 * Provides the implementation of a error buffer for an object that implements MetisDataContents.
 * Each element represents an error that relates to a field.
 */
public class MetisDataItemValidation
        implements MetisDataFieldItem {
    /**
     * The local fields.
     */
    private MetisDataFieldSet theLocalFields;

    /**
     * The first error in the list.
     */
    private final List<MetisErrorElement> theErrors;

    /**
     * Constructor.
     */
    public MetisDataItemValidation() {
        /* Store details */
        theErrors = new ArrayList<>();
        allocateNewFields();
    }

    /**
     * Allocate new DataFields.
     */
    private void allocateNewFields() {
        theLocalFields = new MetisDataFieldSet(MetisDataItemValidation.class);
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return theLocalFields;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        int mySize = theErrors.size();
        if (mySize != 1) {
            return mySize
                   + " Errors";
        }
        MetisErrorElement myError = theErrors.get(0);
        return myError.formatError();
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle out of range */
        int iIndex = pField.getIndex();
        if ((iIndex < 0)
            || iIndex >= theErrors.size()) {
            return MetisDataFieldValue.UNKNOWN;
        }

        /* Access the element */
        MetisErrorElement myError = theErrors.get(iIndex);
        return myError.getError();
    }

    /**
     * Do we have any errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return !theErrors.isEmpty();
    }

    /**
     * Get the first error in the list.
     * @return the first error or <code>null</code>
     */
    public MetisErrorElement getFirst() {
        return theErrors.isEmpty()
                                   ? null
                                   : theErrors.get(0);
    }

    /**
     * add error to the list.
     * @param pText the text for the error
     * @param pField the field for the error
     */
    public void addError(final String pText,
                         final MetisDataField pField) {
        /* Create a new error element */
        MetisErrorElement myEl = new MetisErrorElement(pText, pField);

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
    public boolean hasErrors(final MetisDataField pField) {
        /* Loop through the elements */
        Iterator<MetisErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element and return if related to required field */
            MetisErrorElement myCurr = myIterator.next();
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
    public String getFieldErrors(final MetisDataField pField) {
        StringBuilder myErrors = new StringBuilder();

        /* Loop through the elements */
        Iterator<MetisErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element */
            MetisErrorElement myCurr = myIterator.next();

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
    public String getFieldErrors(final MetisDataField[] aFields) {
        StringBuilder myErrors = new StringBuilder();

        /* Loop through the elements */
        Iterator<MetisErrorElement> myIterator = theErrors.iterator();
        while (myIterator.hasNext()) {
            /* Access the element and field */
            MetisErrorElement myCurr = myIterator.next();
            MetisDataField myField = myCurr.getField();

            /* Search the field set */
            boolean bFound = false;
            for (MetisDataField field : aFields) {
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
    public static final class MetisErrorElement {
        /**
         * The text of the error.
         */
        private final String theError;

        /**
         * The field for the error.
         */
        private final MetisDataField theField;

        /**
         * Constructor for the error.
         * @param pError the error text
         * @param pField the field
         */
        private MetisErrorElement(final String pError,
                                  final MetisDataField pField) {
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
        public MetisDataField getField() {
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
