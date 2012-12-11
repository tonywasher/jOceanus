package net.sourceforge.jOceanus.jFieldSet;

import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;

/**
 * Basic Field interface.
 * @author Tony Washer
 */
public interface JFieldItem
        extends JDataContents {
    /**
     * Get the render state for the item.
     * @return the render state
     */
    RenderState getRenderState();

    /**
     * Get the render state for the field.
     * @param pField the field
     * @return the render state
     */
    RenderState getRenderState(final JDataField pField);

    /**
     * Get the Errors for the field.
     * @param pField the field
     * @return the error text
     */
    String getFieldErrors(final JDataField pField);

    /**
     * Get the Errors for the fields.
     * @param pFields the fields
     * @return the error text
     */
    String getFieldErrors(final JDataField[] pFields);
}
