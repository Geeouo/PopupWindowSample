package com.gee.popupwindowsample;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText et_input;
    boolean isSHowingMxNaviKeybroad = false;//是否正在展示自定义布局键盘 默认false
    boolean isNeedRemoveCursorToEnd = false;//是否需要移动输入框的光标到末尾
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_input = findViewById(R.id.et_input);
        initKeyBroad(et_input);
    }

    public void showMxNaviKeybroad() {
        //加载键盘布局
        View keybroadView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_keybroad, null);
        //初始化键盘布局控件
        initKeybroadView(keybroadView);
        //创建popup实例
        popupWindow = new PopupWindow(keybroadView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //再次设置backgrounddrawable,避免不同机型上出现bug
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.keybroadcolor)));
        //设置获取焦点false，edittext需要
        popupWindow.setFocusable(false);
        //设置外部可触摸
        popupWindow.setOutsideTouchable(true);
        //设置进出场动画
        popupWindow.setAnimationStyle(R.style.anim_Popupwindow);
        //展示
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
        //设置popup#dismiss()监听器
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //popup关闭时重置isSHowingMxNaviKeybroad标志位
                isSHowingMxNaviKeybroad = false;
            }
        });
    }

    int i = 0;

    private void initKeybroadView(View keyboradView) {
        //LinearLayout rootView = keyboradView.findViewById(R.id.root);
        TextView btn = keyboradView.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = et_input.getText();
                editable.insert(et_input.getSelectionStart(), "new" + (i++));
                //et_input.setText(et + " new" + (i++));
                Toast.makeText(MainActivity.this, "btn click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public static void hideSystemSofeKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                //固定写法
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }

    public void initKeyBroad(final EditText et) {
        et.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                 /*int inType = et.getInputType(); // backup the input type
                 et.setInputType(InputType.TYPE_NULL); // disable soft input
                 et.onTouchEvent(event); // call native handler
                 et.setInputType(inType); // restore input type*/

                 //触摸会触发初次ontouch() 添加标志位控制
                if (!isSHowingMxNaviKeybroad) {
                    hideSystemSofeKeyboard(et);
                    showMxNaviKeybroad();
                    isSHowingMxNaviKeybroad = true;
                    Log.e("geetag", "onTouch: ");
                }
                //true 无光标 false有光标
                return false;
            }
        });
        //2.设置光标的问题
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //判断光标是否在文本中间
                //如果光标在文本中间，则不需要每次都重置光标到eidttext end位置
                if (et.getSelectionStart() < et.getText().length())
                    isNeedRemoveCursorToEnd = false;
                else isNeedRemoveCursorToEnd = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果光标在文本中间，则不需要每次都重置光标到eidttext end位置
                if (isNeedRemoveCursorToEnd)
                    et.setSelection(et.length());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //因为edittext需要焦点，所以popupwindow需要setFoucsable(false)
        //所以为了监听返回，当mxKeybroad显示时先隐藏掉。模拟setFoucsable(true)
        if (isSHowingMxNaviKeybroad && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        super.onBackPressed();

    }
}