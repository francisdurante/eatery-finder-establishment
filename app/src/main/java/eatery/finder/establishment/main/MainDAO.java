package eatery.finder.establishment.main;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import eatery.finder.establishment.Api.Api;
import static eatery.finder.establishment.Constant.*;
import static eatery.finder.establishment.Utility.*;
public class MainDAO {


    public static void submitCategory(String categoryName, String est_id, final MainVO vo, final Context context) {
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("cat_name", categoryName);
        rp.add("pass", "est_category");
        rp.add("added_by", est_id);
        api.getByUrl(SUBMIT_CATEGORY, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String status = object.getString("status");
                    switch (status) {
                        case "success":
                            vo.setSubmitCategoryStatus(1);
                            break;
                        case "existing":
                            vo.setSubmitCategoryStatus(2);
                            break;
                        case "fail":
                            vo.setSubmitCategoryStatus(3);
                            break;
                    }
                } catch (JSONException e) {
                    showAlertDialogBox(e.getMessage(), ERROR, context, ERROR);
                }
            }
        });
    }

    public static void getEstInformation(String estName, final MainVO vo, final Context contex) {
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_all_est_user");
        rp.add("key", estName);
        rp.add("filter", "");
        api.getByUrl(GET_EST_INFORMATION, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String responseStatus = object.getString("status");
                    if ("success".equals(responseStatus)) {
                        getEstType("active",contex,vo);
                        JSONArray datas = new JSONArray(object.getString("data"));
                        JSONObject data = datas.getJSONObject(0);
                        vo.setAge(data.getString("good_at_of"));
                        vo.setEmotion(data.getString("good_for_emotion_of"));
                        vo.setEstAddress(data.getString("address"));
                        vo.setEstId(data.getString("id"));
                        vo.setEstName(data.getString("establishment_name"));
                        vo.setEstUserId(data.getString("establishment_user_id"));
                        vo.setFrontStoreUrl(picUrl(data.getString("est_front_store")));
                        vo.setLocationLat(data.getString("location_latitude"));
                        vo.setLocationLon(data.getString("location_longitude"));
                        vo.setEstTypeName(data.getString("est_type_name"));
                    } else {
                        showAlertDialogBox(FAIL_GETTING_INFORMATION, ERROR, contex, ERROR);
                    }
                } catch (JSONException e) {
                    showAlertDialogBox(e.getMessage(), ERROR, contex, ERROR);
                }
            }
        });
    }

    public static void getEstType(String status, final Context context, final MainVO vo) {
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("pass", "get_est_type");
        rp.add("status", status);

        api.getByUrl(EST_TYPE, rp, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String responseStatus = object.getString("status");
                    if("success".equals(responseStatus)){
                        JSONArray datas = new JSONArray(object.getString("data"));
                        String[] estTypeData = new String[datas.length()];
                        for(int x = 0 ; x < datas.length(); x++){
                            estTypeData[x] = datas.getJSONObject(x).getString("est_type_name");
                        }
                        vo.set_estTypeName(estTypeData);
                    }
                } catch (JSONException ex){
                    showAlertDialogBox(ex.getMessage(),ERROR,context,ERROR);
                }
            }

            ;
        });
    }
}