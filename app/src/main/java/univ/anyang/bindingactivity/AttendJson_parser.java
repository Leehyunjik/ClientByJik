package univ.anyang.bindingactivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LEEHYUNJIK on 2016-10-28.
 */

public class AttendJson_parser {


    String Date;
    String BeaconFlag;
    String AttendFlag;
    String ClassCode;
    String ClassNo;
    String ClassName;
    String CurrentTime;
    String Week;


    public AttendJson_parser(String attJson){
        try{
            JSONObject attend = new JSONObject(attJson);
            JSONArray ja = attend.getJSONArray("ATTEND_RESULT");

            for(int i=0;i<ja.length();i++)
            {
                JSONObject jo1 = ja.getJSONObject(i);

                ClassNo = jo1.getString("CLASS_NO");
                BeaconFlag = jo1.getString("BEACON_FLAG");
                CurrentTime = jo1.getString("CTIME");
                Date = jo1.getString("TIME");
                AttendFlag = jo1.getString("ATTEND_FLAG");
                Week = jo1.getString("WEEK");
                ClassCode = jo1.getString("CLASS_ID");
                ClassName = jo1.getString("CLASS_NAME");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getClassNo()
    {
        return ClassNo;
    }
}
