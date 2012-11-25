package com.paad.todo_list;

import java.util.ArrayList;
import java.util.Date;

import android.R.bool;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.widget.AdapterView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ToDoList extends Activity {

	static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	
    private boolean addingNew = false;
	private ArrayList<ToDoItem> todoItems;
	private ListView myListView;
	private EditText myEditText;
	private ToDoItemAdapter aa;
	
	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	
	ToDoDBAdapter toDoDBAdapter;
	Cursor toDoListCursor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
    	myListView = (ListView)findViewById(R.id.myListView);
        myEditText = (EditText)findViewById(R.id.myEditText);
        
        todoItems = new ArrayList<ToDoItem>();
        int resID = R.layout.todolist_item;
        
        aa = new ToDoItemAdapter(this, resID, todoItems);
        
        myListView.setAdapter(aa);
        
        myEditText.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if(event.getAction() == KeyEvent.ACTION_DOWN)
        			if(keyCode == KeyEvent.KEYCODE_ENTER) {
        				ToDoItem newItem = new ToDoItem(myEditText.getText().toString());
        				//todoItems.add(0, newItem);
        				long temp_index; 
        				temp_index = toDoDBAdapter.insertTask(newItem);
        				//Log.w("TaskDBAdapter", "temp_index: " + temp_index); 
        				updateArray();
        				myEditText.setText("");
        				aa.notifyDataSetChanged();
        				cancelAdd();
        				return true;
        			}
        		return false;
        	}
        });
        
        registerForContextMenu(myListView);
        
        restoreUIState();
        
		toDoDBAdapter = new ToDoDBAdapter(this);
		toDoDBAdapter.open();
		populateToDoList();
    }
    
	private void populateToDoList() {
		toDoListCursor = toDoDBAdapter.getAllToDoItemsCursor();
		startManagingCursor(toDoListCursor);
		
		updateArray();
	}

	private void updateArray() {
		toDoListCursor.requery();
		
		todoItems.clear();
		
		if(toDoListCursor.moveToFirst()) {
			do {
				long index = toDoListCursor.getLong(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_ID));
				//Log.w("TaskDBAdapter", "updateArray index: " + index); 
				String task = toDoListCursor.getString(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_TASK));
				long created = toDoListCursor.getLong(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_CREATION_DATE));
				
				ToDoItem newItem = new ToDoItem(task, new Date(created));
				todoItems.add(0, newItem);
			} while (toDoListCursor.moveToNext());
			
			aa.notifyDataSetChanged();
		}
		
	}

    private void restoreUIState() {
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		
		String text = settings.getString(TEXT_ENTRY_KEY, "");
		Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
		
		if(adding) {
			addNewItem();
			myEditText.setText(text);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE, R.string.add_new);
    	MenuItem itemRemove = menu.add(1, REMOVE_TODO, Menu.NONE, R.string.remove);
    	
    	itemAdd.setIcon(R.drawable.add);
    	itemRemove.setIcon(R.drawable.remove);
    	
    	itemAdd.setShortcut('0', 'a');
    	itemRemove.setShortcut('1', 'r');
    	
        //getMenuInflater().inflate(R.menu.activity_to_do_list, menu);
    	
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	menu.setHeaderTitle("Select To Do Item");
    	menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	
    	int idx = myListView.getSelectedItemPosition();
    	
    	String remoteTitle = getString(addingNew ? R.string.cancel : R.string.remove);
    	
    	MenuItem removeItem = menu.findItem(REMOVE_TODO);
    	removeItem.setTitle(remoteTitle);
    	removeItem.setVisible(addingNew || idx > -1);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	int index = myListView.getSelectedItemPosition();
    	
    	switch (item.getItemId()) {
	    	case (REMOVE_TODO) : {
	    		if(addingNew) {
	    			cancelAdd();
	    		} else {
	    			removeItem(index);
	    		}

	    		return true;
	    	}
	    	case (ADD_NEW_TODO) : {
	    		addNewItem();
	    		return true;
	    	}
    	}
    	return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	super.onContextItemSelected(item);
    	
    	switch (item.getItemId()) {
    		case(REMOVE_TODO) : {
	    		AdapterView.AdapterContextMenuInfo menuInfo;
	    		menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    			
	    		int index = menuInfo.position;
	    		removeItem(index);
	    		
	    		return true;
    		}
    	}
    	return false;
    }
    
	private void cancelAdd() {
		addingNew = false;
		myEditText.setVisibility(View.GONE);
	}
	
	private void addNewItem() {
		addingNew = true;
		myEditText.setVisibility(View.VISIBLE);
		myEditText.requestFocus();
	}

	private void removeItem(int _index) {
		//todoItems.remove(_index);
		//aa.notifyDataSetChanged();
		toDoDBAdapter.removeTask(todoItems.size() - _index); // BUG! If add 5 tasks and delete first 3, then ID will start from 4, but with this condition, will get numbers from 1 :(
		updateArray();
	}
    
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences uiState = getPreferences(0);
		SharedPreferences.Editor editor = uiState.edit();
		
		editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
		editor.putBoolean(ADDING_ITEM_KEY, addingNew);
		
		editor.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstance) {
		savedInstance.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
		super.onSaveInstanceState(savedInstance);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		int pos = -1;
		
		if(savedInstanceState != null) {
			if(savedInstanceState.containsKey(SELECTED_INDEX_KEY)) {
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
			}
		}
		myListView.setSelection(pos);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		toDoDBAdapter.close();
	}
}
