/* JSON API for android appliation */
package user.com.trafficcontroller.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class RestAPI {
    private final String urlString = "http://atraffic.hostoise.com/Handler1.ashx";

    private static String convertStreamToUTF8String(InputStream stream) throws IOException {
	    String result = "";
	    StringBuilder sb = new StringBuilder();
	    try {
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[4096];
            int readedChars = 0;
            while (readedChars != -1) {
                readedChars = reader.read(buffer);
                if (readedChars > 0)
                   sb.append(buffer, 0, readedChars);
            }
            result = sb.toString();
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private String load(String contents) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(60000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream());
        w.write(contents);
        w.flush();
        InputStream istream = conn.getInputStream();
        String result = convertStreamToUTF8String(istream);
        return result;
    }


    private Object mapObject(Object o) {
		Object finalValue = null;
		if (o.getClass() == String.class) {
			finalValue = o;
		}
		else if (Number.class.isInstance(o)) {
			finalValue = String.valueOf(o);
		} else if (Date.class.isInstance(o)) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", new Locale("en", "USA"));
			finalValue = sdf.format((Date)o);
		}
		else if (Collection.class.isInstance(o)) {
			Collection<?> col = (Collection<?>) o;
			JSONArray jarray = new JSONArray();
			for (Object item : col) {
				jarray.put(mapObject(item));
			}
			finalValue = jarray;
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			Method[] methods = o.getClass().getMethods();
			for (Method method : methods) {
				if (method.getDeclaringClass() == o.getClass()
						&& method.getModifiers() == Modifier.PUBLIC
						&& method.getName().startsWith("get")) {
					String key = method.getName().substring(3);
					try {
						Object obj = method.invoke(o, null);
						Object value = mapObject(obj);
						map.put(key, value);
						finalValue = new JSONObject(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		return finalValue;
	}

    public JSONObject ALogin(String username,String pass) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "ALogin");
        p.put("username",mapObject(username));
        p.put("pass",mapObject(pass));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject AddLanes(String name,String car,String datetime) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "AddLanes");
        p.put("name",mapObject(name));
        p.put("car",mapObject(car));
        p.put("datetime",mapObject(datetime));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject DeleteLanes(String lid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "DeleteLanes");
        p.put("lid",mapObject(lid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getLanes() throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getLanes");
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UpdateCar(ArrayList<String> car,ArrayList<String> lid,String datetime,String isexisting) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UpdateCar");
        p.put("car",mapObject(car));
        p.put("lid",mapObject(lid));
        p.put("datetime",mapObject(datetime));
        p.put("isexisting",mapObject(isexisting));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getAuto() throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getAuto");
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UpdateAuto(ArrayList<String> time,ArrayList<String> lid,ArrayList<String> status) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UpdateAuto");
        p.put("time",mapObject(time));
        p.put("lid",mapObject(lid));
        p.put("status",mapObject(status));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getManual() throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getManual");
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject UpdateManual(ArrayList<String> status,ArrayList<String> lid) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "UpdateManual");
        p.put("status",mapObject(status));
        p.put("lid",mapObject(lid));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject getData() throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "getData");
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject updateLane(String lid,String car) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "updateLane");
        p.put("lid",mapObject(lid));
        p.put("car",mapObject(car));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

    public JSONObject updateAllLanes(ArrayList<String> lid,ArrayList<String> car) throws Exception {
        JSONObject result = null;
        JSONObject o = new JSONObject();
        JSONObject p = new JSONObject();
        o.put("interface","RestAPI");
        o.put("method", "updateAllLanes");
        p.put("lid",mapObject(lid));
        p.put("car",mapObject(car));
        o.put("parameters", p);
        String s = o.toString();
        String r = load(s);
        result = new JSONObject(r);
        return result;
    }

}


