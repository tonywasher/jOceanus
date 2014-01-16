/*******************************************************************************
 * jDataManager: Java Data Manager
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
package net.sourceforge.joceanus.jdatamanager;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;

/**
 * Exception extension class. Provides capability of attaching ExceptionClass and Causing object to exception.
 * @author Tony Washer
 */
public class JDataException
        extends Exception
        implements JDataContents {
    /**
     * Required serialisation field.
     */
    private static final long serialVersionUID = 3100519617218144798L;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(JDataException.class.getSimpleName());

    /**
     * Class Field Id.
     */
    public static final JDataField FIELD_CLASS = FIELD_DEFS.declareLocalField("ExceptionClass");

    /**
     * Message Field Id.
     */
    public static final JDataField FIELD_MESSAGE = FIELD_DEFS.declareLocalField("Message");

    /**
     * Origin Field Id.
     */
    public static final JDataField FIELD_ORIGIN = FIELD_DEFS.declareLocalField("Origin");

    /**
     * Cause Field Id.
     */
    public static final JDataField FIELD_CAUSE = FIELD_DEFS.declareLocalField("CausedBy");

    /**
     * Object Field Id.
     */
    public static final JDataField FIELD_OBJECT = FIELD_DEFS.declareLocalField("Object");

    /**
     * Stack Field Id.
     */
    public static final JDataField FIELD_STACK = FIELD_DEFS.declareLocalField("ProgramStack");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If this is a wrapped exception */
        if (theClass == ExceptionClass.WRAPPED) {
            /* Access the wrapped Exception */
            Throwable myWrapped = getCause();
            if (FIELD_CLASS.equals(pField)) {
                return myWrapped.getClass().getName();
            }
            if (FIELD_MESSAGE.equals(pField)) {
                return myWrapped.getMessage();
            }
            if (FIELD_CAUSE.equals(pField)) {
                return myWrapped.getCause();
            }
            if (FIELD_OBJECT.equals(pField)) {
                return JDataFieldValue.SKIP;
            }
            if (FIELD_STACK.equals(pField)) {
                return myWrapped.getStackTrace();
            }

            /* Else this is a proper exception */
        } else {
            if (FIELD_CLASS.equals(pField)) {
                return theClass;
            }
            if (FIELD_MESSAGE.equals(pField)) {
                return getMessage();
            }
            if (FIELD_CAUSE.equals(pField)) {
                return getCause();
            }
            if (FIELD_OBJECT.equals(pField)) {
                return (theObject == null)
                        ? JDataFieldValue.SKIP
                        : theObject;
            }
            if (FIELD_STACK.equals(pField)) {
                return getStackTrace();
            }
        }

        /* Handle Origin separately */
        if (FIELD_ORIGIN.equals(pField)) {
            Throwable myResult = this;
            if (myResult.getCause() == null) {
                return JDataFieldValue.SKIP;
            }
            while (myResult.getCause() != null) {
                myResult = myResult.getCause();
            }
            return myResult;
        }

        /* Unknown field */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return "Exception("
               + theClass
               + ")";
    }

    /**
     * The class of this exception.
     */
    private final ExceptionClass theClass;

    /**
     * The associated object.
     */
    private final Object theObject;

    /**
     * Get the class of the exception.
     * @return the class
     */
    public ExceptionClass getExceptionClass() {
        return theClass;
    }

    /**
     * Get the associated object.
     * @return the associated object
     */
    public Object getObject() {
        return theObject;
    }

    /**
     * Create a wrapped Exception object based on a string and class.
     * @param c the underlying exception
     */
    protected JDataException(final Throwable c) {
        super(c);
        theClass = ExceptionClass.WRAPPED;
        theObject = null;
    }

    /**
     * Create a new Exception object based on a string and class.
     * @param ec the exception class
     * @param s the description of the exception
     */
    public JDataException(final ExceptionClass ec,
                          final String s) {
        super(s);
        theClass = ec;
        theObject = null;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string and a known exception type.
     * @param ec the exception class
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public JDataException(final ExceptionClass ec,
                          final String s,
                          final Throwable c) {
        super(s, c);
        theClass = ec;
        theObject = null;
    }

    /**
     * Create a new Exception object based on a string and an object.
     * @param ec the exception class
     * @param o the associated object
     * @param s the description of the exception
     */
    public JDataException(final ExceptionClass ec,
                          final Object o,
                          final String s) {
        super(s);
        theClass = ec;
        theObject = o;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string an object and a known exception type.
     * @param ec the exception class
     * @param o the associated object
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public JDataException(final ExceptionClass ec,
                          final Object o,
                          final String s,
                          final Throwable c) {
        super(s, c);
        theClass = ec;
        theObject = o;
    }

    /**
     * Enumeration of Exception classes.
     */
    public static enum ExceptionClass {
        /**
         * Wrapped Exception.
         */
        WRAPPED,

        /**
         * Exception from SQL server.
         */
        SQLSERVER,

        /**
         * Exception from Excel.
         */
        EXCEL,

        /**
         * Exception from Cryptographic library.
         */
        CRYPTO,

        /**
         * Exception from Data.
         */
        DATA,

        /**
         * Exception from Validation.
         */
        VALIDATE,

        /**
         * Exception from Preferences.
         */
        PREFERENCE,

        /**
         * Exception from Logic.
         */
        LOGIC,

        /**
         * Exception from XML.
         */
        XML,

        /**
         * Exception from SubVersion.
         */
        SUBVERSION,

        /**
         * Exception from Jira.
         */
        JIRA;
    }
}
