/*******************************************************************************
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
package net.sourceforge.JDataWalker;

import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDataWalker.ReportObject.ReportDetail;

public class ModelException extends java.lang.Exception implements ReportDetail {
    /**
     * Required serialisation field
     */
    private static final long serialVersionUID = 3100519617218144798L;

    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(ModelException.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_CLASS = theFields.declareLocalField("ExceptionClass");
    public static final ReportField FIELD_MESSAGE = theFields.declareLocalField("Message");
    public static final ReportField FIELD_ORIGIN = theFields.declareLocalField("Origin");
    public static final ReportField FIELD_CAUSE = theFields.declareLocalField("CausedBy");
    public static final ReportField FIELD_OBJECT = theFields.declareLocalField("Object");
    public static final ReportField FIELD_STACK = theFields.declareLocalField("ProgramStack");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        /* If this is a wrapped exception */
        if (theClass == ExceptionClass.WRAPPED) {
            /* Access the wrapped Exception */
            Throwable myWrapped = getCause();
            if (pField == FIELD_CLASS)
                return myWrapped.getClass().getName();
            if (pField == FIELD_MESSAGE)
                return myWrapped.getMessage();
            if (pField == FIELD_CAUSE)
                return myWrapped.getCause();
            if (pField == FIELD_OBJECT)
                return ReportObject.skipField;
            if (pField == FIELD_STACK)
                return myWrapped.getStackTrace();
        }

        /* Else this is a proper exception */
        else {
            if (pField == FIELD_CLASS)
                return theClass;
            if (pField == FIELD_MESSAGE)
                return getMessage();
            if (pField == FIELD_CAUSE)
                return getCause();
            if (pField == FIELD_OBJECT)
                return (theObject == null) ? ReportObject.skipField : theObject;
            if (pField == FIELD_STACK)
                return getStackTrace();
        }

        /* Handle Origin separately */
        if (pField == FIELD_ORIGIN) {
            Throwable myResult = this;
            while (myResult.getCause() != null)
                myResult = myResult.getCause();
            return myResult;
        }
        return null;
    }

    @Override
    public String getObjectSummary() {
        return "Exception(" + theClass + ")";
    }

    /**
     * The class of this exception
     */
    private ExceptionClass theClass = null;

    /**
     * The associated object
     */
    private ReportDetail theObject = null;

    /**
     * Get the class of the exception
     * @return the class
     */
    public ExceptionClass getExceptionClass() {
        return theClass;
    }

    /**
     * Get the associated object
     * @return the associated object
     */
    public Object getObject() {
        return theObject;
    }

    /**
     * Create a wrapped Exception object based on a string and class
     * @param c the underlying exception
     */
    protected ModelException(Throwable c) {
        super(c);
        theClass = ExceptionClass.WRAPPED;
    }

    /**
     * Create a new Exception object based on a string and class
     * @param ec the exception class
     * @param s the description of the exception
     */
    public ModelException(ExceptionClass ec, String s) {
        super(s);
        theClass = ec;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string and a known exception type
     * @param ec the exception class
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public ModelException(ExceptionClass ec, String s, Throwable c) {
        super(s, c);
        theClass = ec;
    }

    /**
     * Create a new Exception object based on a string and an object
     * @param ec the exception class
     * @param o the associated object
     * @param s the description of the exception
     */
    public ModelException(ExceptionClass ec, ReportDetail o, String s) {
        super(s);
        theClass = ec;
        theObject = o;
        fillInStackTrace();
    }

    /**
     * Create a new Exception object based on a string an object and a known exception type
     * @param ec the exception class
     * @param o the associated object
     * @param s the description of the exception
     * @param c the underlying exception
     */
    public ModelException(ExceptionClass ec, ReportDetail o, String s, Throwable c) {
        super(s, c);
        theClass = ec;
        theObject = o;
    }

    /**
     * Enumeration of Exception classes
     */
    public static enum ExceptionClass {
        /**
         * Wrapped Exception
         */
        WRAPPED,

        /**
         * Exception from SQL server
         */
        SQLSERVER,

        /**
         * Exception from Excel
         */
        EXCEL,

        /**
         * Exception from Cryptographic library
         */
        CRYPTO,

        /**
         * Exception from Data
         */
        DATA,

        /**
         * Exception from Validation
         */
        VALIDATE,

        /**
         * Exception from Preferences
         */
        PREFERENCE,

        /**
         * Exception from Logic
         */
        LOGIC,

        /**
         * Exception from SubVersion
         */
        SUBVERSION,

        /**
         * Exception from Jira
         */
        JIRA;
    }
}
