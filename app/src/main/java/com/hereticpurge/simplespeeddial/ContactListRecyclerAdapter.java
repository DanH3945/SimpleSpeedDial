package com.hereticpurge.simplespeeddial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        viewHolder.mTextView.setText(mContactList.get(i).getName());

        List<String> contactNumbers = mContactList.get(i).getPhoneNumbers();

        viewHolder.mListView.setAdapter(new SubListAdapter(contactNumbers));
        viewHolder.mListView.setLayoutManager(new LinearLayoutManager(mContext));

        viewHolder.mRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Clicked");
                if (viewHolder.mListView.getVisibility() == View.GONE) {
                    viewHolder.mListView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mListView.setVisibility(View.GONE);
                }
            }
        });

        Timber.d("Binding ViewHolder with %s on item # %s", mContactList.get(i).getName(), i);
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
        RecyclerView mListView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecyclerCard = itemView.findViewById(R.id.contact_recycler_card);
            mTextView = itemView.findViewById(R.id.contact_recycler_card_text);
            mListView = itemView.findViewById(R.id.contact_recycler_card_list);
        }
    }

    // Recyclerview Adapter and View Holder for the sublist containing phone numbers.

    public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder> {

        private List<String> mNumbers;

        public SubListAdapter(List<String> numbers) {
            this.mNumbers = numbers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_recycler_sub_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.mTextView.setText(mNumbers.get(i));
        }

        @Override
        public int getItemCount() {
            return mNumbers.size() > 0 ? mNumbers.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mTextView = itemView.findViewById(R.id.contact_recycler_sublist_text);
            }
        }
    }
}
