package eatery.finder.establishment;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import eatery.finder.establishment.login.LoginDAO;
import eatery.finder.establishment.login.LoginVO;

import static eatery.finder.establishment.Utility.*;
import static eatery.finder.establishment.Constant.*;

public class ActivityLogin extends AppCompatActivity {

    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        Button loginButton = findViewById(R.id.button_login);
        final EditText username = findViewById(R.id.username_login);
        final EditText password = findViewById(R.id.password_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordText = password.getText().toString();
                String usernameText = username.getText().toString();
                if("".equals(usernameText) || "".equals(passwordText)){
                    showToastMessageShort(COMPLETE_FIELD_VALIDATION,mContext);
                }else{
                    final LoginVO vo = new LoginVO();
                    showProgressBar(LOGGING_IN,mContext);
                    LoginDAO.login(usernameText,passwordText,vo);
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            switch(vo.getLoginStatus()){
                                case 1 : //success
                                    /////
                                    save("est_age",vo.getAge(),mContext);
                                    save("est_emotion",vo.getEmotion(),mContext);
                                    save("est_address",vo.getEstAddress(),mContext);
                                    save("est_id",vo.getEstId(),mContext);
                                    save("est_name",vo.getEstName(),mContext);
                                    save("est_user_id",vo.getEstUserId(),mContext);
                                    save("est_front_store_url",vo.getFrontStoreUrl(),mContext);
                                    save("id",vo.getId(),mContext);
                                    save("est_location_lat",vo.getLocationLat(),mContext);
                                    save("est_location_lon",vo.getLocationLon(),mContext);
                                    save("est_est_type_id",Integer.toString(vo.getEstTypeId()),mContext);
                                    startActivity(new Intent(mContext,ActivityMain.class));
                                    finish();
                                    break;
                                case 2 : //deactivated
                                    showAlertDialogBox(DEACTIVATED_ACCOUNT,WARNING,mContext,WARNING);
                                    break;
                                case 3 : //wrong password
                                    showAlertDialogBox(WRONG_PASSWORD_OR_NOT_EXIST,WARNING,mContext,WARNING);
                                    break;
                                default :
                                     showAlertDialogBox(CHECK_CONNECTION,ERROR,mContext,ERROR);
                                     break;
                            }
                            hideProgressBar();
                        }
                    }.start();
                }
            }
        });
    }

}
