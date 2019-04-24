package com.ford.cvs.caq.client.data;

import android.text.TextUtils;

import com.ford.cvs.caq.client.HttpRequest;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.List;

/**
 * Created by CLIU130 on 6/3/2016.
 */
public class HanvonOnlineReader extends ServerDataReader {
    private final String AUTH_URL_PHONE = "http://www.hwlantian.com/sky-user/v1/login/phone";
    private final String AUTH_URL_EMAIL = "http://www.hwlantian.com/sky-user/v1/login/email";
    private final String LIST_DEVICE_URL = "http://www.hwlantian.com/sky-user/v1/devices";
    private final String READ_DATA_URL = "http://www.hwlantian.com/sky-data/v1";

    private String mSessionId = null;
    private String mUsername;
    private String password;

    public HanvonOnlineReader(DataReaderListener listener, String username, String pass) {
        super(listener);

        this.mUsername = username;
        this.password = pass;
    }

    /**
     * Do authentication and get sessionId.
     *  null if authenticationFail.
     *
     */
    private String doAuthentication() {
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(password)) return null;


        boolean emailAuth = mUsername.contains("@");

        HttpRequest request = HttpRequest.post(emailAuth ? AUTH_URL_EMAIL : AUTH_URL_PHONE,
                                               true,
                                               emailAuth ? "email" : "phone", mUsername,
                                               "password", md5(password));

        if (request.ok()) {
            String result = request.body();
            try {
                JSONObject jsb = new JSONObject(result);
                if ("success".equalsIgnoreCase((String) jsb.get("p"))) {
                    return (String) jsb.get("sessionId");
                } else {
                    return null;
                }

            } catch (Throwable throwable) {
                return null;
            }
        }

        return null;
    }

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

    @Override
    protected Result doRequestData() {
        if (mSessionId == null) {
            mSessionId = doAuthentication();
        }

        if (mSessionId == null) {
            return new Result("authentication fail!", Result.CODE_FAIL); //TODO: should callback to UI with request fail message
        }

        HanvonDevice deviceObj = null;
        HttpRequest listDeviceReq = HttpRequest.get(LIST_DEVICE_URL, true,
                "sessionId", mSessionId);
        if (listDeviceReq.ok()) {
            try {
                JSONObject js = new JSONObject(listDeviceReq.body());
                List<HanvonDevice> list = null;
                if (js.has(HanvonKey.JS_A_DEVICES)) {
                    list = EnvDataCreator.parseHanvonDevice(js.getString(HanvonKey.JS_A_DEVICES));
                }

                if (list.size() > 0) {
                    //TODO: only take the first one.
                    deviceObj = list.get(0);
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (deviceObj == null) {
            return new Result("cannot get device", Result.CODE_FAIL); //TODO: callback to UI telling get device failed.
        }

        HttpRequest getDataReq = HttpRequest.get(READ_DATA_URL, true,
                HanvonKey.JS_S_SESSIONID, this.mSessionId,
                HanvonKey.JS_S_DEVICE_ID, deviceObj.getDevId(),
                HanvonKey.JS_S_SERIES, deviceObj.getSeries());
        if (getDataReq.ok()) {
            try {
                EnvData data =  EnvDataCreator.parseHanvonData(getDataReq.body());
                this.notifyNewData(data);
                return new Result(data.toString(), Result.CODE_NORMAL);
            } catch (Throwable throwable) {
                //TODO: callback to UI telling read data fail.
            }

        }

        return new Result("No data!", Result.CODE_FAIL);
    }
}
