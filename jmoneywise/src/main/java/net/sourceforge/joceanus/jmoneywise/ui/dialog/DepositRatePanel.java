/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a DepositRate.
 */
public class DepositRatePanel
        extends DataItemPanel<DepositRate> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1773676548644359220L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<DepositRate> theFieldSet;

    /**
     * The Date.
     */
    private final JDateDayButton theDateButton;

    /**
     * The Rate.
     */
    private final JTextField theRate;

    /**
     * The Bonus.
     */
    private final JTextField theBonus;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public DepositRatePanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theRate = new JTextField(Deposit.NAMELEN);
        theBonus = new JTextField(Deposit.DESCLEN);

        /* Create the buttons */
        theDateButton = new JDateDayButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(DepositRate.FIELD_RATE, DataType.RATE, theRate);
        theFieldSet.addFieldElement(DepositRate.FIELD_BONUS, DataType.RATE, theBonus);
        theFieldSet.addFieldElement(DepositRate.FIELD_ENDDATE, DataType.DATEDAY, theDateButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositRate.FIELD_RATE, this);
        theFieldSet.addFieldToPanel(DepositRate.FIELD_BONUS, this);
        theFieldSet.addFieldToPanel(DepositRate.FIELD_ENDDATE, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Nothing to do */
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        DepositRate myRate = getItem();

        /* Process updates */
        if (myField.equals(DepositRate.FIELD_RATE)) {
            /* Update the Rate */
            myRate.setRate(pUpdate.getRate());
        } else if (myField.equals(DepositRate.FIELD_BONUS)) {
            /* Update the Bonus */
            myRate.setBonus(pUpdate.getRate());
        } else if (myField.equals(DepositRate.FIELD_ENDDATE)) {
            /* Update the EndDate */
            myRate.setEndDate(pUpdate.getDateDay());
        }
    }
}
