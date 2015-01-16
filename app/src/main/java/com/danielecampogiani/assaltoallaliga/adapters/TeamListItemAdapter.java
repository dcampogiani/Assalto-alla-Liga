package com.danielecampogiani.assaltoallaliga.adapters;

import android.content.Context;
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

/**
 * Created by danielecampogiani on 14/01/15.
 */
public class TeamListItemAdapter extends RecyclerView.Adapter<TeamListItemAdapter.ViewHolder> {


    private final Team[] mDataSet;
    private Context mContext;
    private int mSelectedItem;

    public TeamListItemAdapter(Team[] myDataSet, Context myContext) {
        mDataSet = myDataSet;
        mContext = myContext;
        mSelectedItem = -1;
    }

    private static Drawable getImage(Context context, String name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
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

        if (item.getLogoName() != null)
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(getImage(mContext, item.getLogoName()), null, null, null);
        else {
            String[] initialsArray = item.getName().split(" ");
            StringBuilder initialsBuilder = new StringBuilder();
            if (initialsArray.length >= 2) {
                initialsBuilder.append(initialsArray[0].charAt(0));
                initialsBuilder.append(initialsArray[initialsArray.length - 1].charAt(0));
            } else
                initialsBuilder.append(item.getName().charAt(0));
            Drawable drawable = TextDrawable.builder().buildRect(initialsBuilder.toString().toUpperCase(), ColorGenerator.DEFAULT.getColor(item.getName()));
            //int intrinsicH = drawable.getIntrinsicHeight();
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, mContext.getResources().getDisplayMetrics());
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
