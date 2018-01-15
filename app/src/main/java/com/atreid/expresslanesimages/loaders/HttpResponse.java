package com.atreid.expresslanesimages.loaders;

/**
 * @author midris
 */
public class HttpResponse {
    private int responseCode;
    private String responseMessage;
    private String responseBody;

    public HttpResponse() {
    }

    public HttpResponse(int responseCode, String responseMessage, String responseBody) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
               "responseCode=" + responseCode +
               ", responseMessage='" + responseMessage + '\'' +
               ", responseBody='" + responseBody + '\'' +
               '}';
    }
}