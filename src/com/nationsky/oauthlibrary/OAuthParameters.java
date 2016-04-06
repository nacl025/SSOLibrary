package com.nationsky.oauthlibrary;

public class OAuthParameters {

    public OAuthParameters() {
    }
    private String userName;
    private String password;
    private String userIdCode;
    private String tokenId;
    private OAuthError error;
                           
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

    public OAuthError getError() {
    	if(error == null){
    		error = new OAuthError(0);
    	}
        return error;
    }

    public void setError(OAuthError error) {
        this.error = error;
    }

    public String getUserIdCode() {
        return userIdCode;
    }

    public void setUserIdCode(String userIdCode) {
        this.userIdCode = userIdCode;
    }
    
}
