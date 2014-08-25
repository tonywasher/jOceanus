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

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a TransactionTag.
 */
public class TransactionTagPanel
        extends MoneyWiseDataItemPanel<TransactionTag> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5535355076826373500L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TransactionTag> theFieldSet;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionTagPanel(final JFieldManager pFieldMgr,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, TransactionTag.NAMELEN);
        restrictField(myDesc, TransactionTag.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionTag.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(TransactionTag.FIELD_DESC, DataType.STRING, myDesc);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_DESC, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        TransactionTag myItem = getItem();
        if (myItem != null) {
            TransactionTagList myTags = findDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
            setItem(myTags.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || getItem().getDesc() != null;
        theFieldSet.setVisibility(TransactionTag.FIELD_DESC, bShowDesc);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        TransactionTag myTag = getItem();

        /* Process updates */
        if (myField.equals(TransactionTag.FIELD_NAME)) {
            /* Update the Name */
            myTag.setName(pUpdate.getString());
        } else if (myField.equals(TransactionTag.FIELD_DESC)) {
            /* Update the Description */
            myTag.setDescription(pUpdate.getString());
        }
    }
}
