package net.primegap.authexample;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.savagelook.android.UrlJsonAsyncTask;
import com.savagelook.android.UrlJsonAsyncTaskForArray;

public class HomeActivity extends SherlockActivity {
	//users/sign_in
//	private static final String TASKS_URL = "http://tranquil-sands-8533.herokuapp.com/api/v1/tasks.json";
//	private static final String TOGGLE_TASKS_URL = "http://tranquil-sands-8533.herokuapp.com/api/v1/tasks/";
//	private static final String LOGOUT_URL = "http://tranquil-sands-8533.herokuapp.com/api/v1/sessions.json";
	
	private static final String TASKS_URL = "http://tranquil-sands-8533.herokuapp.com/my_opname.json";
	private static final String TOGGLE_TASKS_URL = "http://tranquil-sands-8533.herokuapp.com/opnames/";
	private static final String LOGOUT_URL = "http://tranquil-sands-8533.herokuapp.com/api/v1/sessions.json";
	
	
	
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPreferences.contains("AuthToken")) {
			loadTasksFromAPI(TASKS_URL);
		} else {
			Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_new_task:
			Intent intent = new Intent(HomeActivity.this, NewTaskActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.menu_refresh:
			loadTasksFromAPI(TASKS_URL);
			return true;
		case R.id.menu_logout:
			logoutFromAPI(LOGOUT_URL);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logoutFromAPI(String url) {
		LogoutTask logoutTask = new LogoutTask(HomeActivity.this);
		logoutTask.setMessageLoading("Loggin out...");
		logoutTask.execute(url);

	}

	private void loadTasksFromAPI(String url) {
		GetTasksTask getTasksTask = new GetTasksTask(HomeActivity.this);
		getTasksTask.setMessageLoading("Loading tasks...");
		getTasksTask.setAuthToken(mPreferences.getString("AuthToken", ""));
		getTasksTask.execute(url);
	}

	private void toggleTasksWithAPI(String url) {
		ToggleTaskTask completeTasksTask = new ToggleTaskTask(HomeActivity.this);
		completeTasksTask.setMessageLoading("Updating task...");
		completeTasksTask.execute(url);
	}

	private class TaskAdapter extends ArrayAdapter<Opname> implements
			OnClickListener {

		private ArrayList<Opname> items;
		private int layoutResourceId;

		public TaskAdapter(Context context, int layoutResourceId,
				ArrayList<Opname> items) {
			super(context, layoutResourceId, items);
			this.layoutResourceId = layoutResourceId;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (CheckedTextView) layoutInflater.inflate(
						layoutResourceId, null);
			}
			Opname opname = items.get(position);
			if (opname != null) {
				CheckedTextView taskCheckedTextView = (CheckedTextView) view
						.findViewById(android.R.id.text1);
				if (taskCheckedTextView != null) {
					taskCheckedTextView.setText(opname.getProduct_name());
					//taskCheckedTextView.setChecked(task.getCompleted());
					taskCheckedTextView.setOnClickListener(this);
				}
				view.setTag(opname.getId());
			}
			return view;
		}

		@Override
		public void onClick(View view) {
			CheckedTextView taskCheckedTextView = (CheckedTextView) view
					.findViewById(android.R.id.text1);
			if (taskCheckedTextView.isChecked()) {
				taskCheckedTextView.setChecked(false);
				toggleTasksWithAPI(TOGGLE_TASKS_URL + view.getTag()
						+ "/open.json");
			} else {
				taskCheckedTextView.setChecked(true);
				toggleTasksWithAPI(TOGGLE_TASKS_URL + view.getTag()
						+ "/complete.json");
			}

		}
	}

	private class GetTasksTask extends UrlJsonAsyncTaskForArray {
		public GetTasksTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONArray json) {
			Log.e("Data", json.toString());
			try {
				JSONArray opnames = json;
				JSONObject jsonTask = new JSONObject();
				int length = opnames.length();
				final ArrayList<Opname> tasksArray = new ArrayList<Opname>(length);

				for (int i = 0; i < length; i++) {
//					"id":67,"product_id":1,"location_id":1,"book_count":100,"actual_count":99,"note":null,"url":"http://tranquil-sands-8533.herokuapp.com/opnames/67.json
//						","product":{"name":"Nike Magista Opus Firm Ground 
//						(Volt)"},"location":{"description":"Z1S1H1"}},
					
					//
					jsonTask = opnames.getJSONObject(i);
					String id= jsonTask.getString("id");
					String product_id= jsonTask.getString("product_id");
					String location_id= jsonTask.getString("location_id");
					String book_count= jsonTask.getString("book_count");
					String actual_count= jsonTask.getString("actual_count");
					String note= jsonTask.getString("note");
					String url= jsonTask.getString("url");
					String product= jsonTask.getString("product");
					JSONObject productobj = new JSONObject(product);
					String product_name=productobj.getString("name");
					String location= jsonTask.getString("location");
//					location=location.replace("{", "[");
//					location=location.replace("}", "]");
					
					JSONObject locationobj = new JSONObject(location);
					
					location=locationobj.getString("description");
					

					
					
					
					tasksArray.add(new Opname(product_id, location_id, book_count, actual_count, note, url, location, product_name));
				}

				ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
				if (tasksListView != null) {
					tasksListView.setAdapter(new TaskAdapter(HomeActivity.this,
							android.R.layout.simple_list_item_checked,
							tasksArray));
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	private class ToggleTaskTask extends UrlJsonAsyncTask {
		public ToggleTaskTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPut put = new HttpPut(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					put.setHeader("Accept", "application/json");
					put.setHeader("Content-Type", "application/json");
					put.setHeader("Authorization", "Token token="
							+ mPreferences.getString("AuthToken", ""));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(put, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	private class LogoutTask extends UrlJsonAsyncTask {
		public LogoutTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpDelete delete = new HttpDelete(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					delete.setHeader("Accept", "application/json");
					delete.setHeader("Content-Type", "application/json");
					delete.setHeader("Authorization", "Token token="
							+ mPreferences.getString("AuthToken", ""));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(delete, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("success")) {
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.remove("AuthToken");
					editor.commit();

					Intent intent = new Intent(HomeActivity.this,
							WelcomeActivity.class);
					startActivityForResult(intent, 0);
				}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
}
