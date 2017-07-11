package br.com.bttest;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ModuleListActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listModule = getListView();
        ArrayAdapter<Module> listAdapter = new ArrayAdapter<Module>(
                this, android.R.layout.simple_list_item_1, Module.modules);
        listModule.setAdapter(listAdapter);
    }


}

class Module{

    private String name;

    public static final Module[] modules = {
            new Module("Moduł1"), new Module("Moduł2")
    };

    Module(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

