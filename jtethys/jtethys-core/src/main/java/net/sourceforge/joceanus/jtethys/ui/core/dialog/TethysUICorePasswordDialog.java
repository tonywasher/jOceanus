/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.dialog;

import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIPasswordField;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIPasswordDialog;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUIResource;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public abstract class TethysUICorePasswordDialog
    implements TethysUIPasswordDialog {
    /**
     * Minimum password length.
     */
    private static final int MIN_PASSWORD_LEN = 8;

    /**
     * password field width.
     */
    private static final int PASSWORD_FIELD_LEN = 240;

    /**
     * adding width.
     */
    private static final int PADDING_SIZE = 5;

    /**
     * Text for Password title.
     */
    protected static final String NLS_TITLEPASS = TethysUIResource.PASS_TITLE_PASSWORD.getValue();

    /**
     * Text for New Password title.
     */
    protected static final String NLS_TITLENEWPASS = TethysUIResource.PASS_TITLE_NEWPASS.getValue();

    /**
     * Text for Password Label.
     */
    private static final String NLS_PASSWORD = TethysUIResource.PASS_LABEL_PASSWORD.getValue();

    /**
     * Text for Confirm Label.
     */
    private static final String NLS_CONFIRM = TethysUIResource.PASS_LABEL_CONFIRM.getValue();

    /**
     * Text for OK Button.
     */
    private static final String NLS_OK = TethysUIResource.BUTTON_OK.getValue();

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = TethysUIResource.BUTTON_CANCEL.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_ERROR = TethysUIResource.PASS_TITLE_ERROR.getValue();

    /**
     * Text for Bad Password Error.
     */
    private static final String NLS_ERRORPASS = TethysUIResource.PASS_ERROR_BADPASS.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_CONFIRMERROR = TethysUIResource.PASS_ERROR_CONFIRM.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR1 = TethysUIResource.PASS_ERROR_LENGTH1.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR2 = TethysUIResource.PASS_ERROR_LENGTH2.getValue();

    /**
     * The GUI factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The container box.
     */
    private final TethysUIBorderPaneManager theContainer;

    /**
     * OK Button.
     */
    private final TethysUIButton theOKButton;

    /**
     * Cancel Button.
     */
    private final TethysUIButton theCancelButton;

    /**
     * Error field.
     */
    private final TethysUILabel theErrorField;

    /**
     * Password field.
     */
    private final TethysUIPasswordField thePassField;

    /**
     * Confirmation field.
     */
    private final TethysUIPasswordField theConfirmField;

    /**
     * Is the password set.
     */
    private boolean isPasswordSet;

    /**
     * Do we need to confirm the password.
     */
    private final boolean needConfirm;

    /**
     * Obtained password.
     */
    private char[] thePassword;

    /**
     * Confirmation password.
     */
    private char[] theConfirm;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pNeedConfirm true/false
     */
    protected TethysUICorePasswordDialog(final TethysUICoreFactory<?> pFactory,
                                         final boolean pNeedConfirm) {

        /* Store the parameters */
        theFactory = pFactory;
        needConfirm = pNeedConfirm;

        /* Create the Password label */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myPassLabel = myControls.newLabel(NLS_PASSWORD);

        /* Create the confirm label */
        final TethysUILabel myConfLabel = myControls.newLabel(NLS_CONFIRM);

        /* Create OK button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theOKButton = myButtons.newButton();
        theOKButton.setTextOnly();
        theOKButton.setText(NLS_OK);
        theOKButton.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create cancel button */
        theCancelButton = myButtons.newButton();
        theCancelButton.setTextOnly();
        theCancelButton.setText(NLS_CANCEL);
        theCancelButton.getEventRegistrar().addEventListener(e -> processCancel());

        /* Create password field */
        thePassField = myControls.newPasswordField();
        thePassField.setPreferredWidth(PASSWORD_FIELD_LEN);
        thePassField.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create confirm field */
        theConfirmField = myControls.newPasswordField();
        theConfirmField.setPreferredWidth(PASSWORD_FIELD_LEN);
        theConfirmField.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create the error panel */
        theErrorField = myControls.newLabel();
        theErrorField.setBorderTitle(NLS_ERROR);
        theErrorField.setBorderPadding(PADDING_SIZE);

        /* Set the Error panel to be red */
        theErrorField.setErrorText();

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIGridPaneManager myForm = myPanes.newGridPane();
        myForm.setHGap(PADDING_SIZE);
        myForm.setVGap(PADDING_SIZE);

        /* Layout the password panel */
        myForm.addCellAtPosition(myPassLabel, 0, 0);
        myForm.setCellAlignment(myPassLabel, TethysUIAlignment.EAST);
        myForm.addCellAtPosition(thePassField, 0, 1);
        if (needConfirm) {
            myForm.addCellAtPosition(myConfLabel, 1, 0);
            myForm.setCellAlignment(myConfLabel, TethysUIAlignment.EAST);
            myForm.addCellAtPosition(theConfirmField, 1, 1);
        }
        myForm.addCellAtPosition(theOKButton, 0, 2);
        if (needConfirm) {
            myForm.addCellAtPosition(theCancelButton, 1, 2);
        }

        /* Create the scene */
        theContainer = myPanes.newBorderPane();
        theContainer.setBorderPadding(PADDING_SIZE);
        theContainer.setCentre(myForm);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected TethysUICoreFactory<?> getFactory() {
        return theFactory;
    }

    /**
     * Obtain the container.
     * @return the container
     */
    protected TethysUIBorderPaneManager getContainer() {
        return theContainer;
    }

    /**
     * Obtain the passwordNode.
     * @return the node
     */
    protected TethysUIComponent getPasswordNode() {
        return thePassField;
    }

    /**
     * Obtain the confirmNode.
     * @return the node
     */
    protected TethysUIComponent getConfirmNode() {
        return theConfirmField;
    }

    /**
     * Obtain the okButtonNode.
     * @return the node
     */
    protected TethysUIComponent getOKButtonNode() {
        return theOKButton;
    }

    /**
     * Obtain the cancelButtonNode.
     * @return the node
     */
    protected TethysUIComponent getCancelButtonNode() {
        return theCancelButton;
    }

    @Override
    public char[] getPassword() {
        return thePassword;
    }

    @Override
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    @Override
    public void release() {
        if (thePassword != null) {
            Arrays.fill(thePassword, (char) 0);
        }
        if (theConfirm != null) {
            Arrays.fill(theConfirm, (char) 0);
        }
    }

    /**
     * process cancel.
     */
    private void processCancel() {
        /* Note that we have set the password */
        isPasswordSet = false;

        /* Close the dialog */
        closeDialog();
    }

    /**
     * process password.
     */
    private void processPassword() {
        /* Access the password */
        thePassword = thePassField.getPassword();

        /* If we need to confirm the password */
        if (needConfirm) {
            /* Access the confirm password */
            theConfirm = theConfirmField.getPassword();

            /* If the password is less than the minimum length */
            if (thePassword.length < MIN_PASSWORD_LEN) {
                /* Set error and return */
                setError(NLS_LENGTHERR1
                        + " "
                        + MIN_PASSWORD_LEN
                        + " "
                        + NLS_LENGTHERR2);
                return;
            }

            /* If the confirm password does not match */
            if (!Arrays.equals(thePassword, theConfirm)) {
                /* Set error and return */
                setError(NLS_CONFIRMERROR);
                return;
            }
        }

        /* Note that we have set the password */
        isPasswordSet = true;

        /* Close the dialog */
        closeDialog();
    }

    @Override
    public void reportBadPassword() {
        setError(NLS_ERRORPASS);
    }

    /**
     * set the error.
     * @param pError the error to display
     */
    private void setError(final String pError) {
        /* Set the string to the error field */
        theErrorField.setText(pError);

        /* Show that we need to update the password */
        isPasswordSet = false;

        /* Set the error into the dialog */
        theContainer.setSouth(theErrorField);

        /* ReSize the dialog */
        reSizeDialog();
    }

    /**
     * Close the dialog.
     */
    protected abstract void closeDialog();

    /**
     * ReSize the dialog.
     */
    protected abstract void reSizeDialog();
}
