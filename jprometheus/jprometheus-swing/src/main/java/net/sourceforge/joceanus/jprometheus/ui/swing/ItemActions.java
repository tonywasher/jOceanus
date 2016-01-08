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
/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Utility panel to handle actions on selected item.
 * @param <E> the data type enum class
 */
public class ItemActions<E extends Enum<E>>
        extends TethysSwingEnablePanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6509682125660951159L;

    /**
     * Strut height.
     */
    protected static final int STRUT_HEIGHT = 5;

    /**
     * The goTo button.
     */
    private final JScrollButton<PrometheusGoToEvent> theGoToButton;

    /**
     * The edit button.
     */
    private final JIconButton<Boolean> theEditButton;

    /**
     * The delete button.
     */
    private final JIconButton<Boolean> theDeleteButton;

    /**
     * The parent panel.
     */
    private final DataItemPanel<?, E> theParent;

    /**
     * Constructor.
     * @param pParent the parent panel
     */
    protected ItemActions(final DataItemPanel<?, E> pParent) {
        /* Store parameters */
        theParent = pParent;

        /* Create the buttons */
        DefaultIconButtonState<Boolean> myEditState = new DefaultIconButtonState<>();
        DefaultIconButtonState<Boolean> myDeleteState = new DefaultIconButtonState<>();

        /* Create the buttons */
        theGoToButton = PrometheusIcons.getGotoButton();
        theEditButton = new JIconButton<>(myEditState);
        theDeleteButton = new JIconButton<>(myDeleteState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theGoToButton.setMargin(myInsets);
        theEditButton.setMargin(myInsets);
        theDeleteButton.setMargin(myInsets);

        /* Set the states */
        PrometheusIcons.buildEditButton(myEditState);
        PrometheusIcons.buildDeleteButton(myDeleteState);

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(0, STRUT_HEIGHT);

        /* Create the layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theGoToButton);
        add(Box.createRigidArea(myStrutSize));
        add(theEditButton);
        add(Box.createRigidArea(myStrutSize));
        add(theDeleteButton);

        /* Create the listener */
        theGoToButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> theParent.processGoToRequest(theGoToButton.getValue()));
        theEditButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, e -> theParent.requestEdit());
        theDeleteButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, e -> theParent.requestDelete());

        JScrollMenuBuilder<PrometheusGoToEvent> myMenuBuilder = theGoToButton.getMenuBuilder();
        theParent.declareGoToMenuBuilder(myMenuBuilder);
        myMenuBuilder.getEventRegistrar().addEventListener(e -> {
            myMenuBuilder.clearMenu();
            theParent.buildGoToMenu();
        });
    }

    /**
     * Update state.
     */
    protected void updateState() {
        /* Set the edit value and enable state */
        theEditButton.storeValue(Boolean.TRUE);
        theEditButton.setEnabled(theParent.isEditable());

        /* Set the delete value and enable state */
        theDeleteButton.storeValue(Boolean.TRUE);
        theDeleteButton.setEnabled(theParent.isDeletable());
    }
}
