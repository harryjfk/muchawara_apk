package cu.inmobile.wara.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.UUID;

import cu.inmobile.wara.Activities.ChatActivity;
import cu.inmobile.wara.Adapters.ChatMessageAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage {
    private boolean isImage, isMine, isGift,isRecieved, isReaded;
    private String content;

    //Url of the gift
    private String gift_url;

    private  String gift_id;

    private String image_url;

    private Bitmap image;
    private String token;


    public ChatMessage(String message, boolean mine, boolean image, boolean gift, String token, Boolean recieved, Boolean readed) {
        content = message;
        isMine = mine;
        isGift = gift;
        isImage = image;
               if(token!=null)
        this.token = token;
        else
        {
            UUID uuid = UUID.randomUUID();
            this.token = uuid.toString();
        }
        this.setIsReaded(readed);
        this.setIsRecieved(recieved);
    }

    public String getContent() {
        return content;
    }
    public String getToken() {
        return token;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
    public boolean isReaded() {
        return isReaded;
    }

    public void setIsReaded(boolean isReaded) {
        this.isReaded = isReaded;
        if(this.isReaded  && this.getView()!=null)
        this.getView().findViewById(R.id.img_received).setVisibility(View.VISIBLE);
    }
    public boolean isRecieved() {
        return isRecieved;
    }


    private View view;
    public void setView(View view)
    {
  this.view = view;

        this.setIsReaded(this.isReaded);
  this.setIsRecieved(this.isRecieved);

    }

    public View getView(){return this.view;}
    public void setIsRecieved(boolean isRecieved) {
        this.isRecieved = isRecieved;
        if(this.isRecieved && this.getView()!=null) {
            this.getView().findViewById(R.id.img_sended).setVisibility(View.VISIBLE);

        }

    }

    /**
     *
     * @return
     */
    public boolean isGift(){
        return isGift;
    }

    /**
     *
     * @return the url if the current item is gift type else null
     */
    public String getGiftUrl(){
        if(isGift){
            return gift_url;
        }
        return null;
    }

    /**
     * This function sets the url of the gift
     * @param giftUrl of the gift.
     */
    public void setGiftUrl(String giftUrl){
        if(isGift){
            this.gift_url = giftUrl;
        }
    }

    /**
     * Sets the gift id.
     * @param id
     */
    public void setGiftId(String id){
        if(isGift){
            this.gift_id = id;
        }
    }

    /**
     * Returns the gift id
     * @return
     */
    public String getGiftId(){
        if(isGift){
            return this.gift_id;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getImageUrl(){
        if(isImage){
            return this.image_url;
        }
        return null;
    }

    /**
     *
     * @param url
     */
    public void setImageUrl(String url){
        if(isImage){
            this.image_url = url;
        }
    }

    /**
     *
     * @return
     */
    public Bitmap getImageBitmap(){
        if(isImage){
            return this.image;
        }
        return null;
    }

    /**
     *
     * @param newImage
     */
    public void setImageBitmao(Bitmap newImage){
        if(isImage){
            this.image = newImage;
        }
    }
}
