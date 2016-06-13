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
package net.sourceforge.joceanus.jmoneywise.ui.dialog.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a TransactionTag.
 */
public class TransactionTagPanel
        extends MoneyWiseDataItemPanel<TransactionTag> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<TransactionTag> theFieldSet;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionTagPanel(final TethysSwingGuiFactory pFactory,
                               final MetisFieldManager pFieldMgr,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final MoneyWiseErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, TransactionTag.NAMELEN);
        restrictField(myDesc, TransactionTag.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionTag.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(TransactionTag.FIELD_DESC, MetisDataType.STRING, myDesc);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(TransactionTag.FIELD_DESC, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        TransactionTag myItem = getItem();
        if (myItem != null) {
            TransactionTagList myTags = getDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
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
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
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

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* No GoTo items */
    }
}
