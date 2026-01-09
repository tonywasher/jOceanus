/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.core.field;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIValidatedField;
import net.sourceforge.joceanus.tethys.core.base.TethysUIResource;

/**
 * DataEditTextField base class.
 * @param <T> the data type
 */
public class TethysUIDataEditTextFieldControl<T>
        implements TethysUIValidatedField<T> {
    /**
     * The InvalidValue Error Text.
     */
    private static final String ERROR_BADPARSE = TethysUIResource.PARSE_BADVALUE.getValue();

    /**
     * The Field.
     */
    private final TethysUICoreDataEditField<T> theField;

    /**
     * The DataConverter.
     */
    private final TethysUICoreDataEditConverter<T> theConverter;

    /**
     * The validator.
     */
    private Function<T, String> theValidator;

    /**
     * The reporter.
     */
    private Consumer<String> theReporter;

    /**
     * The base value.
     */
    private T theValue;

    /**
     * The display text.
     */
    private String theDisplay;

    /**
     * The edit text.
     */
    private String theEdit;

    /**
     * Error text.
     */
    private String theErrorText;

    /**
     * Did we create a new value?
     */
    private boolean parsedNewValue;

    /**
     * Constructor.
     * @param pField the owing field
     * @param pConverter the data converter
     */
    public TethysUIDataEditTextFieldControl(final TethysUICoreDataEditField<T> pField,
                                            final TethysUICoreDataEditConverter<T> pConverter) {
        theField = pField;
        theConverter = pConverter;
        theValidator = p -> null;
    }

    /**
     * Obtain the display text.
     * @return the display text.
     */
    public String getDisplayText() {
        return theDisplay;
    }

    /**
     * Obtain the edit text.
     * @return the edit text.
     */
    public String getEditText() {
        if (theEdit == null) {
            theEdit = theConverter.formatEditValue(theValue);
        }
        return theEdit;
    }

    /**
     * Obtain the converter.
     * @return the converter.
     */
    public TethysUICoreDataEditConverter<T> getConverter() {
        return theConverter;
    }

    /**
     * Did we create a new value?
     * @return true/false
     */
    public boolean parsedNewValue() {
        return parsedNewValue;
    }

    /**
     * Clear the new value indication.
     */
    public void clearNewValue() {
        parsedNewValue = false;
        theErrorText = null;
    }

    @Override
    public void setValidator(final Function<T, String> pValidator) {
        theValidator = pValidator;
    }

    @Override
    public void setReporter(final Consumer<String> pReporter) {
        theReporter = pReporter;
    }

    /**
     * Obtain the error text.
     * @return the error
     */
    public String getErrorText() {
        return theErrorText;
    }

    /**
     * Report error.
     */
    private void reportError() {
        if (theReporter != null) {
            theReporter.accept(theErrorText);
        }
    }

    /**
     * Clear error Report.
     */
    public void clearErrorReport() {
        if (theReporter != null) {
            theReporter.accept(null);
        }
        theErrorText = null;
    }

    /**
     * Process value.
     * @param pNewValue the new value
     * @return is value valid?
     */
    public boolean processValue(final String pNewValue) {
        /* Clear flags */
        clearNewValue();

        /* NullOp if there are no changes */
        if (Objects.equals(pNewValue, theEdit)) {
            /* Return success */
            theField.fireEvent(TethysUIEvent.EDITFOCUSLOST, null);

            /* Clear any reporter error */
            clearErrorReport();
            return true;
        }

        /* Protect to catch parsing errors */
        try {
            /* Parse the value */
            final T myValue = pNewValue == null
                    ? null
                    : theConverter.parseEditedValue(pNewValue);

            /* Invoke the validator and reject value if necessary */
            theErrorText = theValidator.apply(myValue);
            if (theErrorText != null) {
                reportError();
                return false;
            }

            /* Clear any reporter error */
            clearErrorReport();

            /* set the value and fire Event */
            setValue(myValue);
            theField.fireEvent(TethysUIEvent.NEWVALUE, myValue);
            parsedNewValue = true;
            return true;

            /* Catch parsing error */
        } catch (IllegalArgumentException e) {
            theErrorText = ERROR_BADPARSE;
            reportError();
            return false;
        }
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    public void setValue(final T pValue) {
        /* Obtain display text */
        theDisplay = pValue == null
                ? null
                : theConverter.formatDisplayValue(pValue);

        /* Initialise edit text */
        theValue = pValue;
        theEdit = theConverter.formatEditValue(pValue);

        /* Store the value */
        theField.setTheValue(pValue);
    }
}

