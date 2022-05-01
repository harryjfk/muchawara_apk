package cu.inmobile.wara.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;


@Entity(tableName = "message_table")
public class Message {

    @ColumnInfo(name = "id")
    private String mId;

    @NonNull
    @ColumnInfo(name = "user_from")
    private String mFrom;

    @ColumnInfo(name = "user_to")
    private String mTo;

    @ColumnInfo(name = "body")
    private String mBody;

    @ColumnInfo(name = "time")
    private String mTime;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "token")
    private String mToken;

    @ColumnInfo(name = "sended")
    private int mSended;

    @ColumnInfo(name = "received")
    private int mReceived;

    @ColumnInfo(name = "readed")
    private int mReaded;

    @ColumnInfo(name = "mine")
    private boolean mMine;

    @ColumnInfo(name = "image")
    private boolean mImage;

    @ColumnInfo(name = "gift")
    private boolean mGift;

    public Message( String id , @NonNull String from, String to , String body , String time, String token, int sended, int received,int readed , boolean mine , boolean image , boolean gift) {
        this.mId = id;
        this.mFrom = from;
        this.mTo = to;
        this.mBody = body;
        this.mTime = time;
        this.mSended = sended;
        this.mReceived = received;
        this.mReaded = readed;
        this.mMine = mine;
        this.mImage = image;
        this.mGift = gift;

        if(token!=null)
            this.mToken = token;
        else
        {
            UUID uuid = UUID.randomUUID();
            this.mToken = uuid.toString();
        }
    }



    public String getId() {
        return mId;
    }

    public void setId(@NonNull String mId) {
        this.mId = mId;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(@NonNull String mFrom) {
        this.mFrom = mFrom;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String mTo) {
        this.mTo = mTo;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public int getSended() {
        return mSended;
    }

    public void setSended(int mSended) {
        this.mSended = mSended;
    }

    public int getReceived() {
        return mReceived;
    }

    public void setmeceived(int mReceived) {
        this.mReceived = mReceived;
    }

    public int getReaded() {
        return mReaded;
    }

    public void setReaded(int mReaded) {
        this.mReaded = mReaded;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public boolean isMine() {
        return mMine;
    }

    public void setMine(boolean mMine) {
        this.mMine = mMine;
    }

    public boolean isImage() {
        return mImage;
    }

    public void setImage(boolean mImage) {
        this.mImage = mImage;
    }

    public boolean isGift() {
        return mGift;
    }

    public void setGift(boolean mGift) {
        this.mGift = mGift;
    }


}
