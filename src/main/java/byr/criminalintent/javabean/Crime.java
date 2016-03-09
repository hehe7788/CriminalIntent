package byr.criminalintent.javabean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by L.Y.C on 2016/2/4.
 */
public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private SimpleDateFormat mSimpleDateFormat;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {

        return mId;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();

    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }
        mSolved = json.getBoolean(JSON_SOLVED);

        //TODO format
        //"yyyy年MM月dd日 E HH:mm"
        mSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm", Locale.CHINA);

        Log.e("before format", json.getString(JSON_DATE));
        try {
            //TODO format bug mDate总是为空
            mDate = mSimpleDateFormat.parse(json.getString(JSON_DATE));
            Log.e("after format", mDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            mDate = new Date(json.getString(JSON_DATE));
        }
    }

    /**
     * 转化
     * @return 可写入JSON文件的JSONObject对象
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_SOLVED, mSolved);
        json.put(JSON_DATE, mDate.toString());

        return json;
    }
}
