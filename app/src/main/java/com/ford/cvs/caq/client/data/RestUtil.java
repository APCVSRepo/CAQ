package com.ford.cvs.caq.client.data;

//import org.apache.http.NameValuePair;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuchong on 17/5/2016.
 */
public class RestUtil {
//    public static HttpPost createPost(String urlString, String[] keys, String[]  values) {
//        HttpPost post = new HttpPost();
//        post.setURI(URI.create(urlString));
//        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
//        for (int i=0;i<keys.length; i++) {
//            pairList.add(new BasicNameValuePair(keys[i], values[i]));
//        }
//
//
//        try {
//            post.setEntity(new UrlEncodedFormEntity(pairList));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return post;
//    }
//
//    public static HttpGet createGet(String url, String[] keys, String[]  values) {
//        HttpGet getReq = new HttpGet();
//        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
//        for (int i=0; i<keys.length; i++) {
//            pairList.add(new BasicNameValuePair(keys[i], values[i]));
//        }
//        String param = URLEncodedUtils.format(pairList, "utf8");
//
//        getReq.setURI(URI.create(url + "?" + param));
//
//        return getReq;
//    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest
                    digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }
}
