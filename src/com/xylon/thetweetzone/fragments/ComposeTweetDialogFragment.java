package com.xylon.thetweetzone.fragments;




import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.models.User;

public class ComposeTweetDialogFragment extends DialogFragment {
	
	private static final int MAX_COUNT = 140;
	EditText etTweet;
	TextView tvTextCount;
	Button btnTweet;
	int totalTweetCount = MAX_COUNT;
	int twitterGrey;
	int twitterBlue;
	int twitterRed;
	String action;
	User userAccount;
	long statusId;
	String replyUserName;
	
	
	 /** Declaring the interface, to invoke a callback function in the implementing activity class */
	PostToTimelineListener postToTimelinelistener;
 
    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface PostToTimelineListener {
        public void onPostToTimeline(String s, long statusId);
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		statusId = 0L;
		userAccount = (User) getArguments().getSerializable("user");
		action = getArguments().getString("action");
		if( action.equals("reply")) {
			statusId = getArguments().getLong("status_id");
			replyUserName = getArguments().getString("reply_user");
		}
		final Dialog dialog = new Dialog(getActivity());	
		twitterGrey = getResources().getColor(R.color.twitterGray);
		twitterBlue = getResources().getColor(R.color.twitterBlue);
		twitterRed = getResources().getColor(R.color.twitterRed);
		customizeDialogAppearance(dialog);
		dialog.show();		
		getViews(dialog);
		setUserInfo(dialog, userAccount);
		disableButton();
		// Add listeners
		addTextWatchToTweet();
		OnTweet();
		postToTimelinelistener = (PostToTimelineListener) getActivity();
		
		return dialog;
	}
	
	private void customizeDialogAppearance(Dialog dialog) {
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.fragment_compose);
		// dialog background color = white
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.WHITE));
		// make it entire screen size
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}
	
	private void getViews(Dialog dialog) {
		// Get all views
		btnTweet = (Button)dialog.findViewById(R.id.btnTweet);
		tvTextCount = (TextView)dialog.findViewById(R.id.tvTextCount);
		etTweet = (EditText)dialog.findViewById(R.id.etTweet);
		
		
	}
	
	private void setUserInfo(Dialog dialog, User userAccount) {
		ImageView ivProfileImage = (ImageView)dialog.findViewById(R.id.ivProfileImage);
		TextView tvScreenName = (TextView)dialog.findViewById(R.id.tvScreenName);
		TextView tvScreenName2 = (TextView)dialog.findViewById(R.id.tvScreenName2);
		ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(userAccount.getProfileImageUrl(), ivProfileImage);
		tvScreenName.setText(userAccount.getName());
		tvScreenName2.setText("@" + userAccount.getScreenName());
		if (action.equals("reply")) {
			// has to have authorized user name
			etTweet.setText("@" + replyUserName);
		} 
	}
	
	private void disableButton() {
		btnTweet.getBackground().setColorFilter(0x4500aced, PorterDuff.Mode.MULTIPLY);
		btnTweet.setEnabled(false);
	}
	
	private void enableButton() {
		btnTweet.getBackground().clearColorFilter();
		btnTweet.getBackground().setColorFilter(0xFF00aced, PorterDuff.Mode.MULTIPLY);
		btnTweet.setEnabled(true);
	}
	
	public void customizeTextCountAppearance(int textcount, int leftCount) {
		tvTextCount.setText(String.valueOf(leftCount) );
		if ( textcount > MAX_COUNT ) {
			tvTextCount.setTextColor(twitterRed);
		} else
			tvTextCount.setTextColor(twitterGrey);
		
	}
	
	public void addTextWatchToTweet() {
		etTweet.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				int numChar = s.length();
				int leftCount = MAX_COUNT - numChar;
				customizeTextCountAppearance(numChar, leftCount);
				if (numChar > 0 && numChar <= MAX_COUNT && !btnTweet.isEnabled())
					enableButton();
				else if (numChar <= 0 && numChar > MAX_COUNT && btnTweet.isEnabled())
					disableButton();
			}
			
		});
	}
	
	public void OnTweet() {
		btnTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Post it timeline 
				postToTimelinelistener.onPostToTimeline(etTweet.getText().toString(), statusId);
				
				// and close dialog
				dismiss();
			}
		});
	}

}
