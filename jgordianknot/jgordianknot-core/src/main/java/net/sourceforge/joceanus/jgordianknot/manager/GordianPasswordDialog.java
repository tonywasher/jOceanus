/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.manager;

import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysPasswordField;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class GordianPasswordDialog<N, I> {
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
     * Text for Password Label.
     */
    private static final String NLS_PASSWORD = GordianMgrResource.LABEL_PASSWORD.getValue();

    /**
     * Text for Confirm Label.
     */
    private static final String NLS_CONFIRM = GordianMgrResource.LABEL_CONFIRM.getValue();

    /**
     * Text for OK Button.
     */
    private static final String NLS_OK = GordianMgrResource.BUTTON_OK.getValue();

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = GordianMgrResource.BUTTON_CANCEL.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_ERROR = GordianMgrResource.TITLE_ERROR.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_CONFIRMERROR = GordianMgrResource.ERROR_CONFIRM.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR1 = GordianMgrResource.ERROR_LENGTH1.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR2 = GordianMgrResource.ERROR_LENGTH2.getValue();

    /**
     * The GUI factory.
     */
    private final TethysGuiFactory<N, I> theFactory;

    /**
     * The container box.
     */
    private final TethysBorderPaneManager<N, I> theContainer;

    /**
     * OK Button.
     */
    private final TethysButton<N, I> theOKButton;

    /**
     * Cancel Button.
     */
    private final TethysButton<N, I> theCancelButton;

    /**
     * Error field.
     */
    private final TethysLabel<N, I> theErrorField;

    /**
     * Password field.
     */
    private final TethysPasswordField<N, I> thePassField;

    /**
     * Confirmation field.
     */
    private final TethysPasswordField<N, I> theConfirmField;

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
    protected GordianPasswordDialog(final TethysGuiFactory<N, I> pFactory,
                                    final boolean pNeedConfirm) {

        /* Store the parameters */
        theFactory = pFactory;
        needConfirm = pNeedConfirm;

        /* Create the Password label */
        final TethysLabel<N, I> myPassLabel = pFactory.newLabel(NLS_PASSWORD);

        /* Create the confirm label */
        final TethysLabel<N, I> myConfLabel = pFactory.newLabel(NLS_CONFIRM);

        /* Create OK button */
        theOKButton = pFactory.newButton();
        theOKButton.setTextOnly();
        theOKButton.setText(NLS_OK);
        theOKButton.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create cancel button */
        theCancelButton = pFactory.newButton();
        theCancelButton.setTextOnly();
        theCancelButton.setText(NLS_CANCEL);
        theCancelButton.getEventRegistrar().addEventListener(e -> processCancel());

        /* Create password field */
        thePassField = pFactory.newPasswordField();
        thePassField.setPreferredWidth(PASSWORD_FIELD_LEN);
        thePassField.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create confirm field */
        theConfirmField = pFactory.newPasswordField();
        theConfirmField.setPreferredWidth(PASSWORD_FIELD_LEN);
        theConfirmField.getEventRegistrar().addEventListener(e -> processPassword());

        /* Create the error panel */
        theErrorField = pFactory.newLabel();
        theErrorField.setBorderTitle(NLS_ERROR);
        theErrorField.setBorderPadding(PADDING_SIZE);

        /* Set the Error panel to be red */
        theErrorField.setErrorText();

        /* Create the panel */
        final TethysGridPaneManager<N, I> myForm = pFactory.newGridPane();
        myForm.setHGap(PADDING_SIZE);
        myForm.setVGap(PADDING_SIZE);

        /* Layout the password panel */
        myForm.addCellAtPosition(myPassLabel, 0, 0);
        myForm.setCellAlignment(myPassLabel, TethysAlignment.EAST);
        myForm.addCellAtPosition(thePassField, 0, 1);
        if (needConfirm) {
            myForm.addCellAtPosition(myConfLabel, 1, 0);
            myForm.setCellAlignment(myConfLabel, TethysAlignment.EAST);
            myForm.addCellAtPosition(theConfirmField, 1, 1);
        }
        myForm.addCellAtPosition(theOKButton, 0, 2);
        if (needConfirm) {
            myForm.addCellAtPosition(theCancelButton, 1, 2);
        }

        /* Create the scene */
        theContainer = pFactory.newBorderPane();
        theContainer.setBorderPadding(PADDING_SIZE);
        theContainer.setCentre(myForm);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected TethysGuiFactory<N, I> getFactory() {
        return theFactory;
    }

    /**
     * Obtain the container.
     * @return the container
     */
    protected TethysBorderPaneManager<N, I> getContainer() {
        return theContainer;
    }

    /**
     * Obtain the passwordNode.
     * @return the node
     */
    protected N getPasswordNode() {
        return thePassField.getNode();
    }

    /**
     * Obtain the confirmNode.
     * @return the node
     */
    protected N getConfirmNode() {
        return theConfirmField.getNode();
    }

    /**
     * Obtain the okButtonNode.
     * @return the node
     */
    protected N getOKButtonNode() {
        return theOKButton.getNode();
    }

    /**
     * Obtain the cancelButtonNode.
     * @return the node
     */
    protected N getCancelButtonNode() {
        return theCancelButton.getNode();
    }

    /**
     * Obtain the password.
     * @return the password
     */
    public char[] getPassword() {
        return thePassword;
    }

    /**
     * Is the password set.
     * @return true/false
     */
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    /**
     * Release resources.
     */
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

    /**
     * set the error.
     * @param pError the error to display
     */
    public void setError(final String pError) {
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
