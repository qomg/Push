/*
* Copyright (C) 2014 The Android Open Source Project
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

package com.boanda.tool.push.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.boanda.tool.push.bean.PushMessage;
import com.boanda.tool.push.service.PushService;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 消息列表展示页面
 * @author 苏浩
 *
 */
public abstract class DisplayFragment extends Fragment {


    protected List<PushMessage> mDataset;
    protected ListView mListView;
    protected ItemAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	mListView = new ListView(getActivity());
    	mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        mAdapter = new ItemAdapter();
        mListView.setAdapter(mAdapter);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	// Initialize data set, this data would usually come from a local content provider or
        // remote server.
		initDataset();
		if(mAdapter != null){
			mAdapter.addItems(mDataset);
		}
    }
    	
    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
    	try {//
			mDataset = x.getDb(PushService.daoConfig).selector(PushMessage.class)
							.where("alias", "=", "system")//FIXME 替换alias
							.orderBy("date", true).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
    }
    
    public abstract void onItemClick(PushMessage pushMessage);
    
    class ItemAdapter extends BaseAdapter{

		public class ViewHolder  {

            public  TextView tvTitle;
            public  TextView tvContent;
            public  View itemView;
            
            public ViewHolder(View itemView) {
//                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                this.itemView = itemView;
                this.itemView.setTag(this);
            }
            
            public void bindValue(final PushMessage item){
            	try {
    				tvTitle.setText(item.getTitle());
    				tvContent.setText(item.getContent().getText());
    			} catch (Exception e) {
    				// handle exception
    			}
            	itemView.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					onItemClick(item);
    				}
    			});
            }
        } 

        private List<PushMessage> mItems = new ArrayList<PushMessage>();
    	
    	public void addItems(List<PushMessage> items){
    		if(items != null && items.size() > 0){
            	mItems.addAll(items);
            	notifyDataSetChanged();
    		}
    	}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public PushMessage getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				//convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.touch_item, parent, false);
				viewHolder = new ViewHolder(convertView);
			}
			viewHolder.bindValue(mItems.get(position));
			
			return convertView;
		}
    	
    }

    
}
