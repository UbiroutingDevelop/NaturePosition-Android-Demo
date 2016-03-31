package com.ubirouting.ubilocdemo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ubirouting.naturelib.OnGetStoreDataListener;
import com.ubirouting.naturelib.ShituLocationLoader;
import com.ubirouting.naturelib.ShituStore;
import com.ubirouting.naturelib.ShituStoreDatas;

/**
 * This activity shows how to fetch stores that you created in ShiTu Cloud
 * 
 * @author YangTao & Deanllv
 *
 */
public class MainActivity extends Activity implements OnGetStoreDataListener, OnItemClickListener {

	private List<ShituStore> mStores;

	private ListView mStoreList;

	private static final String TAG = "NaturePositionDemo";

	private StoreListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mStoreList = (ListView) findViewById(R.id.stores_list);
		mAdapter = new StoreListAdapter();
		mStoreList.setAdapter(mAdapter);
		mStoreList.setOnItemClickListener(this);

		// This should invoke first
		ShituLocationLoader.getInstance(this);

		ShituStoreDatas storeDatas = new ShituStoreDatas(this);
		storeDatas.addOnGetSHTStoreDataListener(this);
		storeDatas.fetchDatas();
	}

	@Override
	public void onFailed(Exception arg0) {

	}

	@Override
	public void onGetDatas(List arg0) {
		mStores = (List<ShituStore>) (arg0);
		Log.d(TAG, mStores.toString());

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private class StoreListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			Log.d(TAG, "getCount");
			if (mStores != null)
				return mStores.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mStores != null)
				return mStores.get(position);
			else
				return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				TextView textView = new TextView(MainActivity.this);
				textView.setText(mStores.get(position).toString());
				return textView;
			} else {
				return convertView;
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent();
		i.setClass(this, LocateActivity.class);
		Constant.sStorePass = mStores.get(position);

		startActivity(i);
	}

}
