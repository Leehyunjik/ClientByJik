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
import android.os.RemoteException;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;


public class attendActivity extends Activity implements BeaconConsumer{

    private BeaconManager beaconManager;
    // 감지된 비콘들을 임시로 담을 리스트
    private ArrayList<Beacon> beaconList = new ArrayList<>();


    LocalService mService;
    boolean mBound = false;
    Intent intent;
    Intent SearchIntent;
    Button btn;
    TextView textView;
    String Search_str=null;
    SearchJson_Parser sJson;

    static Handler atthandler;
    static String Message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //타이틀바 x
        setContentView(R.layout.activity_attend);

        intent = new Intent(this, LocalService.class);
        SearchIntent =getIntent();
        Search_str=SearchIntent.getExtras().getString("Search_str");
        textView = (TextView)findViewById(R.id.Textview);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(),"받아온 intent : "+Search_str,Toast.LENGTH_LONG).show();

        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);

        sJson=new SearchJson_Parser(Search_str);

        MyJsonAdapter adapter = new MyJsonAdapter(getApplicationContext(),R.layout.item,sJson.list);

        ListView list;
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        atthandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                switch(hdmsg.what){
                    case 101:
                        SearchObject searchobj = new SearchObject();
                        searchobj = (SearchObject)hdmsg.obj;
                        //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        try {
                            Json_Maker jMaker= new Json_Maker(beaconList,searchobj.classcode,searchobj.classno,searchobj.classroom,searchobj.week,searchobj.ctime);
                            Message = jMaker.str;
                            Toast.makeText(getApplicationContext(),jMaker.str,Toast.LENGTH_SHORT).show();
                            Intent newintent = new Intent(getBaseContext(), attend2Activity.class);
                            newintent.putExtra("B_Json",jMaker.str);
                            startActivityForResult(newintent,1001);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
        };
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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

}
