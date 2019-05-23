package com.hereticpurge.simplespeeddial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
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

        viewHolder.mListView.setAdapter(new SubListAdapter(mContactList.get(i)));
        viewHolder.mListView.setLayoutManager(new LinearLayoutManager(mContext));

        viewHolder.mRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo this could look better by animating the card sliding out with the numbers.
                Timber.d("Clicked");
                if (viewHolder.mListView.getVisibility() == View.GONE) {
                    viewHolder.mListView.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_anim);
                    viewHolder.mListView.startAnimation(anim);
                } else {
//                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_anim);
//                    viewHolder.mListView.startAnimation(anim);
//                    viewHolder.mListView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            viewHolder.mListView.setVisibility(View.GONE);
//                        }
//                    }, 400);
                    viewHolder.mListView.setVisibility(View.GONE);
                }
            }
        });

        Timber.d("Binding ViewHolder with %s on item # %s", mContactList.get(i).getName(), i);
    }

    @Override
    public int getItemCount() {
        return mContactList.size() > 0 ? mContactList.size() : 0;
    }

    void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        Timber.d("Setting Contact List with %s items", contactList.size());
        this.notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        CardView mRecyclerCard;
        TextView mTextView;
        RecyclerView mListView;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecyclerCard = itemView.findViewById(R.id.contact_recycler_card);
            mTextView = itemView.findViewById(R.id.contact_recycler_card_name_text);
            mListView = itemView.findViewById(R.id.contact_recycler_card_list);
        }
    }

    // Recyclerview Adapter and View Holder for the sublist containing phone numbers.

    public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder> {

        private Contact mContact;
        private Object[] mKeyArray;

        SubListAdapter(Contact contact) {
            this.mContact = contact;
            mKeyArray = contact.getPhoneNumbers().keySet().toArray();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_recycler_sub_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            final String numberType = (String) mKeyArray[i];
            viewHolder.mTypeTextView.setText(numberType);

            final String number = mContact.getPhoneNumbers().get(mKeyArray[i]);
            viewHolder.mNumberTextView.setText(number);

            viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.d("Number selected for: %s with type: %s and number: %s", mContact.getName(), numberType, number);
                }
            });
        }

        @Override
        public int getItemCount() {
            int numbersSize = mContact.getPhoneNumbers().size();
            return numbersSize > 0 ? numbersSize : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout mLayout;
            TextView mTypeTextView;
            TextView mNumberTextView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                mLayout = itemView.findViewById(R.id.contact_recycler_sublist_linear_layout);
                mTypeTextView = itemView.findViewById(R.id.contact_recycler_sublist_type_text);
                mNumberTextView = itemView.findViewById(R.id.contact_recycler_sublist_number_text);
            }
        }
    }
}
