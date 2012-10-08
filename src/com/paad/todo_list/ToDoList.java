package com.paad.todo_list;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ToDoList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        
        ListView myListView = (ListView)findViewById(R.id.myListView);
        final EditText myEditText = (EditText)findViewById(R.id.myEditText);
        
        final ArrayList<String> todoItems = new ArrayList<String>();
        int resID = R.layout.todolist_item;
        
        final ArrayAdapter<String> aa = new ArrayAdapter<String>(this, resID, todoItems);
        
        myListView.setAdapter(aa);
        
        myEditText.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if(event.getAction() == KeyEvent.ACTION_DOWN)
        			if(keyCode == KeyEvent.KEYCODE_ENTER) {
        				todoItems.add(0, myEditText.getText().toString());
        				aa.notifyDataSetChanged();
        				myEditText.setText("");
        				return true;
        			}
        		return false;
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_to_do_list, menu);
        return true;
    }

    
}
