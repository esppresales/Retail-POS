package com.printer.epos.rtpl.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.printer.epos.rtpl.R;

/**
 * Created by hp pc on 02-05-2015.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView title;
    private int layoutId;
    private View mView;

    private int TOGGLE_BUTTON_WIDTH = 0;
    private int TOGGLE_BUTTON_HEIGHT = 0;

    public BaseDialog(Context context, int layoutId) {
        super(context);
        this.context = context;
        this.layoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_dialog_layout);
        getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mView = injectView(layoutId);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.container);
        setupUI(rl, context);

        getWidthAndHeightOfToggle();

        TextView save = (TextView) findViewById(R.id.saveCategory);
        TextView cancel = (TextView) findViewById(R.id.cancelDialog);
        title = (TextView) findViewById(R.id.title);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveCategory)
            onSaveClick();
        else if (view.getId() == R.id.cancelDialog)
            onCancelClick();
    }

    protected void setTitle(String text) {
        title.setText(text);
    }

    protected void onSaveClick() {

    }

    protected void onCancelClick() {
        dismiss();
    }

    private View injectView(int id) {
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(id);
        return stub.inflate();
    }

    protected View getView() {
        return mView;
    }

    private void setupUI(final View view, final Context context) {
        view.setFocusableInTouchMode(true);

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view, context);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, context);
            }
        }
    }

    private void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.requestFocus();
    }

    private void getWidthAndHeightOfToggle() {
        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_off_button_bg);
        TOGGLE_BUTTON_HEIGHT = bd.getBitmap().getHeight();
        TOGGLE_BUTTON_WIDTH = bd.getBitmap().getWidth();
    }

    protected void setToggleDimension(ToggleButton button) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) button.getLayoutParams();
        param.height = TOGGLE_BUTTON_HEIGHT;
        param.width = TOGGLE_BUTTON_WIDTH;
        button.setLayoutParams(param);
    }
}
