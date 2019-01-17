package com.liveperson.mobilemessagingexercise.model;

import android.util.Log;

/****************************************************************
 * Singleton to hold data shared across the entire application.
 ***************************************************************/
public class ApplicationStorage {

    private static final String TAG = ApplicationStorage.class.getSimpleName();
    private static volatile ApplicationStorage applicationStorage = null;

    private String brandServerBaseUrl = "";
    private String appId = "";
    private String firstName = "";
    private String lastName = "";
    private String phoneNumber = "";
    private String authCode = "";
    private String brandAccountNumber = "";
    private Long campaignId;
    private Long engagementId;
    private String sessionId = "";
    private String visitorId = "";
    private String interactionContextId = "";
    private boolean isLoggedIn = false;
    private String jwt;

    /*
     * Private constructor to protect against creation of additional instances
     */
    private ApplicationStorage() {
        if (applicationStorage != null) {
            //Allow only a single instance
            throw new RuntimeException("It's not possible to construct instances of this class." +
                    "Use the getInstance() method instead.");
        }
    }

    /*
     * Factory method to return an instance
     */
    public synchronized static ApplicationStorage getInstance() {
        if (applicationStorage == null) {
            // Create the singleton, and set up the shared data for the application
            applicationStorage = new ApplicationStorage();
            Log.i(TAG, "ApplicationStorage singleton created");
        }
        return applicationStorage;
    }

    /*****************************************************
     * Bean Methods
     ****************************************************/
    public String getBrandServerBaseUrl() {
        return brandServerBaseUrl;
    }

    public void setBrandServerBaseUrl(String brandServerBaseUrl) {
        this.brandServerBaseUrl = brandServerBaseUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getBrandAccountNumber() {
        return brandAccountNumber;
    }

    public void setBrandAccountNumber(String brandAccountNumber) {
        this.brandAccountNumber = brandAccountNumber;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getEngagementId() {
        return engagementId;
    }

    public void setEngagementId(Long engagementId) {
        this.engagementId = engagementId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getInteractionContextId() {
        return interactionContextId;
    }

    public void setInteractionContextId(String interactionContextId) {
        this.interactionContextId = interactionContextId;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}

