package univ.anyang.bindingactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

// 바인딩으로 서비스를 시작하면 StartCommand 가 실행되지 않는 문제점 .

public class LocalService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    Socket socket;
    SocketClient client;
    ReceiveThread receive;
    SendThread send;

    // 서비스 생성시 딱 1번만 실행되는 onCreate 문
    public void onCreate() {
        super.onCreate();
        client = new SocketClient("220.66.60.204","8888");
        client.start();
        Toast.makeText(getBaseContext(),"서비스(onCreate)시작",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(),"서비스(onStartCommand)시작",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
    public String getTestMSG() {
        String msg = "원배굿";
        return msg;

    }

    public void sendMsg(String msg) {
        send = new SendThread(socket,msg);
        send.start();
    }


    class SocketClient extends Thread {
        boolean threadAlive;
        String ip;
        String port;
        String mac;

        //InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(ip, Integer.parseInt(port));
                //inputStream = socket.getInputStream();
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class ReceiveThread extends Thread {
        private Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try{
                input = new DataInputStream(socket.getInputStream());
            }catch(Exception e){
            }
        }
        // 메세지 수신후 Handler로 전달
        public void run() {
            try {
                while (input != null) {

                    String msg = input.readUTF();
                    if (msg != null) {
                        Log.d(ACTIVITY_SERVICE, msg);
                        Intent intent = new Intent(getApplicationContext(), LocalService.class);
                        Message hdmsg;
                        hdmsg = LoginActivity.msghandler.obtainMessage();
                        if(msg.contains("LOGIN_RESULT"))
                        {

                            hdmsg.what = 1;
                            hdmsg.obj = msg;
                            LoginActivity.msghandler.sendMessage(hdmsg);
                        }
                        else if(msg.contains("SEARCH_RESULT"))
                        {
                            hdmsg.what = 2;
                            hdmsg.obj = msg;
                            LoginActivity.msghandler.sendMessage(hdmsg);

                        }
                        else if(msg.contains("ATTEND"))
                        {
                            hdmsg.what = 3;
                            hdmsg.obj = msg;
                            LoginActivity.msghandler.sendMessage(hdmsg);
                        }
                        //intent.putExtra("login",msg);
                        //Message hdmsg = LoginActivity.msghandler.obtainMessage();



                        /*
                        이게 좀 중요함
                        LoginActivity 에서 msghandler를 static으로 선언한다음에
                        서비스.class에서 가져다 사용한다음에 여기서 내가
                        하고싶은대로 온갖 메시지를 다 보낼 수 있음 .
                        */

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class SendThread extends Thread {
        private Socket socket;

        String sendmsg = "";
        DataOutputStream output;

        public SendThread(Socket socket , String msg) {
            this.socket = socket;
            this.sendmsg = msg;
            try {
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
            }
        }

        public void run() {

            try {
                // 메세지 전송부 (누군지 식별하기위한 방법으로 mac를 사용)
                Log.d(ACTIVITY_SERVICE, "11111");
                //String mac = null;
                if (output != null) {
                    if (sendmsg != null) {
                        output.writeUTF(this.sendmsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();

            }
        }
    }

}
