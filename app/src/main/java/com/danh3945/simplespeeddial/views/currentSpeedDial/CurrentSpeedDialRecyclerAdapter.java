package com.danh3945.simplespeeddial.views.currentSpeedDial;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.danh3945.simplespeeddial.R;
import com.danh3945.simplespeeddial.database.LargeWidgetObject;
import com.danh3945.simplespeeddial.database.SpeedDialDatabase;
import com.danh3945.simplespeeddial.image.ImageHelper;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CurrentSpeedDialRecyclerAdapter extends RecyclerView.Adapter<CurrentSpeedDialRecyclerAdapter.CurrentSpeedDialRecyclerViewHolder> implements Observer<List<LargeWidgetObject>> {

    List<LargeWidgetObject> mCurrentSpeedDialList;
    Context mContext;

    SpeedDialDatabase mDatabase;

    CurrentSpeedDialRecyclerAdapter(Context context, LifecycleOwner lifecycleOwner, RecyclerView recyclerView) {
        this.mContext = context;
        this.mCurrentSpeedDialList = new ArrayList<>();
        mDatabase = SpeedDialDatabase.getSpeedDialDatabase(context);
        mDatabase.largeWidgetDao()
                .getSpeedDialButtonsListLiveData()
                .observe(lifecycleOwner, this);

        attachSwipeListener(recyclerView);
    }

    void attachSwipeListener(RecyclerView recyclerView) {

        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        CurrentSpeedDialRecyclerAdapter.this.removeEntry(position);
                        Timber.d("Swiped Position, %s", position);
                        LargeWidgetProvider.notifyLargeWidgets(mContext);
                    }
                };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public CurrentSpeedDialRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.current_speed_dial_recycler_item, viewGroup, false);
        return new CurrentSpeedDialRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentSpeedDialRecyclerViewHolder viewHolder, int i) {
        LargeWidgetObject largeWidgetObject = mCurrentSpeedDialList.get(i);

        int dimen = (int) mContext.getResources().getDimension(R.dimen.current_recycler_fragment_image_dimensions);
        Bitmap bitmap = ImageHelper.getContactPhoto(mContext, largeWidgetObject.getLookupUri(), largeWidgetObject.getName(), dimen, dimen);
        viewHolder.mImageView.setImageBitmap(bitmap);
        viewHolder.mNameText.setText(largeWidgetObject.getName());
        viewHolder.mNumberText.setText(largeWidgetObject.getNumber());
        viewHolder.mNumberTypeText.setText(largeWidgetObject.getNumberType());

    }

    @Override
    public int getItemCount() {
        return mCurrentSpeedDialList.size() > 0 ? mCurrentSpeedDialList.size() : 0;
    }

    void removeEntry(int position) {
        mCurrentSpeedDialList.get(position).removeFromSpeedDial(mContext);
    }

    @Override
    public void onChanged(@Nullable List<LargeWidgetObject> largeWidgetObjects) {
        mCurrentSpeedDialList = largeWidgetObjects;
        this.notifyDataSetChanged();
    }

    public class CurrentSpeedDialRecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        ImageView mImageView;
        TextView mNameText;
        TextView mNumberText;
        TextView mNumberTypeText;

        public CurrentSpeedDialRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = itemView.findViewById(R.id.current_speed_dial_recycler_item_card);
            mImageView = itemView.findViewById(R.id.current_speed_dial_recycler_item_image);
            mNameText = itemView.findViewById(R.id.current_speed_dial_recycler_item_name);
            mNumberText = itemView.findViewById(R.id.current_speed_dial_recycler_item_number);
            mNumberTypeText = itemView.findViewById(R.id.current_speed_dial_recycler_item_number_type);

        }
    }
}
