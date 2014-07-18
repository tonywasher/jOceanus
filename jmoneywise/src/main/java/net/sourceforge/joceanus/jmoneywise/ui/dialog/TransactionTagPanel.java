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
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a TransactionTag.
 */
public class TransactionTagPanel
        extends DataItemPanel<TransactionTag> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5535355076826373500L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TransactionTag> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public TransactionTagPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(TransactionTag.NAMELEN);
        theDesc = new JTextField(TransactionTag.DESCLEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionTag.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(TransactionTag.FIELD_DESC, DataType.STRING, theDesc);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_DESC, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        TransactionTag myTag = getItem();

        /* Process updates */
        if (myField.equals(TransactionTag.FIELD_NAME)) {
            /* Update the Name */
            myTag.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(TransactionTag.FIELD_NAME)) {
            /* Update the Description */
            myTag.setDescription(pUpdate.getValue(String.class));
        }
    }
}
