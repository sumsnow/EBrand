package com.sam.ebrand.meetingNetwork.http.app;

import android.os.AsyncTask;
import android.util.Log;

import com.sam.ebrand.manage.SettingManager;
import com.sam.ebrand.meetingNetwork.ParamDown;
import com.sam.ebrand.meetingNetwork.ParamUp;
import com.sam.ebrand.meetingNetwork.http.HttpPlatform;
import com.sam.ebrand.meetingNetwork.http.logic.HttpApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam on 2016/12/5.
 */

public class VGAOutRequestTask extends AsyncTask<String[], Void, ParamDown>
{
    public static final String TAG = "VGAOutRequest";
    private OnResultListener mListener;

    public VGAOutRequestTask(final OnResultListener mListener) {
        this.mListener = mListener;
    }

    private String MakeJsonContentParam(final String s, final String s2) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("mgID", (Object) SettingManager.getInstance().readSetting("mgID", "", "").toString());
        jsonObject.put("com_in", (Object)s);
        jsonObject.put("com_out", (Object)s2);
        return jsonObject.toString();
    }

    protected ParamDown doInBackground(final String[]... array) {
        final String[] array2 = array[0];
        final ParamUp paramUp = new ParamUp();
        paramUp.cookieAction = 2;
        paramUp.type = "sendcom";
        paramUp.contentType = 0;
        while (true) {
            try {
                paramUp.content = this.MakeJsonContentParam(array2[0], array2[1]);
                paramUp.type = HttpApp.GetURLFromType(paramUp.type);
                return HttpPlatform.HttpPost(paramUp);
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }

    protected void onCancelled() {
        this.mListener.CancelListener();
        super.onCancelled();
    }

    protected void onPostExecute(final ParamDown paramDown) {
        super.onPostExecute(paramDown);
        if (this.mListener == null) {
            return;
        }
        if (paramDown == null) {
            this.mListener.ResultListener(-1, "无网络连接。");
            return;
        }
        final String replace = paramDown.result.replace("\r\n\r\n\r\n", "");
        int int1 = -1;
        String string = "";
        while (true) {
            try {
                final JSONObject jsonObject = new JSONObject(replace);
                int1 = jsonObject.getInt("code");
                string = jsonObject.getString("msg");
                Log.e("VGAOutRequest", "code:" + int1 + ",msg:" + string);
                this.mListener.ResultListener(int1, string);
            }
            catch (JSONException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }

    public interface OnResultListener
    {
        void CancelListener();

        void ResultListener(final int p0, final String p1);
    }
}
