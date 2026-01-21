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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;

/**
 * Cell implementation.
 */
public class PrometheusOdfCell
        extends PrometheusSheetCell {
    /**
     * The cell storage.
     */
    private final PrometheusOdfCellStore theStore;

    /**
     * Constructor .
     *
     * @param pStore    the cell storage
     * @param pRow      the row
     * @param pIndex    the index
     * @param pReadOnly is the cell readOnly?
     */
    PrometheusOdfCell(final PrometheusOdfCellStore pStore,
                      final PrometheusOdfRow pRow,
                      final int pIndex,
                      final boolean pReadOnly) {
        /* Store parameters */
        super(pRow, pIndex, pReadOnly);
        theStore = pStore;
    }

    @Override
    public PrometheusOdfSheet getSheet() {
        return (PrometheusOdfSheet) super.getSheet();
    }

    @Override
    public Boolean getBoolean() {
        return theStore.getBooleanValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusDate getDate() {
        return theStore.getDateValueAtIndex(getCellIndex());
    }

    @Override
    public Integer getInteger() throws OceanusException {
        return theStore.getIntegerValueAtIndex(getCellIndex());
    }

    @Override
    public Long getLong() throws OceanusException {
        return theStore.getLongValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusMoney getMoney() throws OceanusException {
        return theStore.getMoneyValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusPrice getPrice() throws OceanusException {
        return theStore.getPriceValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusUnits getUnits() throws OceanusException {
        return theStore.getUnitsValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusRate getRate() throws OceanusException {
        return theStore.getRateValueAtIndex(getCellIndex());
    }

    @Override
    public OceanusRatio getRatio() throws OceanusException {
        return theStore.getRatioValueAtIndex(getCellIndex());
    }

    @Override
    public String getString() {
        return theStore.getStringValueAtIndex(getCellIndex());
    }

    @Override
    protected void setNullValue() {
        theStore.setValueAtIndex(null, getCellIndex());
    }

    @Override
    protected void setBooleanValue(final Boolean pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setDateValue(final OceanusDate pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setIntegerValue(final Integer pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setLongValue(final Long pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setStringValue(final String pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setDecimalValue(final OceanusDecimal pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setMonetaryValue(final OceanusMoney pValue) {
        theStore.setAlternateAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setHeaderValue(final String pValue) {
        theStore.setAlternateAtIndex(pValue, getCellIndex());
    }
}
