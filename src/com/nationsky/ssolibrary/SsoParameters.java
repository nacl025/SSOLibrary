package com.nationsky.ssolibrary;

public class SsoParameters {

    public SsoParameters() {
    }

    private String userName;
    private String password;
    private String userIdCode;
    private String tokenId;
    private String errorCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUserIdCode() {
        return userIdCode;
    }

    public void setUserIdCode(String userIdCode) {
        this.userIdCode = userIdCode;
    }

    @Override
    public String toString() {
        return "SsoParameters{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userIdCode='" + userIdCode + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }

}
