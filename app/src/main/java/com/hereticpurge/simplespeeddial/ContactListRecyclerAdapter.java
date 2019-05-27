package com.hereticpurge.simplespeeddial;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.hereticpurge.simplespeeddial.contacts.ContactsViewer;
import com.hereticpurge.simplespeeddial.database.QuickContact;
import com.hereticpurge.simplespeeddial.database.QuickContactDao;
import com.hereticpurge.simplespeeddial.database.QuickContactDatabase;
import com.hereticpurge.simplespeeddial.widget.WidgetProvider;

import java.util.List;

import timber.log.Timber;

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.ContactViewHolder> {

    private List<Contact> mContactList;
    private Context mContext;
    private MutableLiveData<ContactViewHolder> mActiveViewHolder;

    public ContactListRecyclerAdapter(Context context) {
        mContext = context;

        mActiveViewHolder = new MutableLiveData<>();

        ContactsViewer.getInstance().getContacts(context, new ContactsViewer.ContactsCallback() {
            @Override
            public void onResponse(List<Contact> contactList) {
                setContactList(contactList);
            }
        });
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contact_recycler_item, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ContactViewHolder holder) {
        holder.unObserve();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ContactViewHolder holder) {
        holder.observer();
        super.onViewAttachedToWindow(holder);
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
                viewHolder.switchVisibility();
            }
        });


        Timber.d("Binding ViewHolder with %s on item # %s", mContactList.get(i).getName(), i);
    }



    @Override
    public int getItemCount() {
        if (mContactList != null) {
            return mContactList.size() > 0 ? mContactList.size() : 0;
        } else {
            return 0;
        }
    }

    void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        Timber.d("Setting Contact List with %s items", contactList.size());

        // This method could be called from another thread.  The call to notifyDatasetChanged
        // must run on the UI thread.  So we'll make sure it does.
        if (Looper.myLooper() != mContext.getMainLooper()) {
            // Get the main looper and send the runnable to the UI thread.
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ContactListRecyclerAdapter.this.notifyDataSetChanged();
                }
            });
            // return so we don't notify twice
            return;
        }
        // Skipped the if because we're already on the UI thread.
        // So we just notify and move on.
        this.notifyDataSetChanged();

    }

    class ContactViewHolder extends RecyclerView.ViewHolder implements Observer<ContactViewHolder> {

        CardView mRecyclerCard;
        TextView mTextView;
        RecyclerView mListView;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecyclerCard = itemView.findViewById(R.id.contact_recycler_card);
            mTextView = itemView.findViewById(R.id.contact_recycler_card_name_text);
            mListView = itemView.findViewById(R.id.contact_recycler_card_list);
        }

        private void switchVisibility() {
            if (mListView.getVisibility() == View.GONE) {
                mActiveViewHolder.postValue(this);
                setVisible();
            } else {
                setGone();
            }
        }

        private void setVisible() {
            mListView.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_anim);
            mListView.startAnimation(anim);
        }

        private void setGone() {
            mListView.setVisibility(View.GONE);
        }

        void observer() {
            mActiveViewHolder.observeForever(this);
        }

        void unObserve() {
            mActiveViewHolder.removeObserver(this);
        }

        @Override
        public void onChanged(@Nullable ContactViewHolder contactViewHolder) {
            if (contactViewHolder != this) {
                setGone();
            }
        }
    }

    // Recyclerview Adapter and View Holder for the sublist containing phone numbers.  This whole
    // adapter only covers the phone numbers of a single contact from the constructor.

    public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder> {

        private Boolean isVisisble;

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
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            QuickContact quickContact = new QuickContact(mContact.getName());

                            quickContact.setNumber(number);
                            quickContact.setNumberType(numberType);

                            QuickContactDao quickContactDao = QuickContactDatabase.getQuickContactDatabase(mContext).quickContactDao();
                            quickContactDao.insertContact(quickContact);

                            Timber.d("Number selected for: %s with type: %s and number: %s ... Adding to database",
                                    mContact.getName(),
                                    numberType,
                                    number);

                            WidgetProvider.notifyWidgets(mContext);
                        }
                    });
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
