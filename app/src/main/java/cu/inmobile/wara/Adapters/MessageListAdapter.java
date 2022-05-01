package cu.inmobile.wara.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import cu.inmobile.wara.Activities.ChatActivity;
import cu.inmobile.wara.Activities.ProfileViewActivity;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.MessageThread;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.R;

/**
 * Created by amal on 30/08/16.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {

    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMM yy, h:mm a");
    private List<MessageThread> messageThreadList;
    private Context mContext;

    public MessageListAdapter(List<MessageThread> messageThreadList, Context mContext) {
        this.messageThreadList = messageThreadList;
        this.mContext = mContext;
    }

    @Override
    public MessageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageListAdapter.MyViewHolder holder, int position) {

        final MessageThread messageThread = messageThreadList.get(position);
        if ( messageThread.getOnline() == 0 )
            holder.imgOnline.setColorFilter(ContextCompat.getColor(mContext, R.color.gray));
        else
            holder.imgOnline.setColorFilter(ContextCompat.getColor(mContext, R.color.yellow));

        holder.tvName.setText(messageThread.getName());
        holder.tvDate.setText(messageThread.getDate());
        if (messageThread.isUnread()) {
            holder.tvLastMsgUnread.setVisibility(View.VISIBLE);
            holder.tvLastMsg.setVisibility(View.GONE);
            holder.tvLastMsgUnread.setText(messageThread.getLastMessage());
        } else{
            holder.tvLastMsgUnread.setVisibility(View.GONE);
            holder.tvLastMsg.setVisibility(View.VISIBLE);
            holder.tvLastMsg.setText(messageThread.getLastMessage());
        }


        Glide.with(mContext).load(Endpoints.baseNoThumbUrl + messageThread.getPicture()).placeholder(R.drawable.user_profile).dontAnimate().into(holder.imgPicture);

        holder.llMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                //intent.putExtra("receiverId", messageThread.getUserId());
                intent.putExtra("target_id", messageThread.getUserId());
                /*intent.putExtra("receiverImageUrl", messageThread.getPicture());
                intent.putExtra("receiverName", messageThread.getName());
                intent.putExtra("contact_id",messageThread.getContactId());
                intent.putExtra("aboutme",messageThread.getUserAboutMe());
                intent.putExtra("online",messageThread.getOnline());*/
                mContext.startActivity(intent);
            }
        });

        holder.imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileViewActivity.class);
                //intent.putExtra("receiverId", messageThread.getUserId());
                intent.putExtra("target_id", messageThread.getUserId());
                intent.putExtra("blur_image", false);
                intent.putExtra("from", "MessageListAdapter");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageThreadList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvLastMsg,tvLastMsgUnread, tvName, tvDate;
        public LinearLayout llMessageLayout;
        public ImageView imgPicture, imgOnline;

        public MyViewHolder(View view) {
            super(view);
            tvLastMsg = (TextView) view.findViewById(R.id.tv_last_message);
            tvLastMsgUnread = (TextView) view.findViewById(R.id.tv_last_message_unread);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            llMessageLayout = (LinearLayout) view.findViewById(R.id.ll_message_layout);
            imgPicture = (ImageView) view.findViewById(R.id.img_user_image);
            imgOnline = (ImageView) view.findViewById(R.id.img_online);

        }
    }
}
