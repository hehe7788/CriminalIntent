package byr.criminalintent.javabean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by L.Y.C on 2016/3/10.
 */
public class Photo {

    private static final String JSON_FILENAME = "filename";
    private int mOrientation;
    private String mFileName;

    public Photo(String fileName) {
        mFileName = fileName;
    }

    //拍摄时记录方向
    public Photo(String fileName, int orientation) {
        mFileName = fileName;
        mOrientation = orientation;
    }

    public Photo(JSONObject json) throws JSONException {
        mFileName = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFileName);
        return json;
    }

    public String getFileName() {
        return mFileName;
    }
}
