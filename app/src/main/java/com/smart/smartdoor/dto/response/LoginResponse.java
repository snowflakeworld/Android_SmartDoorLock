package com.smart.smartdoor.dto.response;

import org.json.JSONObject;

public class LoginResponse extends BaseResponse {

    private final static String KEY_CID = "cid";
    private final static String KEY_NAME = "name";
    private final static String KEY_GENDER = "gender";
    private final static String KEY_BIRTHDAY = "birthday";
    private final static String KEY_CITIZEN_NUMBER = "citizenNumber";
    private final static String KEY_INSTALL_PLACE = "installPlace";
    private final static String KEY_ROLE_TYPE = "roleType";
    private final static String KEY_STATE = "state";
    private final static String KEY_DISTRICT_ID = "districtId";
    private final static String KEY_DISTRICT_INFO = "districtInfo";
    private final static String KEY_DETAIL_INFO = "detailInfo";
    private final static String KEY_CONV_DETAIL_INFO = "convDetailInfo";
    private final static String KEY_PROPERTY_INFO = "propertyInfo";
    private final static String KEY_DEVICE_ID = "deviceId";

    private String cid;
    private String name;
    private String gender;
    private String birthday;
    private String citizenNumber;
    private String installPlace;
    private String roleType;
    private int state;
    private int districtId;
    private String districtInfo;
    private String detailInfo;
    private String convDetailInfo;
    private String propertyInfo;
    private long deviceId;

    public LoginResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);
            setCid(obj.getString(KEY_CID));
            setName(obj.getString(KEY_NAME));
            setGender(obj.getString(KEY_GENDER));
            setBirthday(obj.getString(KEY_BIRTHDAY));
            setCitizenNumber(obj.getString(KEY_CITIZEN_NUMBER));
            setInstallPlace(obj.getString(KEY_INSTALL_PLACE));
            setRoleType(obj.getString(KEY_ROLE_TYPE));
            setState(obj.getInt(KEY_STATE));
            setDistrictId(obj.getInt(KEY_DISTRICT_ID));
            setDistrictInfo(obj.getString(KEY_DISTRICT_INFO));
            setDetailInfo(obj.getString(KEY_DETAIL_INFO));
            setConvDetailInfo(obj.getString(KEY_CONV_DETAIL_INFO));
            setDeviceId(obj.getLong(KEY_DEVICE_ID));
            if (obj.has(KEY_PROPERTY_INFO))
                setPropertyInfo(obj.getString(KEY_PROPERTY_INFO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCitizenNumber() {
        return citizenNumber;
    }

    public void setCitizenNumber(String citizenNumber) {
        this.citizenNumber = citizenNumber;
    }

    public String getInstallPlace() {
        return installPlace;
    }

    public void setInstallPlace(String installPlace) {
        this.installPlace = installPlace;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getDistrictInfo() {
        return districtInfo;
    }

    public void setDistrictInfo(String districtInfo) {
        this.districtInfo = districtInfo;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    public String getConvDetailInfo() {
        return convDetailInfo;
    }

    public void setConvDetailInfo(String convDetailInfo) {
        this.convDetailInfo = convDetailInfo;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getPropertyInfo() {
        return propertyInfo;
    }

    public void setPropertyInfo(String propertyInfo) {
        this.propertyInfo = propertyInfo;
    }
}
