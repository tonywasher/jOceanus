/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

import java.util.Iterator;

/**
 * Validate InfoSet.
 * @param <T> the infoItem type
 */
public abstract class MoneyWiseValidateInfoSet<T extends PrometheusDataInfoItem> {
    /**
     * The infoSet.
     */
    private PrometheusDataInfoSet<T> theInfoSet;

    /**
     * The owner.
     */
    private PrometheusDataItem theOwner;

    /**
     * Validate the infoSet.
     * @param pInfoSet the infoSet
     */
    void validate(final PrometheusDataInfoSet<T> pInfoSet) {
        /* Store the item */
        theInfoSet = pInfoSet;
        theOwner = pInfoSet.getOwner();

        /* Loop through the classes */
        final Iterator<PrometheusDataInfoClass> myIterator = theInfoSet.classIterator();
        while (myIterator.hasNext()) {
            final PrometheusDataInfoClass myClass = myIterator.next();

            /* Access info for class */
            final T myInfo = theInfoSet.getInfo(myClass);

            /* If basic checks are passed */
            if (checkClass(myInfo, myClass)) {
                /* validate the class */
                validateClass(myInfo, myClass);
            }
        }
    }

    /**
     * Obtain the owner.
     * @return the owner
     */
    PrometheusDataItem getOwner() {
        return theOwner;
    }

    /**
     * Perform basic class validation.
     * @param pInfo the info (or null)
     * @param pClass the infoClass
     * @return continue checks true/false
     */
    private boolean checkClass(final T pInfo,
                               final PrometheusDataInfoClass pClass) {
        /* Check whether info exists */
        final boolean isExisting = pInfo != null
                && !pInfo.isDeleted();

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                theOwner.addError(PrometheusDataItem.ERROR_MISSING, theInfoSet.getFieldForClass(pClass));
            }
            return false;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            theOwner.addError(PrometheusDataItem.ERROR_EXIST, theInfoSet.getFieldForClass(pClass));
            return false;
        }

        /* Continue checks */
        return true;
    }

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    public abstract MetisFieldRequired isClassRequired(PrometheusDataInfoClass pClass);

    /**
     * Validate the class.
     * @param pInfo the info
     * @param pClass the infoClass
     */
    abstract void validateClass(T pInfo,
                                PrometheusDataInfoClass pClass);
}
