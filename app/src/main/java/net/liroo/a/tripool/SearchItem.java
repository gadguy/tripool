package net.liroo.a.tripool;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

//검색된 결과 아이템, 오브젝트
public class SearchItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String no, deptMain, deptSub, departure, destMain, destSub, destination, deptDate;
    private String luggage, people, vehicleType, totalState, state, distance, takeTime, price, totalPrice, commission, discount, discountType;

    public SearchItem() {  }

    @SuppressWarnings("unchecked")
    public SearchItem(JSONObject data)
    {
        try {
            no = data.getString("no");
            deptMain = data.getString("dept_main");
            deptSub = data.getString("dept_sub");
            departure = data.getString("departure");
            destMain = data.getString("dest_main");
            destSub = data.getString("dest_sub");
            destination = data.getString("destination");
            deptDate = data.getString("dept_date");
        } catch ( Exception e ) {

        }
    }

    public String getDeptMain() { return deptMain; }
    public String getDeptSub() { return deptSub; }
    public String getDeparture() { return departure; }
    public String getDestMain() { return deptMain; }
    public String getDestSub() { return destSub; }
    public String getDestination() { return destination; }
    public String getDeptDate() { return deptDate; }

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
        bundle.putString("destMain", destMain);
        bundle.putString("destSub", destSub);
        bundle.putString("destination", destination);
        bundle.putString("deptDate", deptDate);

        dest.writeBundle(bundle);
    }

    public SearchItem(Parcel in)
    {
        Bundle bundle = in.readBundle();

        no = bundle.getString("no");
        deptMain = bundle.getString("deptMain");
        deptSub = bundle.getString("deptSub");
        departure = bundle.getString("departure");
        destMain = bundle.getString("destMain");
        destSub = bundle.getString("destSub");
        destination = bundle.getString("destination");
        deptDate = bundle.getString("deptDate");
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
