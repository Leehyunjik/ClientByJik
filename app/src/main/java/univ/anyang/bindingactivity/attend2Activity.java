package univ.anyang.bindingactivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static univ.anyang.bindingactivity.LoginActivity.msghandler;


public class attend2Activity extends Activity{

    LocalService mService;
    boolean mBound = false;
    Intent intent;

    String B_Json=null;
    TextView txtview_attend;
    Button btn_ok , btn_cancel , btn_close;
    ProgressBar progressBar;
    AttendJson_parser aJp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_attend2);

        Intent msgintent;
        msgintent = getIntent();

        B_Json=msgintent.getExtras().getString("B_Json");
        Toast.makeText(getApplicationContext(),B_Json,Toast.LENGTH_SHORT).show();
        intent = new Intent(this,LocalService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);

        setLayout();        // layout으로부터 정보 받아옴.(getfindViewbyid)

        txtview_attend.setText("강의명 : "+MyJsonAdapter.class_name+"\n"+          // 강의명과 강의실을 레이아웃에 띄움
        "강의실 : "+MyJsonAdapter.class_room);

        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                switch (hdmsg.what) {
                    case 3:
                        Toast.makeText(getApplicationContext(),hdmsg.obj.toString(),Toast.LENGTH_LONG).show(); //파싱 되는거 확인
                        aJp=new AttendJson_parser(hdmsg.obj.toString());
                        Toast.makeText(getApplicationContext(),aJp.ClassNo,Toast.LENGTH_LONG).show(); //파싱 되는거 확인
                        Toast.makeText(getApplicationContext(),"BF: "+aJp.BeaconFlag,Toast.LENGTH_LONG).show(); //파싱 되는거 확인
                        Toast.makeText(getApplicationContext(),"AF: "+aJp.AttendFlag,Toast.LENGTH_LONG).show(); //파싱 되는거 확인

                        if(aJp.BeaconFlag.equals("1"))  // 비콘센서안에있을때
                        {
                            btn_ok.setVisibility(View.INVISIBLE);
                            btn_cancel.setVisibility(View.INVISIBLE);
                            btn_close.setVisibility(View.VISIBLE);
                            if(aJp.AttendFlag.equals("00"))
                            {   // 출석처리됨
                                txtview_attend.setText("출석이 확인되었습니다.");
                            }
                            else if(aJp.AttendFlag.equals("01"))
                            {   // 지각처리됨
                                txtview_attend.setText("(지각)출석이 확인되었습니다.");
                            }
                            else if(aJp.AttendFlag.equals("10"))
                            {   // 유고처리됨

                            }
                            else if(aJp.AttendFlag.equals("11"))
                            {   // 결석처리됨

                            }
                        }
                        else if(aJp.BeaconFlag.equals("2"))     // 거리밖에 비콘 감지
                        {
                            btn_ok.setVisibility(View.INVISIBLE);
                            btn_cancel.setVisibility(View.INVISIBLE);
                            btn_close.setVisibility(View.VISIBLE);
                            txtview_attend.setText("Beacon Flag : 2");
                        }
                        else if(aJp.BeaconFlag.equals("3"))     // 비콘을 찾을 수 없음
                        {
                            btn_ok.setVisibility(View.INVISIBLE);
                            btn_cancel.setVisibility(View.INVISIBLE);
                            btn_close.setVisibility(View.VISIBLE);
                            txtview_attend.setText("Beacon Flag : 3");
                        }
                        break;
                }
            }
        };


        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            Toast.makeText(getApplicationContext(),"전송완료",Toast.LENGTH_LONG).show();
            mService.sendMsg(attendActivity.Message);
        }

    }
    public void onClicked(View v) {
        switch(v.getId()) {
            case R.id.btn_ok:                                               //확인버튼을 눌렀을 때
                if (mBound) {                                               //B_Json을 서버로 전송함
                                                                            //56line에서 핸들러로 데이터를 받음
                    // Call a method from the LocalService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    mService.sendMsg(B_Json);
                }
                progressBar.setVisibility(v.VISIBLE);                        //확인버튼 누를때 프로그레스바Visible을 true로
                break;
            case R.id.btn_cancel:                                           //취소버튼을 눌렀을때
                finish();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }


    public void sendMsg(String msg) {
        if(mBound)
        {
            Toast.makeText(getApplicationContext(),"전송완료",Toast.LENGTH_LONG).show();
            mService.sendMsg(msg);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (!mBound) {
            unbindService(mConnection);
            bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
            //mBound = false;
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
    private void setLayout() {
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_close = (Button)findViewById(R.id.btn_close);
        txtview_attend = (TextView)findViewById(R.id.TextView_attend);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
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
