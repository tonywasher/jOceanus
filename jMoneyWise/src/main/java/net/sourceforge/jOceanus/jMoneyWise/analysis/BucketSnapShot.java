/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;

/**
 * History snapShot for a bucket.
 * @param <T> the values
 */
public class BucketSnapShot<T extends BucketValues<T, ?>>
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(BucketSnapShot.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Date Id.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Values Id.
     */
    private static final JDataField FIELD_VALUES = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataValues"));

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_VALUES.equals(pField)) {
            return theSnapShot;
        }
        return JDataFieldValue.UnknownField;
    }

    /**
     * The date of the event.
     */
    private final JDateDay theDate;

    /**
     * SnapShot Values.
     */
    private final T theSnapShot;

    /**
     * Obtain date.
     * @return the date
     */
    protected JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain snapShot.
     * @return the snapShot
     */
    protected T getSnapShot() {
        return theSnapShot;
    }

    /**
     * Constructor.
     * @param pEvent the event
     * @param pValues the values
     */
    protected BucketSnapShot(final Event pEvent,
                             final T pValues) {
        /* Store event date */
        theDate = pEvent.getDate();

        /* Store the snapshot map */
        theSnapShot = pValues.getSnapShot();
    }
}
