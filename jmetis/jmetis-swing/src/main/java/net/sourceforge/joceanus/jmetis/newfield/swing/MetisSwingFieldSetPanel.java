/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.newfield.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Currency;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldSetPanel;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyItem;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButton.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Swing FieldSet Panel.
 */
public class MetisSwingFieldSetPanel
        extends MetisFieldSetPanel<JComponent, Color, Font, Icon> {
    /**
     * The Node.
     */
    private final JPanel theNode;

    /**
     * Constructor.
     * @param pParent the parent pair
     */
    protected MetisSwingFieldSetPanel(final MetisSwingFieldSetPanelPair pParent) {
        /* Initialise underlying panel */
        super(pParent);

        /* Create the Node */
        theNode = new JPanel();
        theNode.setLayout(new BoxLayout(theNode, BoxLayout.Y_AXIS));
    }

    /**
     * Constructor.
     * @param pAttributes the attribute set
     */
    public MetisSwingFieldSetPanel(final MetisSwingFieldAttributeSet pAttributes) {
        this(pAttributes, new MetisDataFormatter());
    }

    /**
     * Constructor.
     * @param pAttributes the attribute set
     * @param pFormatter the formatter
     */
    public MetisSwingFieldSetPanel(final MetisSwingFieldAttributeSet pAttributes,
                                   final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pAttributes, pFormatter);

        /* Create the Node */
        theNode = new JPanel();
        theNode.setLayout(new BoxLayout(theNode, BoxLayout.Y_AXIS));
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public MetisSwingFieldSetStringItem addStringField(final MetisField pField) {
        return new MetisSwingFieldSetStringItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetShortItem addShortField(final MetisField pField) {
        return new MetisSwingFieldSetShortItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetIntegerItem addIntegerField(final MetisField pField) {
        return new MetisSwingFieldSetIntegerItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetLongItem addLongField(final MetisField pField) {
        return new MetisSwingFieldSetLongItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetMoneyItem addMoneyField(final MetisField pField) {
        return new MetisSwingFieldSetMoneyItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetPriceItem addPriceField(final MetisField pField) {
        return new MetisSwingFieldSetPriceItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetRateItem addRateField(final MetisField pField) {
        return new MetisSwingFieldSetRateItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetUnitsItem addUnitsField(final MetisField pField) {
        return new MetisSwingFieldSetUnitsItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetDilutionItem addDilutionField(final MetisField pField) {
        return new MetisSwingFieldSetDilutionItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetRatioItem addRatioField(final MetisField pField) {
        return new MetisSwingFieldSetRatioItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetDateItem addDateButtonField(final MetisField pField) {
        return new MetisSwingFieldSetDateItem(this, pField);
    }

    @Override
    public MetisSwingFieldSetDateItem addDateButtonField(final MetisField pField,
                                                         final TethysDateButtonManager<?> pManager) {
        return new MetisSwingFieldSetDateItem(this, pField, (TethysSwingDateButtonManager) pManager);
    }

    @Override
    public <X> MetisSwingFieldSetScrollItem<X> addScrollButtonField(final MetisField pField,
                                                                    final Class<X> pClass) {
        return new MetisSwingFieldSetScrollItem<>(this, pField, pClass);
    }

    @Override
    public <X> MetisSwingFieldSetScrollItem<X> addScrollButtonField(final MetisField pField,
                                                                    final TethysScrollButtonManager<X, JComponent, Icon> pManager,
                                                                    final Class<X> pClass) {
        return new MetisSwingFieldSetScrollItem<>(this, pField, pClass, (TethysSwingScrollButtonManager<X>) pManager);
    }

    @Override
    public <X> MetisSwingFieldSetListItem<X> addListButtonField(final MetisField pField,
                                                                final Class<X> pClass) {
        return new MetisSwingFieldSetListItem<>(this, pField, pClass);
    }

    @Override
    public <X> MetisSwingFieldSetListItem<X> addListButtonField(final MetisField pField,
                                                                final TethysListButtonManager<X, JComponent, Icon> pManager,
                                                                final Class<X> pClass) {
        return new MetisSwingFieldSetListItem<>(this, pField, pClass, (TethysSwingListButtonManager<X>) pManager);
    }

    @Override
    public <X> MetisSwingFieldSetIconItem<X> addIconButtonField(final MetisField pField,
                                                                final Class<X> pClass) {
        return new MetisSwingFieldSetIconItem<>(this, pField, pClass);
    }

    @Override
    public <X> MetisSwingFieldSetIconItem<X> addIconButtonField(final MetisField pField,
                                                                final TethysIconButtonManager<X, JComponent, Icon> pManager,
                                                                final Class<X> pClass) {
        return new MetisSwingFieldSetIconItem<>(this, pField, pClass, pManager);
    }

    /**
     * Item class.
     * @param <T> the item type
     */
    protected static class MetisSwingFieldSetPanelItem<T>
            extends MetisFieldSetPanelItem<T, JComponent, Color, Font, Icon> {
        /**
         * The node.
         */
        private final JPanel theNode;

        /**
         * The label.
         */
        private final JLabel theLabel;

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field definition
         * @param pNumeric is the field numeric?
         * @param pEdit the edit field
         */
        protected MetisSwingFieldSetPanelItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField,
                                              final boolean pNumeric,
                                              final TethysDataEditField<T, JComponent, Color, Font, Icon> pEdit) {
            /* Set fields */
            this(pPanel, pField, null, pNumeric, pEdit);
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field definition
         * @param pClass the item class
         * @param pNumeric is the field numeric?
         * @param pEdit the edit field
         */
        protected MetisSwingFieldSetPanelItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField,
                                              final Class<T> pClass,
                                              final boolean pNumeric,
                                              final TethysDataEditField<T, JComponent, Color, Font, Icon> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, pNumeric, pEdit);

            /* Create the label */
            theLabel = new JLabel();
            theLabel.setText(pField.getName() + ":");
            theLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            /* Create the Node */
            theNode = new JPanel(new BorderLayout());
            theNode.add(theLabel, BorderLayout.LINE_START);
            theNode.add(pEdit.getNode(), BorderLayout.CENTER);

            /* Add to panel */
            getPanel().theNode.add(theNode);
        }

        @Override
        protected MetisSwingFieldSetPanel getPanel() {
            return (MetisSwingFieldSetPanel) super.getPanel();
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            theNode.setVisible(true);
        }

        @Override
        protected void detachFromPanel(final int pIndex) {
            theNode.setVisible(false);
        }

        @Override
        protected double getLabelWidth() {
            return theLabel.getWidth();
        }

        @Override
        protected void setLabelWidth(final double pWidth) {
            theLabel.setMinimumSize(new Dimension((int) pWidth, 0));
        }

        @Override
        protected void setTheValue(final T pValue) {
            getEditField().setValue(pValue);
        }

        @Override
        protected void setEditable(final boolean pEditable) {
            getEditField().setEditable(pEditable);
        }
    }

    /**
     * DateButtonField.
     */
    public static class MetisSwingFieldSetDateItem
            extends MetisSwingFieldSetPanelItem<TethysDate>
            implements MetisFieldSetDateItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetDateItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField) {
            /* Create a new dateButton Manager */
            this(pPanel, pField, new TethysSwingDateButtonManager(pPanel.getFormatter()));
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the date manager
         */
        protected MetisSwingFieldSetDateItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField,
                                             final TethysSwingDateButtonManager pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDate.class, false, new TethysSwingDateButtonField(pManager));
        }

        @Override
        protected TethysSwingDateButtonField getEditField() {
            return (TethysSwingDateButtonField) super.getEditField();
        }

        @Override
        public TethysSwingDateButtonManager getManager() {
            return getEditField().getDateManager();
        }
    }

    /**
     * ScrollButtonField.
     * @param <T> the item class
     */
    public static class MetisSwingFieldSetScrollItem<T>
            extends MetisSwingFieldSetPanelItem<T>
            implements MetisFieldSetScrollItem<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisSwingFieldSetScrollItem(final MetisSwingFieldSetPanel pPanel,
                                               final MetisField pField,
                                               final Class<T> pClass) {
            /* Create a new scrollButton Manager */
            this(pPanel, pField, pClass, new TethysSwingScrollButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the scroll manager
         * @param pClass the item class
         */
        protected MetisSwingFieldSetScrollItem(final MetisSwingFieldSetPanel pPanel,
                                               final MetisField pField,
                                               final Class<T> pClass,
                                               final TethysSwingScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, new TethysSwingScrollButtonField<T>(pManager));
        }

        @Override
        protected TethysSwingScrollButtonField<T> getEditField() {
            return (TethysSwingScrollButtonField<T>) super.getEditField();
        }

        @Override
        public TethysSwingScrollButtonManager<T> getManager() {
            return getEditField().getScrollManager();
        }
    }

    /**
     * ListButtonField.
     * @param <T> the item class
     */
    public static class MetisSwingFieldSetListItem<T>
            extends MetisSwingFieldSetPanelItem<TethysItemList<T>>
            implements MetisFieldSetListItem<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisSwingFieldSetListItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField,
                                             final Class<T> pClass) {
            /* Create a new listButton Manager */
            this(pPanel, pField, pClass, new TethysSwingListButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the list manager
         * @param pClass the item class
         */
        protected MetisSwingFieldSetListItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField,
                                             final Class<T> pClass,
                                             final TethysSwingListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, false, new TethysSwingListButtonField<T>(pManager));
        }

        @Override
        protected TethysSwingListButtonField<T> getEditField() {
            return (TethysSwingListButtonField<T>) super.getEditField();
        }

        @Override
        public TethysSwingListButtonManager<T> getManager() {
            return getEditField().getListManager();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected TethysItemList<T> getCastValue(final Object pValue) {
            return (TethysItemList<T>) pValue;
        }

        @Override
        protected boolean isInstance(final Object pValue) {
            return TethysItemList.class.isInstance(pValue);
        }
    }

    /**
     * IconButtonField.
     * @param <T> the item class
     */
    public static class MetisSwingFieldSetIconItem<T>
            extends MetisSwingFieldSetPanelItem<T>
            implements MetisFieldSetIconItem<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisSwingFieldSetIconItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField,
                                             final Class<T> pClass) {
            /* Create a new iconButton Manager */
            this(pPanel, pField, pClass, new TethysSwingSimpleIconButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the list manager
         * @param pClass the item class
         */
        protected MetisSwingFieldSetIconItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField,
                                             final Class<T> pClass,
                                             final TethysIconButtonManager<T, JComponent, Icon> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, new TethysSwingIconButtonField<T>(pManager));
        }

        @Override
        protected TethysSwingIconButtonField<T> getEditField() {
            return (TethysSwingIconButtonField<T>) super.getEditField();
        }

        @Override
        public TethysIconButtonManager<T, JComponent, Icon> getManager() {
            return getEditField().getIconManager();
        }
    }

    /**
     * StringField.
     */
    public static class MetisSwingFieldSetStringItem
            extends MetisSwingFieldSetPanelItem<String> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetStringItem(final MetisSwingFieldSetPanel pPanel,
                                               final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, String.class, false, new TethysSwingStringTextField());
        }

        @Override
        protected TethysSwingStringTextField getEditField() {
            return (TethysSwingStringTextField) super.getEditField();
        }
    }

    /**
     * ShortField.
     */
    public static class MetisSwingFieldSetShortItem
            extends MetisSwingFieldSetPanelItem<Short> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetShortItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Short.class, true, new TethysSwingShortTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingShortTextField getEditField() {
            return (TethysSwingShortTextField) super.getEditField();
        }
    }

    /**
     * IntegerField.
     */
    public static class MetisSwingFieldSetIntegerItem
            extends MetisSwingFieldSetPanelItem<Integer> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetIntegerItem(final MetisSwingFieldSetPanel pPanel,
                                                final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Integer.class, true, new TethysSwingIntegerTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingIntegerTextField getEditField() {
            return (TethysSwingIntegerTextField) super.getEditField();
        }
    }

    /**
     * LongField.
     */
    public static class MetisSwingFieldSetLongItem
            extends MetisSwingFieldSetPanelItem<Long> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetLongItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Long.class, true, new TethysSwingLongTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingLongTextField getEditField() {
            return (TethysSwingLongTextField) super.getEditField();
        }
    }

    /**
     * MoneyField.
     */
    public static class MetisSwingFieldSetMoneyItem
            extends MetisSwingFieldSetPanelItem<TethysMoney>
            implements TethysCurrencyItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetMoneyItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysMoney.class, true, new TethysSwingMoneyTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingMoneyTextField getEditField() {
            return (TethysSwingMoneyTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * PriceField.
     */
    public static class MetisSwingFieldSetPriceItem
            extends MetisSwingFieldSetPanelItem<TethysPrice>
            implements TethysCurrencyItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetPriceItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysPrice.class, true, new TethysSwingPriceTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingPriceTextField getEditField() {
            return (TethysSwingPriceTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * RateField.
     */
    public static class MetisSwingFieldSetRateItem
            extends MetisSwingFieldSetPanelItem<TethysRate> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetRateItem(final MetisSwingFieldSetPanel pPanel,
                                             final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRate.class, true, new TethysSwingRateTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingRateTextField getEditField() {
            return (TethysSwingRateTextField) super.getEditField();
        }
    }

    /**
     * UnitsField.
     */
    public static class MetisSwingFieldSetUnitsItem
            extends MetisSwingFieldSetPanelItem<TethysUnits> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetUnitsItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysUnits.class, true, new TethysSwingUnitsTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingUnitsTextField getEditField() {
            return (TethysSwingUnitsTextField) super.getEditField();
        }
    }

    /**
     * DilutionField.
     */
    public static class MetisSwingFieldSetDilutionItem
            extends MetisSwingFieldSetPanelItem<TethysDilution> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetDilutionItem(final MetisSwingFieldSetPanel pPanel,
                                                 final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDilution.class, true, new TethysSwingDilutionTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingDilutionTextField getEditField() {
            return (TethysSwingDilutionTextField) super.getEditField();
        }
    }

    /**
     * RatioField.
     */
    public static class MetisSwingFieldSetRatioItem
            extends MetisSwingFieldSetPanelItem<TethysRatio> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisSwingFieldSetRatioItem(final MetisSwingFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRatio.class, true, new TethysSwingRatioTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysSwingRatioTextField getEditField() {
            return (TethysSwingRatioTextField) super.getEditField();
        }
    }
}
