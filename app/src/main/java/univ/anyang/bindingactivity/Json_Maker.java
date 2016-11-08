package univ.anyang.bindingactivity;

import org.altbeacon.beacon.Beacon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LEEHYUNJIK on 2016-10-04.
 */
/*
이름 그대로 비콘 json형식으로 만드는 class임
* */
public class Json_Maker {
    //Beacon_Json bJson = new Beacon_Json();
    //Json_Parser jParser = new Json_Parser(bJson.bJson);

    String str;
    public Json_Maker(ArrayList<Beacon> Beaconlist , String ClassCode, String classno, String classroom, String week, String ctime) throws JSONException {
        JSONObject Jobj2 = new JSONObject();//BEACON의 개체들의 값
        JSONObject Jobj3 = new JSONObject();//BEACON 배열을 저장

        JSONArray jArr1 = new JSONArray(); //BEACON_INFO를 저장할 배열
        JSONArray jArr2 = new JSONArray(); //BEACON을 저장할 배열
        for(Beacon beacon : Beaconlist) {
            JSONObject Jobj1 = new JSONObject();//BEACON_INFO의 개체들의 값

            Jobj1.put("UUID", beacon.getId1());
            Jobj1.put("MAJOR", beacon.getId2());
            Jobj1.put("MINOR", beacon.getId3());
            Jobj1.put("DISTANCE", beacon.getDistance());
            jArr1.put(Jobj1);
        }
        //Jobj2.put("SID",jParser.SID);
        //Jobj2.put("NUM",jParser.num);
        Jobj2.put("CLASS_CODE", ClassCode);
        Jobj2.put("CLASS_NO", classno);
        Jobj2.put("CLASSROOM", classroom);
        Jobj2.put("WEEK", week);
        Jobj2.put("CTIME", ctime);
        Jobj2.put("BEACON_CNT",Beaconlist.size());
        Jobj2.put("BEACON_INFO",jArr1); //BEACON_INFO : [{ 형태로 저장

        jArr2.put(Jobj2);
        Jobj3.put("BEACON",jArr2);
        str=Jobj3.toString();
    }
}