/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.test.ui;

import java.io.File;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAboutBox;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAlert;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIChildDialog;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIDirectorySelector;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIPasswordDialog;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;

/**
 * Test Dialog options.
 */
public class TethysTestDialog {
    /**
     * The Panel.
     */
    private final TethysUIBoxPaneManager thePane;

    /**
     * Constructor.
     * @param pFactory the gui factory
     */
    TethysTestDialog(final TethysUIFactory<?> pFactory) {
        /* Create the pane */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIDialogFactory myDialogs = pFactory.dialogFactory();
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        thePane = myPanes.newVBoxPane();

        /* Create the Alert button */
        final TethysUILabel myResult = myControls.newLabel();

        /* Create the Error button */
        TethysUIButton myButton = myButtons.newButton();
        myButton.setText("Error");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAlert myAlert = myDialogs.newAlert();
            myAlert.setMessage("Error");
            myAlert.setTitle("Error Title");
            myAlert.showError();
        });
        thePane.addNode(myButton);

        /* Create the Warning button */
        myButton = myButtons.newButton();
        myButton.setText("Warning");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAlert myAlert = myDialogs.newAlert();
            myAlert.setMessage("Warning");
            myAlert.setTitle("Warning Title");
            myAlert.showWarning();
        });
        thePane.addNode(myButton);

        /* Create the Info button */
        myButton = myButtons.newButton();
        myButton.setText("Info");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAlert myAlert = myDialogs.newAlert();
            myAlert.setMessage("Info");
            myAlert.setTitle("Info Title");
            myAlert.showInfo();
        });
        thePane.addNode(myButton);

        /* Create the ConfirmYesNo button */
        myButton = myButtons.newButton();
        myButton.setText("ConfirmYesNo");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAlert myAlert = myDialogs.newAlert();
            myAlert.setMessage("ConfirmYesNo");
            myAlert.setTitle("ConfirmYesNo Title");
            final boolean myRes = myAlert.confirmYesNo();
            myResult.setText("Confirm=" + myRes);
        });
        thePane.addNode(myButton);

        /* Create the ConfirmOKCancel button */
        myButton = myButtons.newButton();
        myButton.setText("ConfirmOKCancel");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAlert myAlert = myDialogs.newAlert();
            myAlert.setMessage("ConfirmOKCancel");
            myAlert.setTitle("ConfirmOKCancel Title");
            final boolean myRes = myAlert.confirmOKCancel();
            myResult.setText("Confirm=" + myRes);
        });
        thePane.addNode(myButton);

        /* Create the ChildDialog button */
        final TethysUIButton myChildButton = myButtons.newButton();
        myChildButton.setText("ChildDialog");
        myChildButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIChildDialog myChild = myDialogs.newChildDialog();
            myChild.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, c -> {
                myResult.setText("ChildDialog Closed");
                myChildButton.setEnabled(true);
            });
            myChild.setTitle("ChildDialog");
            myChildButton.setEnabled(false);
            final TethysUILabel myLabel = myControls.newLabel("Child");
            final TethysUIBorderPaneManager myContent = myPanes.newBorderPane();
            myContent.setCentre(myLabel);
            myChild.setContent(myContent);
            myChild.showDialog();
        });
        thePane.addNode(myChildButton);

        /* Create the Password button */
        myButton = myButtons.newButton();
        myButton.setText("Password");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIPasswordDialog myPass = myDialogs.newPasswordDialog("Enter Password", false);
            if (myPass.showDialog()) {
                myResult.setText("Password=" + new String(myPass.getPassword()));
            } else {
                myResult.setText("Password Cancelled");
            }
            myPass.release();
        });
        thePane.addNode(myButton);

        /* Create the New Password button */
        myButton = myButtons.newButton();
        myButton.setText("New Password");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIPasswordDialog myPass = myDialogs.newPasswordDialog("New Password", true);
            if (myPass.showDialog()) {
                myResult.setText("Password=" + new String(myPass.getPassword()));
            } else {
                myResult.setText("Password Cancelled");
            }
            myPass.release();
        });
        thePane.addNode(myButton);

        /* Create the New FileSelector button */
        myButton = myButtons.newButton();
        myButton.setText("FileSelect");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIFileSelector myFile = myDialogs.newFileSelector();
            myFile.setTitle("File Select");
            final File mySelected = myFile.selectFile();
            if (mySelected != null) {
                myResult.setText("File=" + mySelected.getName());
            } else {
                myResult.setText("FileSelect Cancelled");
            }
        });
        thePane.addNode(myButton);

        /* Create the New DirSelector button */
        myButton = myButtons.newButton();
        myButton.setText("DirectorySelect");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIDirectorySelector myDir = myDialogs.newDirectorySelector();
            myDir.setTitle("Directory Select");
            final File mySelected = myDir.selectDirectory();
            if (mySelected != null) {
                myResult.setText("Directory=" + mySelected.getName());
            } else {
                myResult.setText("DirectorySelect Cancelled");
            }
        });
        thePane.addNode(myButton);

        /* Create the New AboutBox button */
        myButton = myButtons.newButton();
        myButton.setText("About");
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> {
            final TethysUIAboutBox myAbout = myDialogs.newAboutBox();
            myAbout.showDialog();
        });
        thePane.addNode(myButton);
        thePane.addNode(myResult);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return thePane;
    }
}
