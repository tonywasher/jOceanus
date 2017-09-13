/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper class to facilitate reporting of exception.
 */
public class MetisExceptionWrapper
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisExceptionWrapper.class.getSimpleName());

    /**
     * Class Field Id.
     */
    public static final MetisField FIELD_CLASS = FIELD_DEFS.declareLocalField("ExceptionClass");

    /**
     * Message Field Id.
     */
    public static final MetisField FIELD_MESSAGE = FIELD_DEFS.declareLocalField("Message");

    /**
     * Origin Field Id.
     */
    public static final MetisField FIELD_ORIGIN = FIELD_DEFS.declareLocalField("Origin");

    /**
     * Cause Field Id.
     */
    public static final MetisField FIELD_CAUSE = FIELD_DEFS.declareLocalField("CausedBy");

    /**
     * Object Field Id.
     */
    public static final MetisField FIELD_OBJECT = FIELD_DEFS.declareLocalField("Object");

    /**
     * Stack Field Id.
     */
    public static final MetisField FIELD_STACK = FIELD_DEFS.declareLocalField("ProgramStack");

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
    public MetisExceptionWrapper(final Throwable e) {
        /* Store details */
        theWrapped = e;
        theClass = theWrapped.getClass().getSimpleName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
            if (!(theWrapped instanceof OceanusException)) {
                return MetisFieldValue.SKIP;
            }
            final OceanusException myWrapped = (OceanusException) theWrapped;
            final Object myObject = myWrapped.getObject();
            return (myObject == null)
                                      ? MetisFieldValue.SKIP
                                      : myObject;
        }
        if (FIELD_STACK.equals(pField)) {
            return theWrapped.getStackTrace();
        }

        /* Handle Origin separately */
        if (FIELD_ORIGIN.equals(pField)) {
            Throwable myResult = theWrapped;
            if (myResult.getCause() == null) {
                return MetisFieldValue.SKIP;
            }
            while (myResult.getCause() != null) {
                myResult = myResult.getCause();
            }
            return myResult;
        }

        /* Unknown field */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getMessage();
    }

    /**
     * Obtain message.
     * @return the message
     */
    public String getMessage() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theClass);
        myBuilder.append(": ");
        myBuilder.append(theWrapped.getMessage());
        return myBuilder.toString();
    }
}
