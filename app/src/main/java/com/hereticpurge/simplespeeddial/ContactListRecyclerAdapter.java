package com.hereticpurge.simplespeeddial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hereticpurge.simplespeeddial.contacts.Contact;

import java.util.List;

import timber.log.Timber;

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.ContactViewHolder> {

    private List<Contact> mContactList;
    private Context mContext;

    public ContactListRecyclerAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contact_recycler_item, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder viewHolder, int i) {
        viewHolder.mRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Clicked");
                Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_anim);
                viewHolder.mRecyclerCard.startAnimation(anim);
            }
        });

        Timber.d("Binding ViewHolder with %s on item # %s", mContactList.get(i).getName(), i);
        viewHolder.mTextView.setText(mContactList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        Timber.d("Getting item count with a value of %s", (mContactList.size() > 0 ? mContactList.size() : 0));
        return mContactList.size() > 0 ? mContactList.size() : 0;
    }

    public void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        Timber.d("Setting Contact List with %s items", contactList.size());
        this.notifyDataSetChanged();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        CardView mRecyclerCard;
        TextView mTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecyclerCard = itemView.findViewById(R.id.contact_recycler_card);
            mTextView = itemView.findViewById(R.id.contact_recycler_card_text);
        }
    }
}
