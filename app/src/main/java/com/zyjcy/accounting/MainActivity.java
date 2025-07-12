package com.zyjcy.accounting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView balanceTV;
    TextView incomeTV;
    TextView expenditureTV;
    Button AddAccountBT;

    private AccountDBHelper mHelper;
    List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
    List<Account> list;
    //从数据库中取出来的列名称放入该数组
    private String[] columnNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
    }

//初始化控件
    public void initView() {
        balanceTV = findViewById(R.id.balance);
        incomeTV = findViewById(R.id.income);
        expenditureTV = findViewById(R.id.expenditure);
        AddAccountBT = findViewById(R.id.addAccount);
        AddAccountBT.setOnClickListener(this);
    }

//按钮监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAccount: {
               //跳转到记账页面
                Intent intent = new Intent(MainActivity.this, AddAccount.class);
                startActivity(intent);
            }
            break;
        }
    }


    //并将查询数据展示在listview中
    private void refresh() {
        //查询数据库中所有数据
        list = mHelper.queryAll();
        //拿到元数据（列名称）的数组
        columnNames = mHelper.getColumnNames();

        //刷新账户数据
        double incomeMoney = mHelper.querySUM("收入");
        double takeOutMoney = mHelper.querySUM("支出");
        balanceTV.setText(incomeMoney - takeOutMoney + "");
        incomeTV.setText(incomeMoney + "");
        expenditureTV.setText(takeOutMoney + "");

        //先清空历史数据
        listMap.clear();
        //遍历集合，将每一个Account对象中的数据取出来，以key-value的形式放入map集合，然后将每一个account对应的map放入listMap中
        for (int i = 0; i < list.size(); i++) {
            //LinkedHashMap装入和取出的顺序不会变
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            //columnNames[]数组代表的是从数据库中取出来的列名称
            map.put(columnNames[0], list.get(i).getId());
            map.put(columnNames[1], list.get(i).getDate());
            map.put(columnNames[2], list.get(i).getItem());
            map.put(columnNames[3], list.get(i).getType());
            map.put(columnNames[4], list.get(i).getAmount());
            map.put(columnNames[5], list.get(i).getRemark());

            listMap.add(map);
        }
        showResult(listMap);
    }

    //显示查询数据的列表，并对列表设置监听事件
    private void showResult(List<Map<String, Object>> listMap) {
        //用适配器将list里面的每一个map与listview形成绑定关系，并将其展示。
        //此处的columnNames[]对应的是列名称， 装入new String[]中是将listMap其对应的名字取出来放在listview中
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listMap, R.layout.list_layout_account,
                new String[]{columnNames[1], columnNames[2], columnNames[3], columnNames[4], columnNames[5]},
                new int[]{R.id.date, R.id.item, R.id.type, R.id.amount, R.id.remark});
        ListView listView = (ListView) findViewById(R.id.lv_information);
        listView.setAdapter(simpleAdapter);

        //给listView设置点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //拿到被点击位置对应的map
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                //拿到这组数据的id
                int id1 = (int) map.get("id");
                //创建弹窗方法对象
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //用对象调用方法，设置弹窗信息（先拿到listMap里面被点击那个对象）
                builder.setMessage("删除后不能恢复，您确定删除本条记录吗？\n\n")
                        //设置弹窗中文字按钮的点击监听事件
                        .setPositiveButton("狠心删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //通过id删除该组数据
                                mHelper.deleteById(id1);
                                //刷新显示界面的数据和列表数据
                                refresh();
                            }
                        })
                        .setNegativeButton("算了算了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                //创建弹窗并展示
                builder.create().show();

            }
        });
    }

//activity启动时的操作
    @Override
    protected void onStart() {
        super.onStart();

        //获取数据库帮助器的实例
        mHelper = AccountDBHelper.getInstance(this);
        //打开数据库读写连接
        mHelper.openWriteLink();
        mHelper.openReadLink();
        //查询数据库中所有数据，并刷新列表和余额数据。
        refresh();
    }

    //activity关闭时的操作
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭数据库连接
        mHelper.closeLink();
    }
}