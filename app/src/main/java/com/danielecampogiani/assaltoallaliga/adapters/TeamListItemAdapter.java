package com.danielecampogiani.assaltoallaliga.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.danielecampogiani.assaltoallaliga.R;
import com.danielecampogiani.assaltoallaliga.model.Team;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by danielecampogiani on 14/01/15.
 */
public class TeamListItemAdapter extends RecyclerView.Adapter<TeamListItemAdapter.ViewHolder> {


    private final Team[] mDataSet;
    private Context mContext;
    private int mSelectedItem;

    public TeamListItemAdapter(Team[] myDataSet, Context myContext) {
        if (myDataSet == null)
            throw new NullPointerException("myDataSet can't be null");
        if (myContext == null)
            throw new NullPointerException("myContext can't be null");
        mDataSet = myDataSet;
        mContext = myContext;
        mSelectedItem = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_in_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Team item = mDataSet[position];

        holder.textView.setText(item.getName());
        Drawable transparentDrawable = new ColorDrawable(android.R.color.transparent);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, mContext.getResources().getDisplayMetrics());
        transparentDrawable.setBounds(0, 0, size, size);
        holder.textView.setCompoundDrawables(transparentDrawable, null, null, null);
        final ViewHolder finalHolder = holder;

        if (!item.getLogoPath().equals(mContext.getString(R.string.no_logo_url))) {
            Ion.with(mContext).load(item.getLogoPath()).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    final Drawable drawable = new BitmapDrawable(mContext.getResources(), result);
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, mContext.getResources().getDisplayMetrics());
                    drawable.setBounds(0, 0, size, size);
                    finalHolder.textView.setCompoundDrawables(drawable, null, null, null);
                }
            });
        } else {
            String[] initialsArray = item.getName().split(" ");
            StringBuilder initialsBuilder = new StringBuilder();
            if (initialsArray.length >= 2) {
                initialsBuilder.append(initialsArray[0].charAt(0));
                initialsBuilder.append(initialsArray[initialsArray.length - 1].charAt(0));
            } else
                initialsBuilder.append(item.getName().charAt(0));
            Drawable drawable = TextDrawable.builder().buildRect(initialsBuilder.toString().toUpperCase(), ColorGenerator.DEFAULT.getColor(item.getName()));
            //int intrinsicH = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, size, size);
            holder.textView.setCompoundDrawables(drawable, null, null, null);
        }

        if (mSelectedItem >= 0) {
            if (mSelectedItem == position)
                holder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
            else
                holder.textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public void setSelectedItem(int mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textView);
        }

    }


}
