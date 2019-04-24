package com.ford.cvs.caq.client.widget;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.ford.cvs.caq.client.R;

public class PmTipDialog extends Dialog
{

	private Context ctx;
	
	public PmTipDialog(Context context) {
		super(context);
		this.ctx = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	private void init(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pm_tips_dialog);

		
		//setCanceledOnTouchOutside(true);// 设置点击dialog外面的界面 关闭dialog
		Window dialogWindow = this.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		lp.width =dm.widthPixels;
		//lp.height = (int) (0.21 * dm.heightPixels);
		dialogWindow.setAttributes(lp);
	}
}
