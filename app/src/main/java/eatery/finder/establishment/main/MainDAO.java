package eatery.finder.establishment.main;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import eatery.finder.establishment.Api.Api;
import eatery.finder.establishment.Constant;

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
        });
    }

    public static void submitEditedProfile(final MainVO vo,String path, final Context context){
        Api api = new Api();
        String[] allowedContentTypes = new String[] { "*/*", "application/pdf", "image/png", "image/jpeg" };
        RequestParams rp = new RequestParams();
        rp.add("est_name",vo.getEstName());
        rp.add("lat",vo.getLocationLat());
        rp.add("lon",vo.getLocationLon());
        rp.add("emotion",vo.getEmotion());
        rp.add("age",vo.getAge());
        rp.add("est_type",vo.getEstTypeName());
        rp.add("address",vo.getEstAddress());
        rp.add("est_id",vo.getEstId());
        rp.add("pass","submit_edit_est");
        if(!"".equals(path)){
            RequestParams params = new RequestParams();
            try {
                params.put("est_name",vo.getEstName());
                params.put("lat",vo.getLocationLat());
                params.put("lon",vo.getLocationLon());
                params.put("emotion",vo.getEmotion());
                params.put("age",vo.getAge());
                params.put("est_type",vo.getEstTypeName());
                params.put("address",vo.getEstAddress());
                params.put("est_id",vo.getEstId());
                params.put("pass","submit_edit_est");
                params.put("imageOne",new File(path));
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(SUBMIT_EDITED_PROFILE,params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject object = new JSONObject(response.toString());
                        String responseStatus = object.getString("status");
                        if ("success".equals(responseStatus)) {
                            vo.setSubmitEditProfileStatus(1);
                        } else {
                            vo.setSubmitCategoryStatus(2);
                        }
                    }catch(JSONException e){
                        showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                }
            });

        }else {
            api.postByUrl(SUBMIT_EDITED_PROFILE, rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONObject object = new JSONObject(response.toString());
                        String responseStatus = object.getString("status");
                        if ("success".equals(responseStatus)) {
                            vo.setSubmitEditProfileStatus(1);
                        } else {
                            vo.setSubmitCategoryStatus(2);
                        }
                    } catch (JSONException e) {
                        showAlertDialogBox(e.getMessage(), ERROR, context, ERROR);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                }
            });
        }
    }
    public static void getProductNameByEstId(final String estId, final MainVO vo, final Context context, final String status){
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("for_process",status);
        rp.add("pass","get_product");
        rp.add("id",estId);

        api.getByUrl(GET_EST_ITEM_NAME,rp,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String responseStatus = object.getString("status");
                    if("success".equals(responseStatus)){
                        JSONArray datas = new JSONArray(object.getString("data"));
                        String[] estProducts = new String[datas.length()];
                        for(int x = 0; x < datas.length(); x++){
                            estProducts[x] = datas.getJSONObject(x).getString("item_name");
                        }
                        vo.set_itemNameList(estProducts);
                        getCategoryByEstId(estId,vo,context,status);
                    }
                }catch (JSONException e){
                    showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });

    }
    public static void getCategoryByEstId(String estId, final MainVO vo, final Context context, String status){
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("for_process",status);
        rp.add("pass","est_get_category");
        rp.add("id",estId);

        api.getByUrl(GET_CATEGORY_NAME,rp,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String responseStatus = object.getString("status");
                    System.out.println(responseStatus + " aaaaaaaaaaaa");
                    if("success".equals(responseStatus)){
                        JSONArray datas = new JSONArray(object.getString("data"));
                        String[] categoryName = new String[datas.length()];
                        for(int x = 0; x < datas.length(); x++){
                            categoryName[x] = datas.getJSONObject(x).getString("category_name");
                            System.out.println(categoryName[x] + " aaaaaaaaaaaa");
                        }
                        vo.set_itemCategory(categoryName);
                    }
                }catch (JSONException e){
                    showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }
    public static void getItemInformation(String estId, String itemName, final MainVO vo, final Context context){
        Api api = new Api();
        RequestParams rp = new RequestParams();
        rp.add("pass","get_product");
        rp.add("est_id",estId);
        rp.add("item_name",itemName);

        api.getByUrl(GET_ITEM_INFORMATION,rp,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    String responseStatus = object.getString("status");
                    System.out.println(response.toString() + " aaaaaaaaaaaaaa");
                    if("success".equals(responseStatus)){
                        JSONObject data = object.getJSONObject("data");
                        vo.setItemId(data.getString("item_id"));
                        vo.setItemName(data.getString("item_name"));
                        vo.setItemPicPath(picUrl(data.getString("path")));
                        vo.setPrice(data.getInt("price"));
                        vo.setItemStatus(data.getString("item_status").equals("1")?"ACTIVE":"INACTIVE");
                        vo.setItemCategory(data.getString("category_name"));
                    }
                }catch (JSONException e)
                {
                    showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
                }
            }
        });
    }

    public static void submitEditItemMenu(final MainVO vo, String estId, String path, final Context context,int ItemId){
        int upload = 0;
        if(!"".equals(path)) {
            upload = 1;
        }
            try {
                RequestParams rp = new RequestParams();
                rp.put("id", ItemId);
                rp.put("cat_name", vo.getItemCategory());
                rp.put("status", vo.getItemStatus());
                rp.put("est_id", estId);
                rp.put("item_name", vo.getItemName());
                rp.put("price", vo.getPrice());
                rp.put("pass", "submit_edited_product");
                rp.put("upload", upload);
                if(upload == 1) {
                    rp.put("imageOne", new File(path));
                }
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(SUBMIT_EDITED_ITEM, rp,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject object = new JSONObject(response.toString());
                            String responseStatus = object.getString("status");
                            System.out.println(response.toString() + " aaaaaaaaaaaaa");
                            if ("success".equals(responseStatus)) {
                                vo.setSubmitItemStatus(1);
                            } else {
                                vo.setSubmitItemStatus(2);
                            }
                        }catch(JSONException e){
                            showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    }
                });

            }catch (FileNotFoundException e){
                showAlertDialogBox(e.getMessage(),ERROR,context,ERROR);
            }
        }
    }