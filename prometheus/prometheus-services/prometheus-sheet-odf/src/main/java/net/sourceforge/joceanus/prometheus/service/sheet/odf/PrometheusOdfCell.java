/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;
import net.sourceforge.joceanus.tethys.decimal.TethysPrice;
import net.sourceforge.joceanus.tethys.decimal.TethysRate;
import net.sourceforge.joceanus.tethys.decimal.TethysRatio;
import net.sourceforge.joceanus.tethys.decimal.TethysUnits;

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
     * @param pStore the cell storage
     * @param pRow the row
     * @param pIndex the index
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
    public TethysDate getDate() {
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
    public TethysMoney getMoney() throws OceanusException {
        return theStore.getMoneyValueAtIndex(getCellIndex());
    }

    @Override
    public TethysPrice getPrice() throws OceanusException {
        return theStore.getPriceValueAtIndex(getCellIndex());
    }

    @Override
    public TethysUnits getUnits() throws OceanusException {
        return theStore.getUnitsValueAtIndex(getCellIndex());
    }

    @Override
    public TethysRate getRate() throws OceanusException {
        return theStore.getRateValueAtIndex(getCellIndex());
    }

    @Override
    public TethysRatio getRatio() throws OceanusException {
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
    protected void setDateValue(final TethysDate pValue) {
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
    protected void setDecimalValue(final TethysDecimal pValue) {
        theStore.setValueAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setMonetaryValue(final TethysMoney pValue) {
        theStore.setAlternateAtIndex(pValue, getCellIndex());
    }

    @Override
    protected void setHeaderValue(final String pValue) {
        theStore.setAlternateAtIndex(pValue, getCellIndex());
    }
}
