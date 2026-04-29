/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.themis.gui.source;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollItem;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisPackage;

/**
 * Button to select package from module.
 */
public class ThemisUISourcePackageSelect
        implements OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The scroll button.
     */
    private final TethysUIScrollButtonManager<ThemisPackage> theButton;

    /**
     * The package menu.
     */
    private final TethysUIScrollMenu<ThemisPackage> thePackageMenu;

    /**
     * The package select.
     */
    private final ThemisUISourceFileSelect theFileSelect;

    /**
     * The prefixLabel.
     */
    private final TethysUILabel thePrefixLabel;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The current module.
     */
    private ThemisModule theModule;

    /**
     * The current package.
     */
    private ThemisPackage thePackage;

    /**
     * The current prefix.
     */
    private String thePrefix;

    /**
     * Constructor.
     *
     * @param pFactory    the factory
     * @param pFileSelect the fileSelectButton
     */
    ThemisUISourcePackageSelect(final TethysUIFactory<?> pFactory,
                                final ThemisUISourceFileSelect pFileSelect) {
        /* Store the file select */
        theFileSelect = pFileSelect;

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        thePrefixLabel = myControls.newLabel();
        final TethysUILabel myPromptLabel = myControls.newLabel(ThemisUIResource.PROMPT_PACKAGE.getValue());

        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(ThemisPackage.class);
        thePackageMenu = theButton.getMenu();

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        thePanel = myPanes.newHBoxPane();
        thePanel.addNode(myPromptLabel);
        thePanel.addNode(thePrefixLabel);
        thePanel.addNode(theButton);

        /* Set listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPackage());
        theButton.setMenuConfigurator(e -> buildPackageMenu());
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * Obtain the current package.
     *
     * @return the current package
     */
    ThemisPackage getCurrentPackage() {
        return thePackage;
    }

    /**
     * Set the current module.
     *
     * @param pModule the current module
     */
    void setCurrentModule(final ThemisModule pModule) {
        /* Store the module */
        theModule = pModule;

        /* Determine the prefix */
        determinePrefix();

        /* Set the default package */
        final ThemisPackage myPackage = getDefaultPackage();
        theButton.setValue(myPackage, getDisplayName(myPackage));
        handleNewPackage();
    }

    /**
     * Obtain the default package.
     *
     * @return the default package
     */
    private ThemisPackage getDefaultPackage() {
        /* If we have a non-null module */
        if (theModule != null) {
            /* Loop through the available packages */
            for (ThemisPackage myPackage : theModule.getPackages()) {
                /* Skip package if necessary */
                if (skipPackage(myPackage)) {
                    continue;
                }

                /* Return the valid package */
                return myPackage;
            }
        }

        /* No package */
        return null;
    }

    /**
     * Determine the prefix.
     */
    private void determinePrefix() {
        /* Initialise the prefix */
        thePrefix = null;
        theButton.setVisible(false);

        /* If we have a non-null modules */
        if (theModule != null) {
            /* Loop through the available packages */
            for (ThemisPackage myPackage : theModule.getPackages()) {
                /* Adjust the prefix */
                adjustPrefix(myPackage);
            }
        }

        /* Update the prefixLabel */
        thePrefixLabel.setText(thePrefix);
        thePrefixLabel.setVisible(thePrefix != null && !thePrefix.isEmpty());
    }

    /**
     * Adjust prefix.
     *
     * @param pPackage the package
     */
    private void adjustPrefix(final ThemisPackage pPackage) {
        /* Ignore placeHolder */
        if (skipPackage(pPackage)) {
            return;
        }

        /* If we do not have a prefix */
        final String myName = pPackage.getPackage();
        if (thePrefix == null) {
            thePrefix = myName;

            /* else if we need to change the prefix */
        } else {
            /* We have more than one package so display selection button */
            theButton.setVisible(true);

            /* Determine common prefix */
            thePrefix = getCommonPrefix(myName, thePrefix);
        }
    }

    /**
     * Obtain common prefix.
     *
     * @param pFirst  the first name
     * @param pSecond the second name
     * @return the prefix
     */
    private String getCommonPrefix(final String pFirst,
                                   final String pSecond) {
        if (pFirst.equals(pSecond)) {
            return pFirst;
        }
        return pFirst.length() >= pSecond.length()
                ? getCommonPrefix(getParentName(pFirst), pSecond)
                : getCommonPrefix(pFirst, getParentName(pSecond));
    }

    /**
     * Obtain Parent Name.
     *
     * @param pName the name
     * @return the parent name
     */
    private String getParentName(final String pName) {
        /* Determine the short name */
        final int iIndex = pName.lastIndexOf(ThemisChar.PERIOD);
        return iIndex == -1 ? "" : pName.substring(0, iIndex);
    }

    /**
     * Obtain the displayName of the package.
     *
     * @param pPackage the package
     * @return the displayName
     */
    private String getDisplayName(final ThemisPackage pPackage) {
        final int myPrefixLen = thePrefix == null ? 0 : thePrefix.length();
        return pPackage == null ? "" : pPackage.getPackage().substring(myPrefixLen);
    }

    /**
     * Build the package menu.
     */
    private void buildPackageMenu() {
        /* Reset the popUp menu */
        thePackageMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<ThemisPackage> myActive = null;
        final ThemisPackage myCurr = theButton.getValue();

        /* Loop through the available packages */
        for (ThemisPackage myPackage : theModule.getPackages()) {
            /* skip package if required */
            if (skipPackage(myPackage)) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<ThemisPackage> myItem = thePackageMenu.addItem(myPackage, getDisplayName(myPackage));

            /* If this is the active package */
            if (myPackage.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Should we skip the package?
     *
     * @param pPackage the package
     * @return true/false
     */
    private boolean skipPackage(final ThemisPackage pPackage) {
        /* Skip placeholders */
        return pPackage.isPlaceHolder();
    }

    /**
     * Handle new Package.
     */
    private void handleNewPackage() {
        /* Select the new package */
        thePackage = theButton.getValue();
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE);
        theFileSelect.setCurrentPackage(thePackage);
    }
}
