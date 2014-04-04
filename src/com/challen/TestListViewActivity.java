package com.challen;

import java.util.Vector;

import cindy.android.test.synclistview.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TestListViewActivity extends Activity implements
		AdapterView.OnItemClickListener {

	ListView viewBookList;
	BookItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		viewBookList = (ListView) findViewById(R.id.viewBookList);

		adapter = new BookItemAdapter(this, viewBookList);

		viewBookList.setAdapter(adapter);
		viewBookList.setOnItemClickListener(this);
		reload();
	}

	private void reload() {
		adapter.clean();
		// loadStateView.startLoad();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadDate();
				sendMessage(REFRESH_LIST);
			}
		}).start();
	}

	public void loadDate() {
		for (int i = 0; i < 100; i++) {
			adapter.addBook("我是challen的测试异步加" + i, "1",
					"http://ww1.sinaimg.cn/thumbnail/80ab1ad3gw1dx8tfjvbgdj.jpg");

			adapter.addBook("小美" + i, "2",
					"http://ww2.sinaimg.cn/thumbnail/7f9fd9a9jw1dtyrqrh4mjj.jpg");

			adapter.addBook("金总" + i, "3",
					"http://ww3.sinaimg.cn/thumbnail/9d57e8e4jw1dx6topumz5j.jpg");

			adapter.addBook("创意铺子" + i, "4",
					"http://www.pfwx.com/files/article/image/3/3237/3237s.jpg");

			adapter.addBook("人名日报" + i, "5",
					"http://ww2.sinaimg.cn/thumbnail/9263d293jw1dx8snx58s7j.jpg");

			adapter.addBook("名字是乱明的" + i, "6",
					"http://tp1.sinaimg.cn/1660452532/50/5646449168/0");
			adapter.addBook("帅哥即将出现" + i, "7",
					"http://p1.qhimg.com/t01a869bb64c7f3d8c6.png");
			adapter.addBook("注意了哦" + i, "8",
					"http://www.baidu.com/img/baidu_jgylogo3.gif");
			adapter.addBook("来拉" + i, "9",
					"http://tp4.sinaimg.cn/2190322767/50/5605436918/1");
			adapter.addBook("这个就是我啦" + i, "10",
					"http://avatar.csdn.net/E/7/2/3_jkingcl.jpg");

		}
	}

	private static final int REFRESH_LIST = 0x10001;
	public static final int SHOW_STR_TOAST = 0;
	public static final int SHOW_RES_TOAST = 1;

	private Handler pichandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				handleOtherMessage(msg.what);
			}
		}
	};

	public void sendMessage(int flag) {
		pichandler.sendEmptyMessage(flag);
	}

	protected void handleOtherMessage(int flag) {
		switch (flag) {
		case REFRESH_LIST:
			adapter.notifyDataSetChanged();
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public class BookItemAdapter extends BaseAdapter {

		public class BookModel {
			public String book_id;
			public String out_book_url;
			public String author;
			public String book_state_s;
			public String leading_role;
			public String update_time;
			public String book_name;
			public String out_book_pic;
			public String sort_id;
			public String last_update_section_title;
			public String last_update_section_url;
			public String introduction;
		}

		private LayoutInflater mInflater;
		private Vector<BookModel> mModels = new Vector<BookModel>();
		private ListView mListView;
		SyncImageLoader syncImageLoader;

		public BookItemAdapter(Context context, ListView listView) {
			mInflater = LayoutInflater.from(context);
			syncImageLoader = new SyncImageLoader();
			mListView = listView;
			
			/*
			 *
			 * 这一句话取消掉注释的话，那么能更加的节省资源，不过体验稍微有点，
			 * 你滑动的时候不会读取图片，当手放开后才开始度图片速度更快，你们可以试一试
			 * */
			
			// mListView.setOnScrollListener(onScrollListener);
		}

		public void addBook(String book_name, String author, String out_book_pic) {
			BookModel model = new BookModel();
			model.book_name = book_name;
			model.author = author;
			model.out_book_pic = out_book_pic;
			mModels.add(model);
		}

		public void clean() {
			mModels.clear();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mModels.size();
		}

		@Override
		public Object getItem(int position) {
			if (position >= getCount()) {
				return null;
			}
			return mModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_adapter,
						null);
			}
			BookModel model = mModels.get(position);
			convertView.setTag(position);
			ImageView iv = (ImageView) convertView.findViewById(R.id.sItemIcon);
			TextView sItemTitle = (TextView) convertView
					.findViewById(R.id.sItemTitle);
			TextView sItemInfo = (TextView) convertView
					.findViewById(R.id.sItemInfo);
			sItemTitle.setText(model.book_name);
			sItemInfo.setText(model.out_book_url);
			// 添加�?��背景在滑动的时�?就会显示背景而不是其他的缓存的照片，用户体验更好
			iv.setBackgroundResource(R.drawable.rc_item_bg);
			syncImageLoader.loadImage(position, model.out_book_pic,
					imageLoadListener, model.author);
			return convertView;
		}

		SyncImageLoader.OnImageLoadListener imageLoadListener = new SyncImageLoader.OnImageLoadListener() {

			@Override
			public void onImageLoad(Integer t, Drawable drawable) {
				// BookModel model = (BookModel) getItem(t);
				View view = mListView.findViewWithTag(t);
				if (view != null) {
					ImageView iv = (ImageView) view
							.findViewById(R.id.sItemIcon);
					iv.setBackgroundDrawable(drawable);
				}
			}

			@Override
			public void onError(Integer t) {
				BookModel model = (BookModel) getItem(t);
				View view = mListView.findViewWithTag(model);
				if (view != null) {
					ImageView iv = (ImageView) view
							.findViewById(R.id.sItemIcon);
					iv.setBackgroundResource(R.drawable.rc_item_bg);
				}
			}

		};

		public void loadImage() {
			int start = mListView.getFirstVisiblePosition();
			int end = mListView.getLastVisiblePosition();
			if (end >= getCount()) {
				end = getCount() - 1;
			}
			syncImageLoader.setLoadLimit(start, end);
			syncImageLoader.unlock();
		}

		AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					syncImageLoader.lock();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					loadImage();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					syncImageLoader.lock();
					break;

				default:
					break;
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		};
	}

}
