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
package net.sourceforge.joceanus.jprometheus.data;

import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jPrometheus Data Fields.
 */
public enum PrometheusDataResource implements ResourceId {
    /**
     * DataSet Name.
     */
    DATASET_NAME("DataSet.Name"),

    /**
     * DataSet Generation.
     */
    DATASET_GENERATION("DataSet.Generation"),

    /**
     * DataSet Granularity.
     */
    DATASET_GRANULARITY("DataSet.Granularity"),

    /**
     * DataSet Version.
     */
    DATASET_VERSION("DataSet.Version"),

    /**
     * DataSet Security.
     */
    DATASET_SECURITY("DataSet.Security"),

    /**
     * DataErrorList Name.
     */
    ERRORLIST_NAME("ErrorList.Name"),

    /**
     * DataItem Name.
     */
    DATAITEM_NAME("DataItem.Name"),

    /**
     * DataItem Id.
     */
    DATAITEM_ID("DataItem.Id"),

    /**
     * DataItem Type.
     */
    DATAITEM_TYPE("DataItem.Type"),

    /**
     * DataItem Name.
     */
    DATAITEM_BASE("DataItem.Base"),

    /**
     * DataItem Touch.
     */
    DATAITEM_TOUCH("DataItem.Touch"),

    /**
     * DataItem Deleted.
     */
    DATAITEM_DELETED("DataItem.Deleted"),

    /**
     * DataItem State.
     */
    DATAITEM_STATE("DataItem.State"),

    /**
     * DataItem EditState.
     */
    DATAITEM_EDITSTATE("DataItem.EditState"),

    /**
     * DataItem Header.
     */
    DATAITEM_HEADER("DataItem.Header"),

    /**
     * DataItem Errors.
     */
    DATAITEM_ERRORS("DataItem.Errors"),

    /**
     * DataItem History.
     */
    DATAITEM_HISTORY("DataItem.History"),

    /**
     * DataItem Field Name.
     */
    DATAITEM_FIELD_NAME("DataItem.Field.Name"),

    /**
     * DataItem Field Description.
     */
    DATAITEM_FIELD_DESC("DataItem.Field.Desc"),

    /**
     * DataItem Error Validation.
     */
    DATAITEM_ERROR_VALIDATION("DataItem.Error.Validation"),

    /**
     * DataItem Error Resolution.
     */
    DATAITEM_ERROR_RESOLUTION("DataItem.Error.Resolution"),

    /**
     * DataItem Error Duplicate.
     */
    DATAITEM_ERROR_DUPLICATE("DataItem.Error.Duplicate"),

    /**
     * DataItem Error Unknown.
     */
    DATAITEM_ERROR_UNKNOWN("DataItem.Error.Unknown"),

    /**
     * DataItem Error Exist.
     */
    DATAITEM_ERROR_EXIST("DataItem.Error.Exist"),

    /**
     * DataItem Error Unknown.
     */
    DATAITEM_ERROR_MISSING("DataItem.Error.Missing"),

    /**
     * DataItem Error Length.
     */
    DATAITEM_ERROR_LENGTH("DataItem.Error.Length"),

    /**
     * DataItem Error Negative.
     */
    DATAITEM_ERROR_NEGATIVE("DataItem.Error.Negative"),

    /**
     * DataItem Error Range.
     */
    DATAITEM_ERROR_RANGE("DataItem.Error.Range"),

    /**
     * DataItem Error Disabled.
     */
    DATAITEM_ERROR_DISABLED("DataItem.Error.Disabled"),

    /**
     * DataItem Error Create.
     */
    DATAITEM_ERROR_CREATE("DataItem.Error.Create"),

    /**
     * DataItem Error Multiple.
     */
    DATAITEM_ERROR_MULTIPLE("DataItem.Error.Multiple"),

    /**
     * DataItem Error Zero.
     */
    DATAITEM_ERROR_ZERO("DataItem.Error.Zero"),

    /**
     * Asset Invalid Characters Error.
     */
    DATAITEM_ERROR_INVALIDCHAR("DataItem.Error.InvalidChar"),

    /**
     * DataList Name.
     */
    DATALIST_NAME("DataList.Name"),

    /**
     * DataList Style.
     */
    DATALIST_STYLE("DataList.Style"),

    /**
     * DataList Size.
     */
    DATALIST_SIZE("DataList.Size"),

    /**
     * DataList Maps.
     */
    DATALIST_MAPS("DataList.Maps"),

    /**
     * DataMap Name.
     */
    DATAMAP_NAME("DataMap.Name"),

    /**
     * DataMap Keys.
     */
    DATAMAP_KEYS("DataMap.Keys"),

    /**
     * DataMap KeyCounts.
     */
    DATAMAP_KEYCOUNTS("DataMap.KeyCounts"),

    /**
     * EncryptedItem Name.
     */
    ENCRYPTED_NAME("Encrypted.Name"),

    /**
     * EncryptedItem Usage Error.
     */
    ENCRYPTED_ERROR_USAGE("Encrypted.Error.Usage"),

    /**
     * StaticData Name.
     */
    STATICDATA_NAME("Static.Name"),

    /**
     * StaticData Enabled.
     */
    STATICDATA_ENABLED("Static.Enabled"),

    /**
     * StaticData SortOrder.
     */
    STATICDATA_SORT("Static.Order"),

    /**
     * StaticData Class.
     */
    STATICDATA_CLASS("Static.Class"),

    /**
     * StaticData Id Error.
     */
    STATICDATA_ERROR_ID("Static.Error.BadId"),

    /**
     * StaticData Name Error.
     */
    STATICDATA_ERROR_NAME("Static.Error.BadName"),

    /**
     * StaticDataMap Name.
     */
    STATICDATAMAP_NAME("StaticMap.Name"),

    /**
     * StaticDataMap OrderCounts.
     */
    STATICDATAMAP_ORDERCOUNTS("StaticMap.OrderCounts"),

    /**
     * DataInfo Name.
     */
    DATAINFO_NAME("DataInfo.Name"),

    /**
     * DataInfo List.
     */
    DATAINFO_LIST("DataInfo.List"),

    /**
     * DataInfo InfoType.
     */
    DATAINFO_TYPE("DataInfo.Type"),

    /**
     * DataInfo Owner.
     */
    DATAINFO_OWNER("DataInfo.Owner"),

    /**
     * DataInfo Value.
     */
    DATAINFO_VALUE("DataInfo.Value"),

    /**
     * DataInfo Link.
     */
    DATAINFO_LINK("DataInfo.Link"),

    /**
     * DataInfo Bad Type Error.
     */
    DATAINFO_ERROR_TYPE("DataInfo.Error.Type"),

    /**
     * DataInfo Bad Data Error.
     */
    DATAINFO_ERROR_DATA("DataInfo.Error.Data"),

    /**
     * DataInfo Bad Class Error.
     */
    DATAINFO_ERROR_CLASS("DataInfo.Error.Class"),

    /**
     * DataInfoSet Name.
     */
    DATAINFOSET_NAME("DataInfoSet.Name"),

    /**
     * DataInfoSet Values.
     */
    DATAINFOSET_VALUES("DataInfoSet.Values"),

    /**
     * DataInfoSet Values.
     */
    DATAINFOSET_ERROR_BADSET("DataInfoSet.Error.BadSet"),

    /**
     * DataValues Children.
     */
    DATAVALUES_CHILDREN("DataValues.Children"),

    /**
     * DataValues Child.
     */
    DATAVALUES_CHILD("DataValues.Child"),

    /**
     * DataValues Type Attribute.
     */
    DATAVALUES_ATTRTYPE("DataValues.AttrType"),

    /**
     * DataValues Size Attribute.
     */
    DATAVALUES_ATTRSIZE("DataValues.AttrSize"),

    /**
     * DataValues Version Attribute.
     */
    DATAVALUES_ATTRVER("DataValues.AttrVersion"),

    /**
     * DataGroup Name.
     */
    DATAGROUP_NAME("DataGroup.Name"),

    /**
     * DataGroup Parent.
     */
    DATAGROUP_PARENT("DataGroup.Parent"),

    /**
     * ControlKey Name.
     */
    CONTROLKEY_NAME("ControlKey.Name"),

    /**
     * ControlKey List.
     */
    CONTROLKEY_LIST("ControlKey.List"),

    /**
     * ControlKey isPrime.
     */
    CONTROLKEY_PRIME("ControlKey.isPrime"),

    /**
     * ControlKey Prime Hash.
     */
    CONTROLKEY_PRIMEHASH("ControlKey.PrimeHash"),

    /**
     * ControlKey alternate Hash.
     */
    CONTROLKEY_ALTHASH("ControlKey.AltHash"),

    /**
     * ControlKey Prime Hash.
     */
    CONTROLKEY_PRIMEKEY("ControlKey.PrimeKey"),

    /**
     * ControlKey alternate Hash.
     */
    CONTROLKEY_ALTKEY("ControlKey.AltKey"),

    /**
     * ControlKey Prime Hash.
     */
    CONTROLKEY_PRIMEBYTES("ControlKey.PrimeBytes"),

    /**
     * ControlKey alternate Bytes.
     */
    CONTROLKEY_ALTBYTES("ControlKey.AltBytes"),

    /**
     * ControlKey Database.
     */
    CONTROLKEY_DATABASE("ControlKey.DataBase"),

    /**
     * DataKeySet Name.
     */
    DATAKEYSET_NAME("DataKeySet.Name"),

    /**
     * DataKeySet List.
     */
    DATAKEYSET_LIST("DataKeySet.List"),

    /**
     * DataKeySet CreationDate.
     */
    DATAKEYSET_CREATION("DataKeySet.CreationDate"),

    /**
     * DataKeySet KeyMap.
     */
    DATAKEYSET_KEYMAP("DataKeySet.KeyMap"),

    /**
     * DataKeySet CipherSet.
     */
    DATAKEYSET_CIPHERSET("DataKeySet.CipherSet"),

    /**
     * DataKey Name.
     */
    DATAKEY_NAME("DataKey.Name"),

    /**
     * DataKey List.
     */
    DATAKEY_LIST("DataKey.List"),

    /**
     * DataKey Type.
     */
    DATAKEY_TYPE("DataKey.Type"),

    /**
     * DataKey Definition.
     */
    DATAKEY_DEF("DataKey.Definition"),

    /**
     * DataKey Key.
     */
    DATAKEY_KEY("DataKey.Key"),

    /**
     * DataKey Cipher.
     */
    DATAKEY_CIPHER("DataKey.Cipher"),

    /**
     * ControlData Name.
     */
    CONTROLDATA_NAME("ControlData.Name"),

    /**
     * ControlData List.
     */
    CONTROLDATA_LIST("ControlData.List"),

    /**
     * ControlData Version.
     */
    CONTROLDATA_VERSION("ControlData.Version"),

    /**
     * ControlData Already Exists Error.
     */
    CONTROLDATA_ERROR_EXISTS("ControlData.Error.Exists");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(DataSet.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private PrometheusDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jPrometheus.data";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for cryptography item.
     * @param pValue the Value
     * @return the resource key
     */
    protected static PrometheusDataResource getKeyForCryptoItem(final CryptographyDataType pValue) {
        switch (pValue) {
            case CONTROLKEY:
                return CONTROLKEY_NAME;
            case DATAKEYSET:
                return DATAKEYSET_NAME;
            case DATAKEY:
                return DATAKEY_NAME;
            case CONTROLDATA:
                return CONTROLDATA_NAME;
            default:
                return null;
        }
    }

    /**
     * Obtain key for cryptography list.
     * @param pValue the Value
     * @return the resource key
     */
    protected static PrometheusDataResource getKeyForCryptoList(final CryptographyDataType pValue) {
        switch (pValue) {
            case CONTROLKEY:
                return CONTROLKEY_LIST;
            case DATAKEYSET:
                return DATAKEYSET_LIST;
            case DATAKEY:
                return DATAKEY_LIST;
            case CONTROLDATA:
                return CONTROLDATA_LIST;
            default:
                return null;
        }
    }
}
