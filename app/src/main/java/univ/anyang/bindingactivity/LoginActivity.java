package univ.anyang.bindingactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    LocalService mService;
    boolean mBound = false;
    Button Btn;
    Button Btn_send;
    EditText editText_id;
    EditText editText_pw;
    CheckBox cheBox_id;
    CheckBox cheBox_pw;



    static Handler msghandler;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Btn_send = (Button)findViewById(R.id.btn_send);
        editText_id = (EditText)findViewById(R.id.editText_id);
        editText_pw = (EditText)findViewById(R.id.editText_pw);
        cheBox_id = (CheckBox)findViewById(R.id.checkBox_IDsave);
        cheBox_pw = (CheckBox)findViewById(R.id.checkBox_PWsave);

        intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); // LocalService의 메소드를 실행시키기 위해서 바인딩해야함
        editText_id.setText(loadID());  // Sharedpreference로부터 ID정보를 받아옴
        editText_pw.setText(loadPW());  // Sharedpreference로부터 PW정보를 받아옴
        setChecked();                   // Sharedpreference로부터 마지막으로 로그인했을때 체크박스의 상태를 받아옴
        cheBox_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                   cheBox_id.setChecked(true);          // P/W 저장 체크할때 ID저장버튼도 체크
                }
            }
        });




        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                switch(hdmsg.what){
                    case 1:
                        String result=null;
                        JSONArray jsonArr=null;
                        JSONObject Jobj=null;
                        try {
                            Jobj =new JSONObject(hdmsg.obj.toString());
                            jsonArr= new JSONArray(Jobj.getString("LOGIN_RESULT"));
                            for (int i=0 ; i< jsonArr.length();i++){
                                JSONObject insideobj=jsonArr.getJSONObject(i);
                                result=insideobj.getString("LOGFLAG");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                        //  Toast.makeText(getApplicationContext(),hdmsg.obj.toString(),Toast.LENGTH_LONG).show();
                        //if(hdmsg.obj.toString().contains("TRUE")) {
                        if(result.equals("TRUE")){
                            if(cheBox_id.isChecked())                       // ID저장 체크박스가 true
                                saveID(editText_id.getText().toString());   // saveID 메소드
                            else
                                deleteID();                                 // 아닐때 체크박스false , ID삭제

                            if(cheBox_pw.isChecked())                       // PW저장 체크박스가 true
                                savePW(editText_pw.getText().toString());   // savePW 메소드 실행
                            else
                                deletePW();                                 // 아닐때 체크박스false , PW삭제

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivityForResult(intent, 1001);
                            LoginActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
                        }
                        break;
                }
            }
        };




        Btn_send.setOnClickListener(new View.OnClickListener() {
            String Str_LoginJson;
            @Override
            public void onClick(View view) {
                String id = editText_id.getText().toString();
                String pw = editText_pw.getText().toString();
                try
                {
                    LoginJson LJ0 = new LoginJson(id,pw);
                    Str_LoginJson = LJ0.str;

                } catch(JSONException e) { }
                if (mBound) {
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    mService.sendMsg(Str_LoginJson);
                }


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref" , Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();



        // Unbind from the service
        if (!mBound) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            // mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("알림");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService(intent);
                finish();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "취소 버튼이 눌렸습니다",Toast.LENGTH_SHORT).show();
            }
        });
        alert.setMessage("정말 종료하시겠습니까? ");
        alert.show();

    }

    private void saveID(String id) {
        SharedPreferences pref = getSharedPreferences("Information", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("idchecked",true);
        editor.putString("id",id);
        editor.commit();
    }

    private void deleteID() {
        SharedPreferences pref = getSharedPreferences("Information", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("id");
        editor.putBoolean("idchecked",false);
        editor.commit();
    }

    private void savePW(String pw) {
        SharedPreferences pref = getSharedPreferences("Information", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("pwchecked",true);
        editor.putString("pw",pw);
        editor.commit();
    }

    private void deletePW() {
        SharedPreferences pref = getSharedPreferences("Information", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("pw");
        editor.putBoolean("pwchecked",false);
        editor.commit();
    }


    private String loadID() {
        SharedPreferences pref = getSharedPreferences("Information",Activity.MODE_PRIVATE);
        String id = pref.getString("id","");

        return id;
    }
    private String loadPW() {
        SharedPreferences pref = getSharedPreferences("Information",Activity.MODE_PRIVATE);
        String pw = pref.getString("pw","");

        return pw;
    }

    private void setChecked() {
        SharedPreferences pref = getSharedPreferences("Information", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        boolean bool_id = pref.getBoolean("idchecked",false);
        boolean bool_pw = pref.getBoolean("pwchecked",false);
        if(bool_id) {
            cheBox_id = (CheckBox) findViewById(R.id.checkBox_IDsave);
            cheBox_id.setChecked(true);
        }
        if(bool_pw) {
            cheBox_pw = (CheckBox) findViewById(R.id.checkBox_PWsave);
            cheBox_pw.setChecked(true);
        }
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
