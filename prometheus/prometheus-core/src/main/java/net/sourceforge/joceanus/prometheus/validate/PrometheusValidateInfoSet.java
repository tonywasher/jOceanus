/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validate InfoSet.
 *
 * @param <T> the infoItem type
 */
public abstract class PrometheusValidateInfoSet<T extends PrometheusDataInfoItem> {
    /**
     * The editSet.
     */
    private PrometheusEditSet theEditSet;

    /**
     * The infoSet.
     */
    private PrometheusDataInfoSet<T> theInfoSet;

    /**
     * The owner.
     */
    private PrometheusDataItem theOwner;

    /**
     * Constructor.
     */
    protected PrometheusValidateInfoSet() {
    }

    /**
     * Obtain the owner.
     *
     * @return the owner
     */
    public PrometheusEditSet getEditSet() {
        return theEditSet;
    }

    /**
     * Obtain the infoSet.
     *
     * @return the infoSet
     */
    public PrometheusDataInfoSet<T> getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain the owner.
     *
     * @return the owner
     */
    public PrometheusDataItem getOwner() {
        return theOwner;
    }

    /**
     * Store EditSet details.
     *
     * @param pEditSet the editSet
     */
    public void storeEditSet(final PrometheusEditSet pEditSet) {
        theEditSet = pEditSet;
    }

    /**
     * Store InfoSet details.
     *
     * @param pInfoSet the infoSet
     */
    public void storeInfoSet(final PrometheusDataInfoSet<T> pInfoSet) {
        /* Store the infoSet details */
        theInfoSet = pInfoSet;
        theOwner = pInfoSet.getOwner();
    }

    /**
     * Validate the infoSet.
     *
     * @param pInfoSet the infoSet
     */
    public void validate(final PrometheusDataInfoSet<T> pInfoSet) {
        /* Store the infoSet */
        storeInfoSet(pInfoSet);

        /* Loop through the classes */
        final Iterator<PrometheusDataInfoClass> myIterator = theInfoSet.classIterator();
        while (myIterator.hasNext()) {
            final PrometheusDataInfoClass myClass = myIterator.next();

            /* Access info for class */
            final T myInfo = myClass.isLinkSet()
                    ? null
                    : theInfoSet.getInfo(myClass);

            /* If basic checks are passed */
            if (checkClass(myInfo, myClass)) {
                /* validate the class */
                validateClass(myInfo, myClass);
            }
        }
    }

    /**
     * Perform basic class validation.
     *
     * @param pInfo  the info (or null)
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
     *
     * @param pClass the infoSet class
     * @return the status
     */
    public abstract MetisFieldRequired isClassRequired(PrometheusDataInfoClass pClass);

    /**
     * Validate the class.
     *
     * @param pInfo  the info
     * @param pClass the infoClass
     */
    public abstract void validateClass(T pInfo,
                                       PrometheusDataInfoClass pClass);

    /**
     * autoCorrect values after change.
     *
     * @param pInfoSet the infoSet
     * @throws OceanusException on error
     */
    public void autoCorrect(final PrometheusDataInfoSet<T> pInfoSet) throws OceanusException {
        /* Store the infoSet */
        storeInfoSet(pInfoSet);

        /* Loop through the classes */
        final Iterator<PrometheusDataInfoClass> myIterator = theInfoSet.classIterator();
        while (myIterator.hasNext()) {
            final PrometheusDataInfoClass myClass = myIterator.next();

            /* Access value and requirement */
            final MetisFieldRequired myState = isClassRequired(myClass);
            final boolean isExisting = pInfoSet.isExisting(myClass);

            /* Switch on required state */
            switch (myState) {
                case MUSTEXIST:
                    if (isExisting) {
                        autoCorrect(myClass);
                    } else {
                        setDefault(myClass);
                    }
                    break;
                case NOTALLOWED:
                    if (isExisting) {
                        theInfoSet.setValue(myClass, null);
                    }
                    break;
                case CANEXIST:
                default:
                    if (isExisting) {
                        autoCorrect(myClass);
                    }
                    break;
            }
        }
    }

    /**
     * Set default value for a class.
     *
     * @param pClass the class to autoCorrect
     * @throws OceanusException on error
     */
    protected void setDefault(final PrometheusDataInfoClass pClass) throws OceanusException {
        throw new IllegalArgumentException();
    }

    /**
     * AutoCorrect an existing value (if necessary).
     *
     * @param pClass the class to autoCorrect
     * @throws OceanusException on error
     */
    protected void autoCorrect(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* NoOp */
    }
}
