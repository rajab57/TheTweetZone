package com.xylon.thetweetzone;




import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.xylon.thetweetzone.models.User;

public class ComposeTweetDialogFragment extends DialogFragment {
	
	private static final int MAX_COUNT = 140;
	EditText etTweet;
	TextView tvTextCount;
	Button btnTweet;
	int totalTweetCount = MAX_COUNT;
	
	
	 /** Declaring the interface, to invoke a callback function in the implementing activity class */
	PostToTimelineListener postToTimelinelistener;
 
    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    interface PostToTimelineListener {
        public void onPostToTimeline(String s);
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		User userAccount = (User) getArguments().getSerializable("user");
		final Dialog dialog = new Dialog(getActivity());		
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.fragment_compose);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.WHITE));
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		dialog.show();
		
		// Get all views
		ImageView ivProfileImage = (ImageView)dialog.findViewById(R.id.ivProfileImage);
		TextView tvScreenName = (TextView)dialog.findViewById(R.id.tvScreenName);
		TextView tvScreenName2 = (TextView)dialog.findViewById(R.id.tvScreenName2);
		
		ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		//populate view with tweet data
		imageLoader.displayImage(userAccount.getProfileImageUrl(), ivProfileImage);
		tvScreenName.setText(userAccount.getName());
		tvScreenName2.setText("@" + userAccount.getScreenName());
		
		btnTweet = (Button)dialog.findViewById(R.id.btnTweet);
		tvTextCount = (TextView)dialog.findViewById(R.id.tvTextCount);
		etTweet = (EditText)dialog.findViewById(R.id.etTweet);
		
		disableButton();
		// Add listeners
		addTextWatchToTweet();
		OnTweet();
		postToTimelinelistener = (PostToTimelineListener) getActivity();
		
		return dialog;
	}
	
	private void disableButton() {
		btnTweet.getBackground().setColorFilter(0x454099FF, PorterDuff.Mode.MULTIPLY);
		btnTweet.setEnabled(false);
	}
	
	private void enableButton() {
		btnTweet.getBackground().setColorFilter(0xFF4099FF, PorterDuff.Mode.MULTIPLY);
		btnTweet.setEnabled(true);
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
				System.out.println("numchar " + numChar);
				int leftCount = MAX_COUNT - numChar;
				tvTextCount.setText(String.valueOf(leftCount));
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
				postToTimelinelistener.onPostToTimeline(etTweet.getText().toString());
				
				// and close dialog
				dismiss();
			}
		});
	}

}
