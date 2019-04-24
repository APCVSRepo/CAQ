package com.ford.cvs.caq.client.data.moji;

import android.util.Log;

import com.ford.cvs.caq.client.HttpRequest;
import com.ford.cvs.caq.client.data.DataReaderListener;
import com.ford.cvs.caq.client.data.RestUtil;
import com.ford.cvs.caq.client.data.ServerDataReader;


/**
 * Created by liuchong on 18/8/2016.
 */
public class MojiPubDataReader extends ServerDataReader {

    public MojiPubDataReader(DataReaderListener... callback) {
        super(callback);
    }
    @Override
    protected Result doRequestData() {
        String response = doRequestMoji();
        if (response != null) {
            for(DataReaderListener listener :mListener) {
                listener.onNewData(response);
            }
        }
        return response == null ? new Result(response, Result.CODE_FAIL) : new Result(response, Result.CODE_NORMAL);
    }

    private String doRequestMoji() {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        //yifili09 -- to remove credentials
        final String token = " ";
        final String password = " ";
        final String url = " ";

        String[] keys = new String[] {"timestamp", "token", "cityId", "key"};
        String[] values = new String[] {
                timestamp,
                token,
                "39", //new_weather_layout for beijing 39 for shanghai
                RestUtil.md5(password + timestamp + "39")
        };

        HttpRequest req = HttpRequest.post(url, true, keys[0], values[0],
                keys[1], values[1],
                keys[2], values[2],
                keys[3], values[3]);

        try
        {
            if (req.ok())
                return req.body();
        }
        catch (HttpRequest.HttpRequestException exception)
        {
            exception.printStackTrace();
        }

        return null;
    }
}
