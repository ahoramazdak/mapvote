package net.behpardaz.voting.activities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

/**
 *
 * @author amin
 */
public class Voter implements Serializable {

    private String originId="00000";
    private String voterTitle ="Title1";
    private String organizationId;
    private String organizationName;
    private String cityId;
    private String cityName;
    private String partId;
    private String partName;
    private String mainCityId;
    private String mainCityName;
    private String stateId;
    private String stateName;
    private String certificateCode;
    private String address;
    private String postalCode;
    private String longitude;
    private String latitude;
    private String tel;
    private String mobile;
    private String fax;
    private String hixCode;
    private String hixConnectivity;
    private String createdTime;
    private String partyType;

    public String getCertificateIssueDate() {
        return certificateIssueDate;
    }

    public void setCertificateIssueDate(String certificateIssueDate) {
        this.certificateIssueDate = certificateIssueDate;
    }

    private String certificateIssueDate;


    public String getActivityType() {
        return activityType;
    }

    private String activityType;

    public String getActivityStatus() {
        return activityStatus;
    }

    private String activityStatus;

    public String getGuid() {
        return guid;
    }

    private String guid;

    @Override
    public String toString() {
        return "\noriginId=" + originId + "\n pharmacyTitle=" + voterTitle + "\n organizationId=" + organizationId + "\n organizationName=" + organizationName + "\n cityId=" + cityId + "\n cityName=" + cityName + "\n partId=" + partId + "\n partName=" + partName + "\n mainCityId=" + mainCityId + "\n mainCityName=" + mainCityName + "\n stateId=" + stateId + "\n stateName=" + stateName + "\n certificateCode=" + certificateCode + "\n address=" + address + "\n postalCode=" + postalCode + "\n longitude=" + longitude + "\n latitude=" + latitude + "\n tel=" + tel + "\n mobile=" + mobile + "\n fax=" + fax + "\n hixCode=" + hixCode + "\n hixConnectivity=" + hixConnectivity + "\n createdTime=" + createdTime + "\n partyType=" + partyType ;
    }
    public String mytoString() {
        return getVoterTitle()+"\n\n"+ "آدرس : " + address + "\n تلفن : " + tel + "\nموبایل : " + mobile ;
    }

    public String getOriginId() {
        return originId;
    }

    public String getVoterTitle() {
        return voterTitle;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPartId() {
        return partId;
    }

    public String getPartName() {
        return partName;
    }

    public String getMainCityId() {
        return mainCityId;
    }

    public String getMainCityName() {
        return mainCityName;
    }

    public String getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getTel() {
        return tel;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFax() {
        return fax;
    }

    public String getHixCode() {
        return hixCode;
    }

    public String getHixConnectivity() {
        return hixConnectivity;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getPartyType() {
        return partyType;
    }

}
