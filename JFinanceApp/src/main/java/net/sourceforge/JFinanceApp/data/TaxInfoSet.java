/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataModels.data.DataInfoSet;
import net.sourceforge.JFinanceApp.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoClass;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoType;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoType.TaxYearInfoTypeList;

/**
 * TaxInfoSet class.
 * @author Tony Washer
 */
public class TaxInfoSet extends DataInfoSet<TaxYearInfo, TaxYearNew, TaxYearInfoType, TaxYearInfoClass> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(TaxInfoSet.class.getSimpleName(),
            DataInfoSet.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pInfoList the infoList for the info values
     * @param pTypeList the infoTypeList for the set
     */
    protected TaxInfoSet(final TaxYearNew pOwner,
                         final TaxInfoList pInfoList,
                         final TaxYearInfoTypeList pTypeList) {
        /* Store the Owner and Info List */
        super(pOwner, pInfoList, pTypeList);
    }

    /**
     * Constructor.
     * @param pSource source InfoSet
     */
    protected TaxInfoSet(final TaxInfoSet pSource) {
        super(pSource);
    }
}
