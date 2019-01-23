package net.liroo.a.tripool.obj;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;

//검색된 결과 아이템, 오브젝트
public class SearchItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String no, deptMain, deptSub, departure, destMain, destSub, destination;
    private Long deptDate;
    private String luggage, people, vehicleType, totalState, state, distance, takeTime, price, totalPrice, commission, discount, discountType;
    private String bookID, ownerID, car_no;

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
            deptDate = data.getLong("dept_date");
            people = data.getString("people");
            luggage = data.getString("luggage");
            bookID = data.getString("book_id");
            ownerID = data.getString("owner_id");
            distance = data.getString("distance");
        } catch ( Exception e ) {

        }
    }

    public SearchItem(String no, String deptMain, String deptSub, String departure, String destMain, String destSub, String destination, long deptDate, String people, String luggage, String distance)
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
            this.distance = distance;
        } catch ( Exception e ) {

        }
    }

    public String getDeptMain() { return deptMain; }
    public String getDeptSub() { return deptSub; }
    public String getDeparture() { return departure; }
    public String getDestMain() { return deptMain; }
    public String getDestSub() { return destSub; }
    public String getDestination() { return destination; }
    public long getDeptDate() { return deptDate; }
    public String getPeople() { return people; }
    public String getLuggage() { return luggage; }
    public String getBookID() { return bookID; }
    public String getOwnerID() { return ownerID; }
    public String getDistance() { return distance; }

    public void setLuggage(String luggage) { this.luggage = luggage;}
    public void setPeople(String people) { this.people = people;}

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
        bundle.putString("bookID", bookID);
        bundle.putString("ownerID", ownerID);
        bundle.putString("distance", distance);

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
        deptDate = bundle.getLong("deptDate");
        people = bundle.getString("people");
        luggage = bundle.getString("luggage");
        bookID = bundle.getString("bookID");
        ownerID = bundle.getString("ownerID");
        distance = bundle.getString("distance");
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
