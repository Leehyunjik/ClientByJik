package univ.anyang.bindingactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static univ.anyang.bindingactivity.LoginActivity.msghandler;

public class HomeActivity extends Activity {


    LocalService mService;
    boolean mBound = false;
    String Search_str=null;
    Button btn;
    ImageView imgbtn_attend;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //btn = (Button)findViewById(R.id.button2);
        imgbtn_attend = (ImageView)findViewById(R.id.img_attend);
        intent = new Intent(this, LocalService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);


        /*
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    String Str_search = "{\"SEARCH\":["
                            +"]}";
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    mService.sendMsg(Str_search);

                }

            }
        });
        */
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                switch(hdmsg.what){
                    case 2:
                   //     Toast.makeText(getApplicationContext(),hdmsg.obj.toString(),Toast.LENGTH_LONG).show();
                        Search_str=hdmsg.obj.toString();
                        /* ★★★★★★★★★★★★
                            hdmsg.obj.toString => 서버가 날려준 Search 결과 JSON이 저장되어있음. 이걸 파싱해서
                            동적으로 해당 과목명/ 분반번호/  출력해주고 옆에 출석하기 버튼 만들어주세요
                            ★★★★★★★★★★★★
                         */

                        Intent Searchintent = new Intent(getApplicationContext(), attendActivity.class);
                        Searchintent.putExtra("Search_str",Search_str);
                        startActivityForResult(Searchintent, 1003);
                        break;
                }
            }
        };

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_attend:
                if (mBound) {
                    String Str_search = "{\"SEARCH\":["
                            + "]}";
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    mService.sendMsg(Str_search);
                    break;
                }
        }
    }
    public void logout(View v){
        Intent logoutIntent = new Intent(this,LoginActivity.class);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        stopService(intent);
        startActivity(logoutIntent);
    }

    public void alert(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("알림!!");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "확인 버튼이 눌렸습니다",Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "취소 버튼이 눌렸습니다",Toast.LENGTH_SHORT).show();
            }
        });
        alert.setMessage("안녕하십니까? 알림 예제소스 학습중 입니다");
        alert.show();
    }

    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (!mBound) {
            bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
            mBound = false;
        }
    }
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
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
