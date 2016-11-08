package univ.anyang.bindingactivity;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LEEHYUNJIK on 2016-10-07.
 * 어뎁터 참조 사이트 http://itmir.tistory.com/477
 * 어뎁터는 리스트 하나의 뷰들을 구성하는 데이터가 있잖아
 * 그 데이터를 리스트에 넣어주는 중개자 역할을 해
 * 즉, 어뎁터를 통해 데이터를 뿌려주는거야
 */

 class MyJsonAdapter extends BaseAdapter{
    Context con;
    LayoutInflater inflater;
    ArrayList<MyJson> arJ;
    int layout;
    static String class_name;
    static String class_room;


    public MyJsonAdapter(Context context, int alayout, ArrayList<MyJson> aarJ){
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arJ = aarJ;
        layout = alayout;
    }

    //어뎁터 항목 갯수 조사
    @Override
    public int getCount() {
        return arJ.size();
    }

    //position 위치의 항목 Name 반환
    @Override
    public Object getItem(int position) {
        return arJ.get(position).Name;
    }

    //position 위치의 항목 ID반환
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(layout,parent,false);
        }
        TextView txt = (TextView)convertView.findViewById(R.id.txt);
        txt.setText(arJ.get(position).ClassCode+" "+arJ.get(position).Name + "  분반 : "+arJ.get(position).Num +"  강의실 : "+ arJ.get(position).ClassRoom );
        final SearchObject searchobj=new SearchObject(arJ.get(position).ClassCode, arJ.get(position).Num,arJ.get(position).ClassRoom,arJ.get(position).week,arJ.get(position).ctime);
        Button btn = (Button)convertView.findViewById(R.id.btn);
        btn.setText("출석");
        btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //String str = arJ.get(position).ClassCode;
                //Toast.makeText(con,str, Toast.LENGTH_SHORT).show();
                class_room=arJ.get(position).ClassRoom;
                class_name = arJ.get(position).Name;
                Message hdmsg = attendActivity.atthandler.obtainMessage();
                hdmsg.what = 101;
                hdmsg.obj = searchobj;
                attendActivity.atthandler.sendMessage(hdmsg);

            }
        });
        return convertView;
    }
}
