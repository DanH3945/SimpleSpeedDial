package com.danh3945.simplespeeddial.views.contactList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.LargeWidgetObject;
import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.views.contactList.contacts.Contact;
import com.danh3945.simplespeeddial.views.contactList.contacts.ContactRetriever;

import java.util.List;

import timber.log.Timber;

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.ContactViewHolder> {

    public interface ContactListResultCallback {
        void clickResult(LargeWidgetObject object);
    }

    private List<Contact> mContactList;
    private Context mContext;

    // Live data for managing the state of the view holder's dropdown menus.
    private MutableLiveData<ContactViewHolder> mActiveViewHolder;

    private RecyclerView mOwner;

    private ContactListResultCallback mCallback;

    public ContactListRecyclerAdapter(Context context, RecyclerView owner, ContactListResultCallback callback) {

        mCallback = callback;
        mContext = context;
        mOwner = owner;

        mActiveViewHolder = new MutableLiveData<>();

        ContactRetriever.getInstance().getContacts(context, new ContactRetriever.ContactsCallback() {
            @Override
            public void onResponse(List<Contact> contactList) {
                setContactList(contactList);
            }
        });
    }

    public ContactListRecyclerAdapter(Context context, RecyclerView owner) {
        this(context, owner, null);
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
        holder.observe();
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder viewHolder, int i) {

        Contact contact = mContactList.get(i);

        viewHolder.mTextView.setText(contact.getName());

        Bitmap bitmap = ImageHelper.getContactPhoto(mContext, contact.getLookupUri());

        if (bitmap != null) {
            viewHolder.mContactImageView.setImageBitmap(bitmap);
        } else {


            Drawable drawable = ImageHelper.getDefaultContactIconRounded(contact.getName());

            viewHolder.mContactImageView.setImageDrawable(drawable);
        }

        viewHolder.mListView.setAdapter(new SubListAdapter(mContactList.get(i)));
        viewHolder.mListView.setLayoutManager(new LinearLayoutManager(mContext));

        viewHolder.mRecyclerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Clicked");
                viewHolder.switchSubListVisibility();
            }
        });

        Timber.d("Binding CurrentSpeedDialRecyclerViewHolder with %s on item # %s", mContactList.get(i).getName(), i);
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

        // This method could be called from another thread.  The call to notifyDataSetChanged
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
        ImageView mContactImageView;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mRecyclerCard = itemView.findViewById(R.id.contact_recycler_card);
            mTextView = itemView.findViewById(R.id.contact_recycler_card_name_text);
            mListView = itemView.findViewById(R.id.contact_recycler_card_list);
            mContactImageView = itemView.findViewById(R.id.contact_recycler_card_image_view);
        }

        private void switchSubListVisibility() {
            if (mListView.getVisibility() == View.GONE) {
                mActiveViewHolder.postValue(this);
                showSubList();
            } else {
                hideSubList();
            }
        }

        private void showSubList() {
            mListView.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_anim);
            mListView.startAnimation(anim);
        }

        private void hideSubList() {
            mOwner.setLayoutFrozen(true);
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_anim);
            mListView.startAnimation(anim);
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOwner.setLayoutFrozen(false);
                }
            }, 300);
            mListView.setVisibility(View.GONE);
        }

        void observe() {
            mActiveViewHolder.observeForever(this);
        }

        void unObserve() {
            mActiveViewHolder.removeObserver(this);
        }

        @Override
        public void onChanged(@Nullable ContactViewHolder contactViewHolder) {
            if (contactViewHolder != this) {
                hideSubList();
            }
        }
    }

    // Recyclerview Adapter and View Holder for the sublist containing phone numbers.  This whole
    // adapter only covers the phone numbers of a single contact from the constructor.

    class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.SublistViewHolder> {

        private Contact mContact;
        private Object[] mKeyArray;

        SubListAdapter(Contact contact) {
            this.mContact = contact;
            mKeyArray = contact.getPhoneNumbers().keySet().toArray();
        }

        @NonNull
        @Override
        public SublistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_recycler_sub_list_item, viewGroup, false);
            return new SublistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SublistViewHolder viewHolder, final int i) {
            final String name = mContact.getName();

            final String numberType = (String) mKeyArray[i];
            viewHolder.mTypeTextView.setText(numberType);

            final String number = mContact.getPhoneNumbers().get(mKeyArray[i]);
            viewHolder.mNumberTextView.setText(number);

            final Uri lookupUri = mContact.getLookupUri();

            viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LargeWidgetObject largeWidgetObject = new LargeWidgetObject();

                    largeWidgetObject.setContactId(mContact.getId());
                    largeWidgetObject.setName(name);
                    largeWidgetObject.setNumber(number);
                    largeWidgetObject.setNumberType(numberType);
                    largeWidgetObject.setLookupUri(lookupUri);

                    if (mCallback == null) {
                        largeWidgetObject.addToLargeWidgetSpeedDial(mContext);

                        Timber.d("Number selected for: %s with type: %s and number: %s ... Adding to database",
                                mContact.getName(),
                                numberType,
                                number);
                    } else {
                        mCallback.clickResult(largeWidgetObject);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            int numbersSize = mContact.getPhoneNumbers().size();
            return numbersSize > 0 ? numbersSize : 0;
        }

        class SublistViewHolder extends RecyclerView.ViewHolder {

            LinearLayout mLayout;
            TextView mTypeTextView;
            TextView mNumberTextView;

            SublistViewHolder(@NonNull View itemView) {
                super(itemView);

                mLayout = itemView.findViewById(R.id.contact_recycler_sublist_linear_layout);
                mTypeTextView = itemView.findViewById(R.id.contact_recycler_sublist_type_text);
                mNumberTextView = itemView.findViewById(R.id.contact_recycler_sublist_number_text);
            }
        }
    }
}
