package eatery.finder.establishment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import eatery.finder.establishment.main.MainDAO;
import eatery.finder.establishment.main.MainVO;

import static eatery.finder.establishment.Constant.*;
import static eatery.finder.establishment.Utility.*;

public class ActivityMain extends AppCompatActivity {
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        Button editProfile = findViewById(R.id.edit_profile);
        Button editMenuItem = findViewById(R.id.edit_menu_item);
        Button editCategory = findViewById(R.id.edit_categories);
        Button addCategory = findViewById(R.id.add_category);
        Button addMenu = findViewById(R.id.add_menu);
        Button viewMenu = findViewById(R.id.view_menu);
        ImageView frontStore = findViewById(R.id.front_store);

        Picasso.with(mContext)
                .load(picUrl(Utility.getString("est_front_store_url",mContext)))
                .placeholder(R.drawable.default_image_thumbnail)
                .error(R.drawable.default_image_thumbnail)
                .into(frontStore);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainVO vo = new MainVO();
                MainDAO.getEstInformation(Utility.getString("est_name",mContext),vo,mContext);
                showProgressBar(GETTING_INFORMATION,mContext);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                        dialogBuilder.setTitle(EDIT_PROFILE_DIALOG_TITLE);
                        dialogBuilder.setIcon(R.drawable.ic_user);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.edit_profile, null);
                        EditText estName = dialogView.findViewById(R.id.est_name_edit);
                        EditText estAddress = dialogView.findViewById(R.id.est_address_edit);
                        EditText estLat = dialogView.findViewById(R.id.latitude_edit);
                        EditText estLon = dialogView.findViewById(R.id.longitude_edit);
                        Spinner emotion = dialogView.findViewById(R.id.emotion_edit);
                        Spinner age = dialogView.findViewById(R.id.age_edit);
                        Spinner estTypeName = dialogView.findViewById(R.id.est_type_edit);
                        ImageView frontPic = dialogView.findViewById(R.id.front_store_edit);

                        estName.setText(vo.getEstName());
                        estAddress.setText(vo.getEstAddress());
                        estLat.setText(vo.getLocationLat());
                        estLon.setText(vo.getLocationLon());

                        ArrayAdapter<CharSequence> emotionList = ArrayAdapter.createFromResource(mContext, R.array.emotion_item, android.R.layout.simple_spinner_item);
                        ArrayAdapter<CharSequence> ageList = ArrayAdapter.createFromResource(mContext, R.array.age_range, android.R.layout.simple_spinner_item);
                        ArrayAdapter<CharSequence> estTypeList = new ArrayAdapter(mContext,android.R.layout.simple_spinner_item,vo.get_estTypeName());

                        estTypeName.setAdapter(estTypeList);
                        estTypeName.setSelection(estTypeList.getPosition(vo.getEstTypeName()));
                        emotion.setSelection(emotionList.getPosition(vo.getEmotion()));
                        age.setSelection(ageList.getPosition(vo.getAge()));

                        Picasso.with(mContext)
                                .load(vo.getFrontStoreUrl())
                                .error(R.drawable.default_image_thumbnail)
                                .placeholder(R.drawable.default_image_thumbnail).into(frontPic);


                        dialogBuilder.setView(dialogView);
                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        hideProgressBar();
                    }
                }.start();
            }
        });
        editMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect to editMenu
            }
        });
        editCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(ALERT_TITLE_ADD_CATEGORY);
                alert.setMessage(ALERT_MESSAGE_ADD_CATEGORY);
                alert.setIcon(R.drawable.ic_category_add_icon);
                LinearLayout container = new LinearLayout(mContext);
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(35, 10, 35, 10);
                final EditText category = new EditText (mContext);
                category.setBackground(getResources().getDrawable(R.drawable.text_background_design));
                category.setLayoutParams(lp);
                category.setHint(CATEGORY_HINT);
                category.setHintTextColor(Color.parseColor("#000000"));
                category.setPadding(20,10,20,10);
                container.addView(category);
                alert.setView(container);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String categoryName = category.getText().toString();
                        if(!"".equals(categoryName)){
                            showProgressBar(ADDING,mContext);
                            final MainVO vo = new MainVO();
                            MainDAO.submitCategory(categoryName,Utility.getString("est_user_id",mContext),vo,mContext);
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    switch(vo.getSubmitCategoryStatus()){
                                        case 1 : // success
                                            showAlertDialogBox(ADD_CATEGORY_SUCCESS,SUCCESS,mContext,SUCCESS);
                                            break;
                                        case 2 : // existing
                                            showAlertDialogBox(ADD_CATEGORY_EXISTING,WARNING,mContext,WARNING);
                                            break;
                                        case 3 : // fail
                                            showAlertDialogBox(ADD_CATEGORY_FAIL,WARNING,mContext,WARNING);
                                            break;
                                        default:
                                            showAlertDialogBox(CHECK_CONNECTION,ERROR,mContext,ERROR);
                                            break;
                                    }
                                    hideProgressBar();
                                }
                            }.start();
                        }else{
                            showToastMessageLong(COMPLETE_FIELD_VALIDATION,mContext);
                        }
                    }
                });
                alert.show();
            }
        });

        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect tot add menu
            }
        });
        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect to View menu
            }
        });
    }
}
