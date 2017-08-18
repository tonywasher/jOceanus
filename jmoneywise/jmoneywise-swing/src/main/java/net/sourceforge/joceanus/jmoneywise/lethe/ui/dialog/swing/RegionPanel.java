/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Panel to display/edit/create a Region.
 */
public class RegionPanel
        extends MoneyWiseItemPanel<Region> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public RegionPanel(final TethysSwingGuiFactory pFactory,
                       final MetisFieldManager pFieldMgr,
                       final UpdateSet<MoneyWiseDataType> pUpdateSet,
                       final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Region.NAMELEN);

        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Assign the fields to the panel */
        myPanel.addField(Region.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(Region.FIELD_DESC, MetisDataType.STRING, myDesc);

        /* Define the panel */
        defineMainPanel(myPanel);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Region myItem = getItem();
        if (myItem != null) {
            final RegionList myRegions = getDataList(MoneyWiseDataType.REGION, RegionList.class);
            setItem(myRegions.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final MetisFieldSet<Region> myFieldSet = getFieldSet();

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || getItem().getDesc() != null;
        myFieldSet.setVisibility(Region.FIELD_DESC, bShowDesc);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Region myRegion = getItem();

        /* Process updates */
        if (myField.equals(Region.FIELD_NAME)) {
            /* Update the Name */
            myRegion.setName(pUpdate.getString());
        } else if (myField.equals(Region.FIELD_DESC)) {
            /* Update the Description */
            myRegion.setDescription(pUpdate.getString());
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* No GoTo items */
    }
}
