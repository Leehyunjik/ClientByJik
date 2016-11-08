package univ.anyang.bindingactivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LEEHYUNJIK on 2016-10-07.
 */

public class SearchJson_Parser {
    String num;
    ArrayList<MyJson> list;
    MyJson mJson;
    public SearchJson_Parser(String sJson){
        list = new ArrayList<MyJson>();

        try{
            JSONObject sub = new JSONObject(sJson);
            JSONArray ja = sub.getJSONArray("SEARCH_RESULT");

            for(int i=0;i<ja.length();i++)
            {
                JSONObject jo1 = ja.getJSONObject(i);
                num = jo1.getString("SUBJECT_NUM");

                JSONArray ja2 = jo1.getJSONArray("SUBJECT");
                for(int j=0;j<ja2.length();j++)
                {
                    JSONObject jo2 = ja2.getJSONObject(j);
                    mJson = new MyJson(jo2.getString("CLASS_NAME"),jo2.getString("CLASS_NO"),jo2.getString("CLASS_ID"),jo2.getString("CLASS_ROOM"),jo2.getString("WEEK"),jo2.getString("CTIME"));
                    list.add(mJson);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
