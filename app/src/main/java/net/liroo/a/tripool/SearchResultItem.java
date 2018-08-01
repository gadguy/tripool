package net.liroo.a.tripool;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SearchResultItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String no, deptMain, deptSub, departure, destMain, destSub, destination;
    private long deptDate;
    private String luggage, people, vehicleType, totalState, state, distance, takeTime, price, totalPrice, commission, discount, discountType;

    @SuppressWarnings("unchecked")
    public SearchResultItem(String no, String deptMain, String deptSub, String departure, String destMain, String destSub, String destination, long deptDate, String people, String luggage)
    {
        try {
            this.no = no;
            this.deptMain = deptMain;
            this.deptSub = deptSub;
            this.departure = departure;
            this.destMain = destMain;
            this.destSub = destSub;
            this.destination = destination;
            this.deptDate = deptDate;
            this.people = people;
            this.luggage = luggage;
        } catch ( Exception e ) {

        }
    }

    public String getNo() { return no; }
    public String getDeptMain() { return deptMain; }
    public String getDeptSub() { return deptSub; }
    public String getDeparture() { return departure; }
    public String getDestMain() { return destMain; }
    public String getDestSub() { return destSub; }
    public String getDestination() { return destination; }
    public long getDeptDate() { return deptDate; }
    public String people() { return people; }
    public String luggage() { return luggage; }


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
        bundle.putLong("deptDate", deptDate);
        bundle.putString("people", people);
        bundle.putString("luggage", luggage);

        dest.writeBundle(bundle);
    }

    public SearchResultItem(Parcel in)
    {
        Bundle bundle = in.readBundle();

        no = bundle.getString("no");
        deptMain = bundle.getString("deptMain");
        deptSub = bundle.getString("deptSub");
        departure = bundle.getString("departure");
        destMain = bundle.getString("destMain");
        destSub = bundle.getString("destSub");
        destination = bundle.getString("destination");
        deptDate = bundle.getLong("deptDate");
        people = bundle.getString("people");
        people = bundle.getString("luggage");
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public SearchResultItem createFromParcel(Parcel in) {
            return new SearchResultItem(in);
        }

        public SearchResultItem[] newArray(int size) {
            return new SearchResultItem[size];
        }
    };
}
