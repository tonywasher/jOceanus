/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.metis.field;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Records errors relating to an item.
 */
public class MetisFieldValidation
        implements MetisFieldItem {
    /*
     * FieldSet definitions.
     */
    static {
        MetisFieldSet.newFieldSet(MetisFieldValidation.class);
    }

    /**
     * The local fields.
     */
    private MetisFieldSet<MetisFieldValidation> theLocalFields;

    /**
     * The first error in the list.
     */
    private final List<MetisFieldError> theErrors;

    /**
     * Constructor.
     */
    public MetisFieldValidation() {
        /* Store details */
        theErrors = new ArrayList<>();
        allocateNewFields();
    }

    /**
     * Allocate new DataFields.
     */
    private void allocateNewFields() {
        theLocalFields = MetisFieldSet.newFieldSet(this);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theLocalFields;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        final int mySize = theErrors.size();
        if (mySize != 1) {
            return mySize
                   + " Errors";
        }
        final MetisFieldError myError = theErrors.get(0);
        return myError.formatError();
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
    public MetisFieldError getFirst() {
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
                         final MetisDataFieldId pField) {
        /* Create a new error element */
        final MetisFieldError myError = new MetisFieldError(pText, pField);

        /* Declare error field */
        final int myCount = countFieldErrors(pField);
        final String myName = pField.getId() + "-" + myCount;
        theLocalFields.declareLocalField(myName, f -> myError);

        /* Add to the end of the list */
        theErrors.add(myError);
    }

    /**
     * Determine whether there are any errors for a particular field.
     * @param pField - the field
     * @return <code>true</code> if there are any errors <code>false</code> otherwise
     */
    public boolean hasErrors(final MetisDataFieldId pField) {
        /* Loop through the elements */
        for (MetisFieldError myCurr : theErrors) {
            /* Access the element and return if related to required field */
            if (myCurr.getField().equals(pField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the error details for a particular field.
     * @param pField - the field
     * @return the error text
     */
    public String getFieldErrors(final MetisDataFieldId pField) {
        final StringBuilder myErrors = new StringBuilder();

        /* Loop through the elements */
        for (MetisFieldError myCurr : theErrors) {
            /* Access the element */
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
     * Count the errors for this field.
     * @param pField - the field number to check
     * @return the error count
     */
    private int countFieldErrors(final MetisDataFieldId pField) {
        int myCount = 0;

        /* Loop through the elements */
        for (MetisFieldError myCurr : theErrors) {
            /* Access the element */
            /* If the field matches */
            if (myCurr.getField().equals(pField)) {
                /* Increment the count */
                myCount++;
            }
        }

        /* Return the count */
        return myCount;
    }

    /**
     * Get the error text for fields outside a set of fields.
     * @param aFields the set of fields
     * @return the error text
     */
    public String getFieldErrors(final MetisFieldDef[] aFields) {
        final StringBuilder myErrors = new StringBuilder();

        /* Loop through the elements */
        for (MetisFieldError myCurr : theErrors) {
            /* Access the element and field */
            final MetisDataFieldId myField = myCurr.getField();

            /* Search the field set */
            boolean bFound = false;
            for (MetisFieldDef field : aFields) {
                /* If we have found the field note it and break loop */
                if (field != null && field.equals(myField)) {
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
    public static final class MetisFieldError {
        /**
         * The text of the error.
         */
        private final String theError;

        /**
         * The field for the error.
         */
        private final MetisDataFieldId theField;

        /**
         * Constructor for the error.
         * @param pError the error text
         * @param pField the field
         */
        private MetisFieldError(final String pError,
                                final MetisDataFieldId pField) {
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
        public MetisDataFieldId getField() {
            return theField;
        }

        /**
         * Format the error.
         * @return the formatted error
         */
        private String formatError() {
            return theField.getId()
                   + ": "
                   + theError;
        }
    }
}
