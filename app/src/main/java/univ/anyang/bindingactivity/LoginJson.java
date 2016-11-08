package univ.anyang.bindingactivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016-10-05.
 */

public class LoginJson {
    String str;
    public LoginJson(String id,String pw) throws JSONException {


        JSONObject Jobj = new JSONObject();
        JSONObject Jobj2 = new JSONObject();

        Jobj.put("ID",id);
        Jobj.put("PW",pw);

        JSONArray List = new JSONArray();
        List.put(Jobj);

        Jobj2.put("Login",List);

        str=Jobj2.toString();
    }
}
