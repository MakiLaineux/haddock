/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pantagruel.megaoutrage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.activities.ManageListActivity;
import com.pantagruel.megaoutrage.data.Statement;

import java.util.ArrayList;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private final LayoutInflater mInflater;
    private ArrayList<Statement> mStatementList;
    private Context mContext;
    private static final String TAG = App.TAG + StatementAdapter.class.getSimpleName();

    public StatementAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        Log.d(TAG, "Adapter : constructor");
    }

    @Override
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_statement, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        // fill from the ArrayList
        holder.vhStatementText.setText(mStatementList.get(position).getText());

        // Color of the mark bar
        if (mStatementList.get(position).getStatus() == App.STATUS_MARKED)
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.color_marked);
        else
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.color_fond);
    }

    @Override
    public int getItemCount() {
        return (mStatementList == null) ? 0 : mStatementList.size();
    }


    public void setStatementList(ArrayList<Statement> statementList) {
        mStatementList = statementList;
        Log.d(TAG, "Adapter : setStatementList, size = "+mStatementList.size());
    }

    public Statement getStatementFromPosition (int pos){
        if (mStatementList == null)
            return null;
        else
            return mStatementList.get(pos);
    }

    /**
     *  Custom view holder
     */
    class StatementViewHolder extends RecyclerView.ViewHolder {
        final TextView vhStatementText;
        final View vhStatementBarreFavori;
        StatementViewHolder(View itemView) {
            super(itemView);
            vhStatementText = itemView.findViewById(R.id.text);
            vhStatementBarreFavori = itemView.findViewById(R.id.barrefavori);
        }
    }
}
