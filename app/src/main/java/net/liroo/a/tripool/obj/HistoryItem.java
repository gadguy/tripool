package net.liroo.a.tripool.obj;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;

public class HistoryItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String no, deptMain, deptSub, departure, destMain, destSub, destination;
    private Long deptDate;
    private String luggage, people;
    private boolean isDoEvaluation;

    @SuppressWarnings("unchecked")
    public HistoryItem(JSONObject data)
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
            // TODO: 서버에서 가져온 키로 변경 (평가 여부)
            String evaluation = data.getString("evaluation");
            if ( evaluation != null && evaluation.equals("") ) {
                isDoEvaluation = true;
            }
        } catch ( Exception e ) {

        }
    }

    public HistoryItem(String no, String deptMain, String deptSub, String departure, String destMain, String destSub, String destination, long deptDate, String people, String luggage, boolean isDoEvaluation)
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
            this.isDoEvaluation = isDoEvaluation;
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
    public boolean isDoEvaluation() { return isDoEvaluation; }

    public void setLuggage(String luggage) { this.luggage = luggage; }
    public void setPeople(String people) { this.people = people; }
    public void setDoEvaluation(boolean value) { this.isDoEvaluation = value; }

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
        bundle.putBoolean("isDoEvaluation", isDoEvaluation);

        dest.writeBundle(bundle);
    }

    public HistoryItem(Parcel in)
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
        isDoEvaluation = bundle.getBoolean("isDoEvaluation");
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };
}
