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
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a SecurityPrice.
 */
public class SecurityPricePanel
        extends DataItemPanel<SecurityPrice> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4462131524251971444L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<SecurityPrice> theFieldSet;

    /**
     * The Date.
     */
    private final JDateDayButton theDateButton;

    /**
     * The Price.
     */
    private final JTextField thePrice;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public SecurityPricePanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        thePrice = new JTextField(Deposit.NAMELEN);

        /* Create the buttons */
        theDateButton = new JDateDayButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(SecurityPrice.FIELD_DATE, DataType.DATEDAY, theDateButton);
        theFieldSet.addFieldElement(SecurityPrice.FIELD_PRICE, DataType.PRICE, thePrice);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(SecurityPrice.FIELD_DATE, this);
        theFieldSet.addFieldToPanel(SecurityPrice.FIELD_PRICE, this);
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
        SecurityPrice myPrice = getItem();

        /* Process updates */
        if (myField.equals(SecurityPrice.FIELD_DATE)) {
            /* Update the Date */
            myPrice.setDate(pUpdate.getDateDay());
        } else if (myField.equals(SecurityPrice.FIELD_PRICE)) {
            /* Update the Price */
            myPrice.setPrice(pUpdate.getPrice());
        }
    }
}
