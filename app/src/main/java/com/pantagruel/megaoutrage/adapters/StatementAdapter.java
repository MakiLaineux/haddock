package com.pantagruel.megaoutrage.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;

import java.util.ArrayList;

/*
 * Custom Adapter for a RecyclerView
 * Data is kept in an ArrayList<Statement> which has to be furnished with the setStatementList method
 *
 */

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private final LayoutInflater mInflater;
    private ArrayList<Statement> mStatementList;
    private Context mContext;
    private static final String TAG = App.TAG + StatementAdapter.class.getSimpleName();

    public StatementAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    // Create a view holder
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_statement, parent, false);
        return new StatementViewHolder(itemView);
    }

    // fills a view holder with data and customize the view appearance
    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        // fill from the ArrayList
        holder.vhStatementText.setText(mStatementList.get(position).getText());

        // Set different styles for marked and non-marked items
        if (mStatementList.get(position).getStatus() == Statement.STATUS_MARKED){
            holder.vhCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_card_accent));
            holder.vhCardView.setCardElevation(20f);
            holder.vhStatementText.setTypeface(null, Typeface.BOLD);
            holder.vhStatementText.setTextColor(mContext.getResources().getColor(R.color.color_gray_dark));
        }
        else {
            holder.vhCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_card_light));
            holder.vhCardView.setCardElevation(0f);
            holder.vhStatementText.setTypeface(null, Typeface.NORMAL);
            holder.vhStatementText.setTextColor(mContext.getResources().getColor(R.color.color_gray_medium));
        }
    }

    @Override
    public int getItemCount() {
        return (mStatementList == null) ? 0 : mStatementList.size();
    }


    public void setStatementList(ArrayList<Statement> statementList) {
        mStatementList = statementList;
        //Log.d(TAG, "Adapter : setStatementList, size = "+mStatementList.size());
    }

    // to get the data when you have the position in the adapter
    public Statement getStatementFromPosition (int pos){
        if (mStatementList == null)
            return null;
        else
            return mStatementList.get(pos);
    }

    // to get the adapter position using its text
    public int getPosFromStatementUsingText(Statement statement) {
        if (statement == null) return -1;
        for(int i = 0; i < mStatementList.size(); i++) {
            if(mStatementList.get(i).getText().equals(statement.getText())) return i;
        }
        return -1; //not found
    }

    /**
     *  Custom view holder
     */
    class StatementViewHolder extends RecyclerView.ViewHolder {
        final TextView vhStatementText;
        final CardView vhCardView;
        StatementViewHolder(View itemView) {
            super(itemView);
            vhStatementText = itemView.findViewById(R.id.text);
            vhCardView = itemView.findViewById(R.id.cv);
        }
    }
}
