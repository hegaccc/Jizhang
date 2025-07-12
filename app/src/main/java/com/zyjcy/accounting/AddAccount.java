package com.zyjcy.accounting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddAccount extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, View.OnTouchListener {

    EditText amountET;
    EditText itemET;
    EditText dateET;
    EditText remarkET;
    Button submitBT;
    Button cancelBT;
    RadioGroup typeRG;
    Account account = new Account();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
//初始化控件
        initView();

    }

    //初始化控件
    private void initView() {
        amountET = findViewById(R.id.amount);
        itemET = findViewById(R.id.item);
        dateET = findViewById(R.id.dateET);
        dateET.setText(getTodayYmd());
        dateET.setOnTouchListener(this);
        remarkET = findViewById(R.id.remark);
        submitBT = findViewById(R.id.submit);
        submitBT.setOnClickListener(this);
        cancelBT = findViewById(R.id.cancel);
        cancelBT.setOnClickListener(this);
        typeRG = findViewById(R.id.type);
        typeRG.setOnCheckedChangeListener(this);

        //设置RadioGroup默认值为存钱
        typeRG.check(R.id.income);

    }

    //获取今天的日期
    public static String getTodayYmd() {

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        // 将时分秒,毫秒域清零
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }

    //选择日期组件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_dialog, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            builder.setView(view);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            if (v.getId() == R.id.dateET) {
                final int inType = dateET.getInputType();
                dateET.setInputType(InputType.TYPE_NULL);
                dateET.onTouchEvent(event);
                dateET.setInputType(inType);
                dateET.setSelection(dateET.getText().length());
                /*builder.setTitle("请选择开台时间");*/
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //因为当选择的时间是小于10的话回填的时间只是一位，跟我数据库的时间不符
                        //所以判断小于10的在前面拼接0（如01）
                        //拼接所有选择的时间（最后拼接的：00是秒钟，为了跟数据库类型相同而拼接上的，可根据自己的需求拼接）
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        dateET.setText(sb);//把拼接好的数据回填
                        dialog.cancel();//取消dialog
                    }
                });
            }
            Dialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    //按钮监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit: {

                //拿到录入的数据，并赋值给account对象的对应属性
                account.setDate(String.valueOf(dateET.getText()));
                account.setItem(String.valueOf(itemET.getText()));
                account.setAmount(Double.valueOf(amountET.getText().toString()));
                account.setRemark(String.valueOf(remarkET.getText()));

                //执行插入数据操作
                AccountDBHelper.getInstance(this).insert(account);
                //结束当前activity
                finish();
            }
            break;

            case R.id.cancel: {
                //取消按钮，结束当前activity
                finish();
            }
        }
    }

    //RadioGroup切换时的操作
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.income) {
            account.setType("收入");
        }
        if (checkedId == R.id.takeOut) {
            account.setType("支出");
        }
    }
}