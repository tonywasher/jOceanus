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
package net.sourceforge.joceanus.jmetis.viewer;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Wrapper class to facilitate reporting of exception.
 */
public class JMetisExceptionWrapper
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(JMetisExceptionWrapper.class.getSimpleName());

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
        /* Handle fields */
        if (FIELD_CLASS.equals(pField)) {
            return theClass;
        }
        if (FIELD_MESSAGE.equals(pField)) {
            return theWrapped.getMessage();
        }
        if (FIELD_CAUSE.equals(pField)) {
            return theWrapped.getCause();
        }
        if (FIELD_OBJECT.equals(pField)) {
            if (!(theWrapped instanceof JOceanusException)) {
                return JDataFieldValue.SKIP;
            }
            JOceanusException myWrapped = (JOceanusException) theWrapped;
            Object myObject = myWrapped.getObject();
            return (myObject == null)
                    ? JDataFieldValue.SKIP
                    : myObject;
        }
        if (FIELD_STACK.equals(pField)) {
            return theWrapped.getStackTrace();
        }

        /* Handle Origin separately */
        if (FIELD_ORIGIN.equals(pField)) {
            Throwable myResult = theWrapped;
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
     * The wrapped exception.
     */
    private final Throwable theWrapped;

    /**
     * The wrapped class.
     */
    private final String theClass;

    /**
     * Create a new Metis Exception Wrapper for an underlying exception.
     * @param e the underlying exception
     */
    public JMetisExceptionWrapper(final Throwable e) {
        /* Store details */
        theWrapped = e;
        theClass = theWrapped.getClass().getSimpleName();
    }

    /**
     * Obtain message.
     * @return the message
     */
    public String getMessage() {
        return theWrapped.getMessage();
    }
}
