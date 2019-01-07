package net.liroo.a.tripool.obj;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;

public class NoticeItem implements Serializable, Parcelable
{
    private static final long serialVersionUID = 3467427543832512425L;

    private String date, title, readCount, contentURL, content;

    @SuppressWarnings("unchecked")
    public NoticeItem(JSONObject data)
    {
        try {
            date = data.getString("date");
            title = data.getString("title");
            readCount = data.getString("readCount");
            contentURL = data.getString("content_url");
            content = data.getString("content");
        } catch ( Exception e ) {

        }
    }

    public NoticeItem(String date, String title, String readCount, String contentURL, String content)
    {
        try {
            this.date = date;
            this.title = title;
            this.readCount = readCount;
            this.contentURL = contentURL;
            this.content = content;
        } catch ( Exception e ) {

        }
    }

    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getReadCount() { return readCount; }
    public String getContentURL() { return contentURL; }
    public String getContent() { return content; }

    // Parcelable
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Bundle bundle = new Bundle();

        bundle.putString("date", date);
        bundle.putString("title", title);
        bundle.putString("readCount", readCount);
        bundle.putString("contentURL", contentURL);
        bundle.putString("content", content);

        dest.writeBundle(bundle);
    }

    public NoticeItem(Parcel in)
    {
        Bundle bundle = in.readBundle();

        date = bundle.getString("date");
        title = bundle.getString("title");
        readCount = bundle.getString("readCount");
        contentURL = bundle.getString("contentURL");
        content = bundle.getString("content");
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public NoticeItem createFromParcel(Parcel in) {
            return new NoticeItem(in);
        }

        public NoticeItem[] newArray(int size) {
            return new NoticeItem[size];
        }
    };
}
