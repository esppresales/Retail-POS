package com.printer.epos.rtpl.Utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.R;
import com.printer.epos.rtpl.RetailPosLoging;
import com.printer.epos.rtpl.UiController;

public class CustomDialog {


    /**
     * Method to show dialog with one button
     */
    public void showOneButtonDialog(final Context context, String boldMessage,
                                    String simpleMessage, String button_name, final DialogButtonListener mDialogButtonClickListener) {
        try {

            final Dialog dialog = new Dialog(context);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER_HORIZONTAL
                    | Gravity.CENTER_VERTICAL);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    context.getResources().getDrawable(
                            R.drawable.dilaog_circular_corner));
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            int deviceWidth = displayMetrics.widthPixels;
            int deviceHeight = displayMetrics.heightPixels;
            WindowManager.LayoutParams params = dialog.getWindow()
                    .getAttributes();

            params.width = deviceWidth - (int) (deviceWidth * .2f);

            params.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(params);

            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView simple_text_field = (TextView) dialog
                    .findViewById(R.id.simpleMessage);
            TextView bold_text_field = (TextView) dialog
                    .findViewById(R.id.boldMessage);
            View horizontalSeprator = (View) dialog
                    .findViewById(R.id.horizontalSeperatorView);
            dialog.findViewById(R.id.view).setVisibility(View.GONE);
            Button cancel_button = (Button) dialog.findViewById(R.id.cancel);
            cancel_button.setVisibility(View.GONE);
            Button ok_button = (Button) dialog.findViewById(R.id.ok);
            RelativeLayout.LayoutParams ok_button_param = (RelativeLayout.LayoutParams) ok_button
                    .getLayoutParams();
            ok_button_param.height = (int) (deviceHeight * .1f);
            ok_button.setLayoutParams(ok_button_param);
            ok_button.setText(button_name);
            // Setting height, width and margin

            int margin = (int) (deviceHeight * .02f);
            if (boldMessage != null && simpleMessage == null) {

                RelativeLayout.LayoutParams boldParam = (RelativeLayout.LayoutParams) bold_text_field
                        .getLayoutParams();
                boldParam.bottomMargin = margin + margin;
                boldParam.leftMargin = margin;
                boldParam.rightMargin = margin;
                boldParam.topMargin = margin;

                boldParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bold_text_field.setLayoutParams(boldParam);
                bold_text_field.setMinLines(3);
                // bold_text_field.setPadding(margin,margin,margin,margin+margin);
            } else if (boldMessage == null && simpleMessage != null) {
                RelativeLayout.LayoutParams simpleParam = (RelativeLayout.LayoutParams) simple_text_field
                        .getLayoutParams();
                simpleParam.bottomMargin = margin + margin;
                simpleParam.leftMargin = margin;
                simpleParam.rightMargin = margin;
                simpleParam.topMargin = margin;

                simpleParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                simple_text_field.setLayoutParams(simpleParam);
                // simple_text_field.setPadding(margin,margin,margin,margin+margin);
                simple_text_field.setMinLines(3);
            } else {
                RelativeLayout.LayoutParams boldParam = (RelativeLayout.LayoutParams) bold_text_field
                        .getLayoutParams();
                boldParam.bottomMargin = margin;
                boldParam.leftMargin = margin;
                boldParam.rightMargin = margin;
                boldParam.topMargin = margin;
                bold_text_field.setLayoutParams(boldParam);
                // simple_text_field.setPadding(margin,margin,margin,margin);

                RelativeLayout.LayoutParams simpleParam = (RelativeLayout.LayoutParams) simple_text_field
                        .getLayoutParams();
                simpleParam.bottomMargin = margin + margin;
                simpleParam.leftMargin = margin;
                simpleParam.rightMargin = margin;

                simple_text_field.setLayoutParams(simpleParam);
                // simple_text_field.setPadding(margin,0,margin,margin+margin);

            }

            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    if (mDialogButtonClickListener != null) {
                        mDialogButtonClickListener.onPositiveClick();
                    }

                }
            });
            // Setting values
            // ok_button.setText(button_name);
            if (simpleMessage != null) {
                simple_text_field.setText(simpleMessage);
                simple_text_field.setVisibility(View.VISIBLE);
            } else {
                simple_text_field.setVisibility(View.GONE);
            }

            if (boldMessage != null) {
                bold_text_field.setText(boldMessage);
                bold_text_field.setVisibility(View.VISIBLE);
            } else {
                bold_text_field.setVisibility(View.GONE);
            }

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    // if(context instanceof BaseActivity){
                    // ((BaseActivity)context).onBackPressed();
                    // }else if(context instanceof BaseFragmentActivity){
                    // ((BaseFragmentActivity)context).onBackPressed();
                    // }

                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(CustomDialog.class.getName(), e);
        }

    }

    public void inputIpAddressDialog(final Context context, final SavePreferences savePrefs) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.ip_address_input_dialog_layout);
            dialog.getWindow().setBackgroundDrawable(
                    context.getResources().getDrawable(
                            R.drawable.dilaog_circular_corner));

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            final EditText ipAddressET = (EditText) dialog.findViewById(R.id.ipAddr);


            TextView cancel_button = (TextView) dialog.findViewById(R.id.cancelDialog);
            TextView save_button = (TextView) dialog.findViewById(R.id.saveCategory);

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    String ipAddress = ipAddressET.getText().toString().trim();
                    if (new Validation().checkValidation((ViewGroup) dialog.findViewById(R.id.container))) {
                        UiController.appUrl = null;
                        UiController.appUrl = "http://" + ipAddress.trim() + UiController.appString;
                        savePrefs.store_ip(ipAddress);
                        dialog.dismiss();
                    }
                }
            });


            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();

                }
            });

            dialog.show();
            if (savePrefs.get_ip() != null) {
                ipAddressET.setText(savePrefs.get_ip());
            }
        } catch (Exception ex) {
            RetailPosLoging.getInstance().registerLog(CustomDialog.class.getName(), ex);
        }
    }

    public Dialog getDialog(final Context context, int layout) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(
                context.getResources().getDrawable(
                        R.drawable.dilaog_circular_corner));

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }



    public void editCategoryDialog(final Context context, final String id,
                                   final String productCategory,
                                   final String description, final String markAsDelete,
                                   final EditCategoryListener mCategoryChangedListener, final boolean add) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_category_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    context.getResources().getDrawable(
                            R.drawable.dilaog_circular_corner));

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            View view = dialog.findViewById(R.id.container);
            setupUI(view, context);

            final EditText categoryET = (EditText) dialog.findViewById(R.id.productCategory);
            final EditText productDescriptionET = (EditText) dialog.findViewById(R.id.productDescription);


            TextView cancel_button = (TextView) dialog.findViewById(R.id.cancelDialog);
            TextView save_button = (TextView) dialog.findViewById(R.id.saveCategory);

            categoryET.setText(productCategory);
            productDescriptionET.setText(description);

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (new Validation().checkValidation((ViewGroup) dialog.findViewById(R.id.container))) {
                        String title = categoryET.getText().toString().trim();
                        String description = productDescriptionET.getText().toString().trim();

                        if (add)
                            mCategoryChangedListener.onCategorySaved(title, description);
                        else
                            mCategoryChangedListener.onCategoryUpdate(id, title, description, markAsDelete);
                        dialog.dismiss();
                    }
                }
            });


            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();

                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(CustomDialog.class.getName(), e);
        }

    }

    /**
     * Method to show dialog with two button
     */
    public void showTwoButtonDialog(final Context context, String boldMessage,
                                    String simpleMessage, String positiveButtonName,
                                    String negativeButtonName,
                                    final DialogButtonListener mDialogButtonClickListener) {
        try {
            final Dialog dialog = new Dialog(context);

            // Typeface mFont = getFont(context);

            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER_HORIZONTAL
                    | Gravity.CENTER_VERTICAL);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    context.getResources().getDrawable(
                            R.drawable.dilaog_circular_corner));
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            int deviceWidth = displayMetrics.widthPixels;
            int deviceHeight = displayMetrics.heightPixels;
            WindowManager.LayoutParams params = dialog.getWindow()
                    .getAttributes();

            params.width = deviceWidth - (int) (deviceWidth * .2f);

            params.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(params);

            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            TextView simple_text_field = (TextView) dialog
                    .findViewById(R.id.simpleMessage);
            TextView bold_text_field = (TextView) dialog
                    .findViewById(R.id.boldMessage);
            View centerView = (View) dialog.findViewById(R.id.view);
            centerView.setVisibility(View.VISIBLE);
            Button cancel_button = (Button) dialog.findViewById(R.id.cancel);
            Button ok_button = (Button) dialog.findViewById(R.id.ok);

            RelativeLayout.LayoutParams ok_button_param = (RelativeLayout.LayoutParams) ok_button
                    .getLayoutParams();
            ok_button_param.height = (int) (deviceHeight * .1f);
            ok_button.setLayoutParams(ok_button_param);

            RelativeLayout.LayoutParams cancel_button_param = (RelativeLayout.LayoutParams) cancel_button
                    .getLayoutParams();
            cancel_button_param.height = (int) (deviceHeight * .1f);
            cancel_button.setLayoutParams(cancel_button_param);

            RelativeLayout.LayoutParams centerView_param = (RelativeLayout.LayoutParams) centerView
                    .getLayoutParams();
            centerView_param.height = (int) (deviceHeight * .1f);
            centerView.setLayoutParams(centerView_param);

            // Setting height, width and margin

            int margin = (int) (deviceHeight * .02f);
            if (boldMessage != null && simpleMessage == null) {
                RelativeLayout.LayoutParams boldParam = (RelativeLayout.LayoutParams) bold_text_field
                        .getLayoutParams();
                boldParam.bottomMargin = margin + margin;
                boldParam.leftMargin = margin;
                boldParam.rightMargin = margin;
                boldParam.topMargin = margin;
                boldParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

                bold_text_field.setLayoutParams(boldParam);
                // bold_text_field.setPadding(margin,margin,margin,margin+margin);
                bold_text_field.setMinLines(3);
            } else if (boldMessage == null && simpleMessage != null) {
                RelativeLayout.LayoutParams simpleParam = (RelativeLayout.LayoutParams) simple_text_field
                        .getLayoutParams();
                simpleParam.bottomMargin = margin + margin;
                simpleParam.leftMargin = margin;
                simpleParam.rightMargin = margin;
                simpleParam.topMargin = margin;
                simpleParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

                simple_text_field.setLayoutParams(simpleParam);
                // simple_text_field.setPadding(margin,margin,margin,margin+margin);
                simple_text_field.setMinLines(3);
            } else {
                RelativeLayout.LayoutParams boldParam = (RelativeLayout.LayoutParams) bold_text_field
                        .getLayoutParams();
                boldParam.bottomMargin = margin;
                boldParam.leftMargin = margin;
                boldParam.rightMargin = margin;
                boldParam.topMargin = margin;
                bold_text_field.setLayoutParams(boldParam);
                // bold_text_field.setPadding(margin,margin,margin,margin);
                RelativeLayout.LayoutParams simpleParam = (RelativeLayout.LayoutParams) simple_text_field
                        .getLayoutParams();
                simpleParam.bottomMargin = margin + margin;
                simpleParam.leftMargin = margin;
                simpleParam.rightMargin = margin;

                simple_text_field.setLayoutParams(simpleParam);
                // simple_text_field.setPadding(margin,0,margin,margin);
            }

            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();

                    if (mDialogButtonClickListener != null) {
                        mDialogButtonClickListener.onPositiveClick();
                    }

                }
            });

            cancel_button.setVisibility(View.VISIBLE);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();

                    if (mDialogButtonClickListener != null) {
                        mDialogButtonClickListener.onNegativeClick();
                    }
                }
            });

            // Setting values
            if (simpleMessage != null) {
                simple_text_field.setText(simpleMessage);
                simple_text_field.setVisibility(View.VISIBLE);
            } else {
                simple_text_field.setVisibility(View.GONE);
            }

            if (boldMessage != null) {
                bold_text_field.setText(boldMessage);
                bold_text_field.setVisibility(View.VISIBLE);
            } else {
                bold_text_field.setVisibility(View.GONE);
            }
            ok_button.setText(positiveButtonName);
            cancel_button.setText(negativeButtonName);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    // if(context instanceof BaseActivity){
                    // ((BaseActivity)context).onBackPressed();
                    // }else if(context instanceof BaseFragmentActivity){
                    // ((BaseFragmentActivity)context).onBackPressed();
                    // }

                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            RetailPosLoging.getInstance().registerLog(CustomDialog.class.getName(), e);
        }

    }

    public void showTwoButtonAlertDialog(final Context context, String title, String message,
                                         String positiveButtonName, String negativeButtonName, int alertIcon,
                                         final DialogButtonListener mDialogButtonClickListener) {
        String pBS = context.getResources().getString(android.R.string.yes);
        String nBS = context.getResources().getString(android.R.string.no);


        if (!TextUtils.isEmpty(positiveButtonName))
            pBS = positiveButtonName;

        if (!TextUtils.isEmpty(negativeButtonName))
            nBS = negativeButtonName;


        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Dialog);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(pBS, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                if (mDialogButtonClickListener != null) {
                    mDialogButtonClickListener.onPositiveClick();
                }
            }
        })
                .setNegativeButton(nBS, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        if (mDialogButtonClickListener != null) {
                            mDialogButtonClickListener.onNegativeClick();
                        }
                    }
                });
        builder.setIcon(alertIcon);
//                .setIcon(android.R.drawable.ic_dialog_alert)
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showTwoButtonAlertDialog(final Context context,boolean cancelable, String title, String message,
                                         String positiveButtonName, String negativeButtonName, int alertIcon,
                                         final DialogButtonListener mDialogButtonClickListener) {
        String pBS = context.getResources().getString(android.R.string.yes);
        String nBS = context.getResources().getString(android.R.string.no);


        if (!TextUtils.isEmpty(positiveButtonName))
            pBS = positiveButtonName;

        if (!TextUtils.isEmpty(negativeButtonName))
            nBS = negativeButtonName;


        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Dialog);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(pBS, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                if (mDialogButtonClickListener != null) {
                    mDialogButtonClickListener.onPositiveClick();
                }
            }
        })
                .setNegativeButton(nBS, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        if (mDialogButtonClickListener != null) {
                            mDialogButtonClickListener.onNegativeClick();
                        }
                    }
                });
        builder.setIcon(alertIcon);
//                .setIcon(android.R.drawable.ic_dialog_alert)
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    AlertDialog oneButtonAlertDialog;
    public void showOneButtonAlertDialog(final Context context, String title, String message,
                                         String buttonName, int alertIcon,
                                         final DialogButtonListener mDialogButtonClickListener) {
        if(oneButtonAlertDialog != null && oneButtonAlertDialog.isShowing()){
//            oneButtonAlertDialog.dismiss();
            oneButtonAlertDialog.hide();
        }
        String pBS = context.getResources().getString(android.R.string.yes);


        if (!TextUtils.isEmpty(buttonName))
            pBS = buttonName;


        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Dialog);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(pBS, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                if (mDialogButtonClickListener != null) {
                    mDialogButtonClickListener.onPositiveClick();
                }
            }
        });
        builder.setIcon(alertIcon);
//        builder.setIcon(android.R.drawable.ic_dialog_alert);

        oneButtonAlertDialog = builder.create();
        oneButtonAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        oneButtonAlertDialog.show();
    }

    protected void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.requestFocus();
    }

    public void setupUI(final View view, final Context context) {
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
}
