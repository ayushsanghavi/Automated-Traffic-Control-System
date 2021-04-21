package user.com.trafficcontroller.webservices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nevon-Sony on 07-Oct-17.
 */

public class JSONPARSE {

    public String parse(JSONObject json){
        String name = " ";
        try {
            name = json.getString("Value");
        } catch (JSONException e) {
            name =e.getMessage();

        }
        return name;
    }
}
