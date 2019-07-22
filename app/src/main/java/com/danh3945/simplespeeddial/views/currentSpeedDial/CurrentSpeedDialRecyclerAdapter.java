package com.danh3945.simplespeeddial.views.currentSpeedDial;

import android.content.Context;
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
import com.danh3945.simplespeeddial.database.SpeedDialObject;
import com.danh3945.simplespeeddial.database.SpeedDialDatabase;
import com.danh3945.simplespeeddial.widget.LargeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CurrentSpeedDialRecyclerAdapter extends RecyclerView.Adapter<CurrentSpeedDialRecyclerAdapter.CurrentSpeedDialRecyclerViewHolder> implements Observer<List<SpeedDialObject>> {

    List<SpeedDialObject> mCurrentSpeedDialList;
    Context mContext;

    SpeedDialDatabase mDatabase;

    CurrentSpeedDialRecyclerAdapter(Context context, LifecycleOwner lifecycleOwner, RecyclerView recyclerView) {
        this.mContext = context;
        this.mCurrentSpeedDialList = new ArrayList<>();
        mDatabase = SpeedDialDatabase.getSpeedDialDatabase(context);
        mDatabase.speedDialDao()
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
        SpeedDialObject speedDialObject = mCurrentSpeedDialList.get(i);

        viewHolder.mImageView.setImageBitmap(speedDialObject.getContactPhoto(mContext));
        viewHolder.mNameText.setText(speedDialObject.getName());
        viewHolder.mNumberText.setText(speedDialObject.getNumber());
        viewHolder.mNumberTypeText.setText(speedDialObject.getNumberType());

    }

    @Override
    public int getItemCount() {
        return mCurrentSpeedDialList.size() > 0 ? mCurrentSpeedDialList.size() : 0;
    }

    void removeEntry(int position) {
        mCurrentSpeedDialList.get(position).removeFromSpeedDial(mContext);
    }

    @Override
    public void onChanged(@Nullable List<SpeedDialObject> speedDialObjects) {
        mCurrentSpeedDialList = speedDialObjects;
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
