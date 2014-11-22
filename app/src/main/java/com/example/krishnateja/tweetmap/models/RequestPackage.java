package com.example.krishnateja.tweetmap.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class RequestPackage {
    private String URI;
    private String method="GET";
    private Map<String,String> params=new HashMap<String, String>();
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getURI() {
        return URI;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public void setParam(String key,String value){
        params.put(key,value);
    }
    public String getEncodedParams(){
        StringBuilder sb=new StringBuilder();
        for(String  key : params.keySet()){
            String value = null;
            try {
                value= URLEncoder.encode(params.get(key),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(sb.length()>0){
                sb.append("&");
            }
            sb.append(key + "=" + value);

        }
        return sb.toString();
    }
}
