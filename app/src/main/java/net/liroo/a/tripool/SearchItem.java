package net.liroo.a.tripool;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class SearchItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String no, deptMain, deptSub, departure;

    @SuppressWarnings("unchecked")
    public SearchItem(JSONObject data)
    {
        try {
            no = data.getString("no");
            deptMain = data.getString("dept_main");
            deptSub = data.getString("dept_sub");
            departure = data.getString("departure");
        } catch ( Exception e ) {

        }
    }

    public String getDeptMain() { return deptMain; }

    // Parcelable
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Bundle bundle = new Bundle();

        bundle.putString("no", no);
        bundle.putString("deptMain", deptMain);
        bundle.putString("deptSub", deptSub);
        bundle.putString("departure", departure);

        dest.writeBundle(bundle);
    }

    public SearchItem(Parcel in)
    {
        Bundle bundle = in.readBundle();

        no = bundle.getString("no");
        deptMain = bundle.getString("deptMain");
        deptSub = bundle.getString("deptSub");
        departure = bundle.getString("departure");
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public SearchItem createFromParcel(Parcel in) {
            return new SearchItem(in);
        }

        public SearchItem[] newArray(int size) {
            return new SearchItem[size];
        }
    };
}
