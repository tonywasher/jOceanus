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
package net.sourceforge.joceanus.jmetis.newfield.javafx;

import java.util.Currency;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButtonManager;

/**
 * JavaFX FieldSet Panel.
 */
public class MetisFXFieldSetPanel
        extends MetisFieldSetPanel<Node, Font, Color, Node> {
    /**
     * The Node.
     */
    private final VBox theNode;

    /**
     * The Node Children.
     */
    private final ObservableList<Node> theChildren;

    /**
     * Constructor.
     * @param pParent the parent pair
     */
    protected MetisFXFieldSetPanel(final MetisFXFieldSetPanelPair pParent) {
        /* Initialise underlying panel */
        super(pParent);

        /* Create the Node */
        theNode = new VBox();
        theChildren = theNode.getChildren();
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pAttributes the attribute set
     */
    public MetisFXFieldSetPanel(final TethysFXGuiFactory pFactory,
                                final MetisFXFieldAttributeSet pAttributes) {
        /* Initialise underlying class */
        super(pFactory, pAttributes);

        /* Create the Node */
        theNode = new VBox();
        theChildren = theNode.getChildren();
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public MetisFXFieldSetStringItem addStringField(final MetisField pField) {
        return new MetisFXFieldSetStringItem(this, pField);
    }

    @Override
    public MetisFXFieldSetShortItem addShortField(final MetisField pField) {
        return new MetisFXFieldSetShortItem(this, pField);
    }

    @Override
    public MetisFXFieldSetIntegerItem addIntegerField(final MetisField pField) {
        return new MetisFXFieldSetIntegerItem(this, pField);
    }

    @Override
    public MetisFXFieldSetLongItem addLongField(final MetisField pField) {
        return new MetisFXFieldSetLongItem(this, pField);
    }

    @Override
    public MetisFXFieldSetMoneyItem addMoneyField(final MetisField pField) {
        return new MetisFXFieldSetMoneyItem(this, pField);
    }

    @Override
    public MetisFXFieldSetPriceItem addPriceField(final MetisField pField) {
        return new MetisFXFieldSetPriceItem(this, pField);
    }

    @Override
    public MetisFXFieldSetRateItem addRateField(final MetisField pField) {
        return new MetisFXFieldSetRateItem(this, pField);
    }

    @Override
    public MetisFXFieldSetUnitsItem addUnitsField(final MetisField pField) {
        return new MetisFXFieldSetUnitsItem(this, pField);
    }

    @Override
    public MetisFXFieldSetDilutionItem addDilutionField(final MetisField pField) {
        return new MetisFXFieldSetDilutionItem(this, pField);
    }

    @Override
    public MetisFXFieldSetRatioItem addRatioField(final MetisField pField) {
        return new MetisFXFieldSetRatioItem(this, pField);
    }

    @Override
    public MetisFXFieldSetDateItem addDateButtonField(final MetisField pField) {
        return new MetisFXFieldSetDateItem(this, pField);
    }

    @Override
    public <X> MetisFXFieldSetScrollItem<X> addScrollButtonField(final MetisField pField,
                                                                 final Class<X> pClass) {
        return new MetisFXFieldSetScrollItem<>(this, pField, pClass);
    }

    @Override
    public <X> MetisFXFieldSetListItem<X> addListButtonField(final MetisField pField) {
        return new MetisFXFieldSetListItem<>(this, pField);
    }

    @Override
    public <X> MetisFXFieldSetIconItem<X> addIconButtonField(final MetisField pField,
                                                             final Class<X> pClass) {
        return new MetisFXFieldSetIconItem<>(this, pField, pClass);
    }

    @Override
    public <X, S> MetisFXFieldSetStateIconItem<X, S> addStateIconButtonField(final MetisField pField,
                                                                             final Class<X> pClass) {
        return new MetisFXFieldSetStateIconItem<>(this, pField, pClass);
    }

    /**
     * Item class.
     * @param <T> the item type
     */
    protected static class MetisFXFieldSetPanelItem<T>
            extends MetisFieldSetPanelItem<T, Node, Font, Color, Node> {
        /**
         * The node.
         */
        private final BorderPane theNode;

        /**
         * The label.
         */
        private final Label theLabel;

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field definition
         * @param pNumeric is the field numeric?
         * @param pEdit the edit field
         */
        protected MetisFXFieldSetPanelItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField,
                                           final boolean pNumeric,
                                           final TethysDataEditField<T, Node, Node> pEdit) {
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
        protected MetisFXFieldSetPanelItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField,
                                           final Class<T> pClass,
                                           final boolean pNumeric,
                                           final TethysDataEditField<T, Node, Node> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, pNumeric, pEdit);

            /* Create the label */
            theLabel = new Label();
            theLabel.setText(pField.getName() + ":");
            theLabel.setAlignment(Pos.CENTER_RIGHT);

            /* Create the Node */
            theNode = new BorderPane();
            theNode.setLeft(theLabel);
            theNode.setCenter(pEdit.getNode());

            /* Add to panel */
            getPanel().theChildren.add(theNode);
        }

        @Override
        protected MetisFXFieldSetPanel getPanel() {
            return (MetisFXFieldSetPanel) super.getPanel();
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            getPanel().theChildren.add(pChildNo, theNode);
        }

        @Override
        protected void detachFromPanel(final int pIndex) {
            getPanel().theChildren.remove(pIndex);
        }

        @Override
        protected double getLabelWidth() {
            return theLabel.getWidth();
        }

        @Override
        protected void setLabelWidth(final double pWidth) {
            theLabel.setMinWidth(pWidth);
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
    public static class MetisFXFieldSetDateItem
            extends MetisFXFieldSetPanelItem<TethysDate>
            implements TethysDateField<Node, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetDateItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDate.class, false, pPanel.getGuiFactory().newDateField());
        }

        @Override
        protected TethysFXDateButtonField getEditField() {
            return (TethysFXDateButtonField) super.getEditField();
        }

        @Override
        public TethysFXDateButtonManager getDateManager() {
            return getEditField().getDateManager();
        }
    }

    /**
     * ScrollButtonField.
     * @param <T> the item class
     */
    public static class MetisFXFieldSetScrollItem<T>
            extends MetisFXFieldSetPanelItem<T>
            implements TethysScrollField<T, Node, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetScrollItem(final MetisFXFieldSetPanel pPanel,
                                            final MetisField pField,
                                            final Class<T> pClass) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, pPanel.getGuiFactory().newScrollField());
        }

        @Override
        protected TethysFXScrollButtonField<T> getEditField() {
            return (TethysFXScrollButtonField<T>) super.getEditField();
        }

        @Override
        public TethysFXScrollButtonManager<T> getScrollManager() {
            return getEditField().getScrollManager();
        }
    }

    /**
     * ListButtonField.
     * @param <T> the item class
     */
    public static class MetisFXFieldSetListItem<T>
            extends MetisFXFieldSetPanelItem<TethysItemList<T>>
            implements TethysListField<T, Node, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetListItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, false, pPanel.getGuiFactory().newListField());
        }

        @Override
        protected TethysFXListButtonField<T> getEditField() {
            return (TethysFXListButtonField<T>) super.getEditField();
        }

        @Override
        public TethysFXListButtonManager<T> getListManager() {
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
    public static class MetisFXFieldSetIconItem<T>
            extends MetisFXFieldSetPanelItem<T>
            implements TethysIconField<T, Node, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetIconItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final Class<T> pClass) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, pPanel.getGuiFactory().newSimpleIconField());
        }

        @Override
        protected TethysFXIconButtonField<T> getEditField() {
            return (TethysFXIconButtonField<T>) super.getEditField();
        }

        @Override
        public TethysSimpleIconButtonManager<T, Node, Node> getIconManager() {
            return getEditField().getIconManager();
        }
    }

    /**
     * StateIconButtonField.
     * @param <T> the item class
     * @param <S> the state class
     */
    public static class MetisFXFieldSetStateIconItem<T, S>
            extends MetisFXFieldSetPanelItem<T>
            implements TethysStateIconField<T, S, Node, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetStateIconItem(final MetisFXFieldSetPanel pPanel,
                                               final MetisField pField,
                                               final Class<T> pClass) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, pPanel.getGuiFactory().newStateIconField());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected TethysFXStateIconButtonField<T, S> getEditField() {
            return (TethysFXStateIconButtonField<T, S>) super.getEditField();
        }

        @Override
        public TethysStateIconButtonManager<T, S, Node, Node> getIconManager() {
            return getEditField().getIconManager();
        }
    }

    /**
     * StringField.
     */
    public static class MetisFXFieldSetStringItem
            extends MetisFXFieldSetPanelItem<String> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetStringItem(final MetisFXFieldSetPanel pPanel,
                                            final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, String.class, false, pPanel.getGuiFactory().newStringField());
        }

        @Override
        protected TethysFXStringTextField getEditField() {
            return (TethysFXStringTextField) super.getEditField();
        }
    }

    /**
     * ShortField.
     */
    public static class MetisFXFieldSetShortItem
            extends MetisFXFieldSetPanelItem<Short> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetShortItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Short.class, true, pPanel.getGuiFactory().newShortField());
        }

        @Override
        protected TethysFXShortTextField getEditField() {
            return (TethysFXShortTextField) super.getEditField();
        }
    }

    /**
     * IntegerField.
     */
    public static class MetisFXFieldSetIntegerItem
            extends MetisFXFieldSetPanelItem<Integer> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetIntegerItem(final MetisFXFieldSetPanel pPanel,
                                             final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Integer.class, true, pPanel.getGuiFactory().newIntegerField());
        }

        @Override
        protected TethysFXIntegerTextField getEditField() {
            return (TethysFXIntegerTextField) super.getEditField();
        }
    }

    /**
     * LongField.
     */
    public static class MetisFXFieldSetLongItem
            extends MetisFXFieldSetPanelItem<Long> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetLongItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Long.class, true, pPanel.getGuiFactory().newLongField());
        }

        @Override
        protected TethysFXLongTextField getEditField() {
            return (TethysFXLongTextField) super.getEditField();
        }
    }

    /**
     * MoneyField.
     */
    public static class MetisFXFieldSetMoneyItem
            extends MetisFXFieldSetPanelItem<TethysMoney>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetMoneyItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysMoney.class, true, pPanel.getGuiFactory().newMoneyField());
        }

        @Override
        protected TethysFXMoneyTextField getEditField() {
            return (TethysFXMoneyTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * PriceField.
     */
    public static class MetisFXFieldSetPriceItem
            extends MetisFXFieldSetPanelItem<TethysPrice>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetPriceItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysPrice.class, true, pPanel.getGuiFactory().newPriceField());
        }

        @Override
        protected TethysFXPriceTextField getEditField() {
            return (TethysFXPriceTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * RateField.
     */
    public static class MetisFXFieldSetRateItem
            extends MetisFXFieldSetPanelItem<TethysRate> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetRateItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRate.class, true, pPanel.getGuiFactory().newRateField());
        }

        @Override
        protected TethysFXRateTextField getEditField() {
            return (TethysFXRateTextField) super.getEditField();
        }
    }

    /**
     * UnitsField.
     */
    public static class MetisFXFieldSetUnitsItem
            extends MetisFXFieldSetPanelItem<TethysUnits> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetUnitsItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysUnits.class, true, pPanel.getGuiFactory().newUnitsField());
        }

        @Override
        protected TethysFXUnitsTextField getEditField() {
            return (TethysFXUnitsTextField) super.getEditField();
        }
    }

    /**
     * DilutionField.
     */
    public static class MetisFXFieldSetDilutionItem
            extends MetisFXFieldSetPanelItem<TethysDilution> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetDilutionItem(final MetisFXFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDilution.class, true, pPanel.getGuiFactory().newDilutionField());
        }

        @Override
        protected TethysFXDilutionTextField getEditField() {
            return (TethysFXDilutionTextField) super.getEditField();
        }
    }

    /**
     * RatioField.
     */
    public static class MetisFXFieldSetRatioItem
            extends MetisFXFieldSetPanelItem<TethysRatio> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetRatioItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRatio.class, true, pPanel.getGuiFactory().newRatioField());
        }

        @Override
        protected TethysFXRatioTextField getEditField() {
            return (TethysFXRatioTextField) super.getEditField();
        }
    }
}
