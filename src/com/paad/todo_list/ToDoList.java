package com.paad.todo_list;

import java.util.ArrayList;

import android.R.bool;
import android.os.Bundle;
import android.app.Activity;
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
	private ArrayList<String> todoItems;
	private ListView myListView;
	private EditText myEditText;
	private ArrayAdapter<String> aa;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        
        //ListView myListView = (ListView)findViewById(R.id.myListView);
        myListView = (ListView)findViewById(R.id.myListView);
        //final EditText myEditText = (EditText)findViewById(R.id.myEditText);
        myEditText = (EditText)findViewById(R.id.myEditText);
        
        //final ArrayList<String> todoItems = new ArrayList<String>();
        todoItems = new ArrayList<String>();
        int resID = R.layout.todolist_item;
        
        //final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, resID, todoItems);
        aa = new ArrayAdapter<String>(this, resID, todoItems);
        
        myListView.setAdapter(aa);
        
        myEditText.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if(event.getAction() == KeyEvent.ACTION_DOWN)
        			if(keyCode == KeyEvent.KEYCODE_ENTER) {
        				todoItems.add(0, myEditText.getText().toString());
        				aa.notifyDataSetChanged();
        				myEditText.setText("");
        				cancelAdd();
        				return true;
        			}
        		return false;
        	}
        });
        
        registerForContextMenu(myListView);
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
		todoItems.remove(_index);
		aa.notifyDataSetChanged();
		
	}
    
}
