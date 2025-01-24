/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.viewer;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

/**
 * Wrapper class to facilitate reporting of exception.
 */
public class MetisViewerExceptionWrapper
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MetisViewerExceptionWrapper> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisViewerExceptionWrapper.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_CLASS, MetisViewerExceptionWrapper::getClassName);
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_MESSAGE, MetisViewerExceptionWrapper::getWrappedMessage);
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_ORIGIN, MetisViewerExceptionWrapper::getWrappedCause);
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_CAUSE, MetisViewerExceptionWrapper::getWrappedOrigin);
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_OBJECT, MetisViewerExceptionWrapper::getWrappedObject);
        FIELD_DEFS.declareLocalField(MetisViewerResource.VIEWER_ERROR_STACK, MetisViewerExceptionWrapper::getWrappedStack);
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
    public MetisViewerExceptionWrapper(final Throwable e) {
        /* Store details */
        theWrapped = e;
        theClass = theWrapped.getClass().getSimpleName();
    }

    @Override
    public MetisFieldSet<MetisViewerExceptionWrapper> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getMessage();
    }

    /**
     * Obtain message.
     * @return the message
     */
    public String getMessage() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theClass)
                .append(": ")
                .append(theWrapped.getMessage());
        return myBuilder.toString();
    }

    /**
     * Obtain the className.
     * @return the name
     */
    private String getClassName() {
        return theClass;
    }

    /**
     * Obtain the wrappedCause.
     * @return the cause
     */
    private Throwable getWrappedCause() {
        return theWrapped.getCause();
    }

    /**
     * Obtain the wrappedMessage.
     * @return the message
     */
    private String getWrappedMessage() {
        return theWrapped.getMessage();
    }

    /**
     * Obtain the wrappedMessage.
     * @return the message
     */
    private StackTraceElement[] getWrappedStack() {
        return theWrapped.getStackTrace();
    }

    /**
     * Obtain the wrappedObject.
     * @return the object
     */
    private Object getWrappedObject() {
        return theWrapped instanceof OceanusException
                                                      ? ((OceanusException) theWrapped).getObject()
                                                      : null;
    }

    /**
     * Obtain the wrappedOrigin.
     * @return the origin
     */
    private Throwable getWrappedOrigin() {
        Throwable myResult = theWrapped;
        if (myResult.getCause() == null) {
            return null;
        }
        while (myResult.getCause() != null) {
            myResult = myResult.getCause();
        }
        return myResult;
    }
}
