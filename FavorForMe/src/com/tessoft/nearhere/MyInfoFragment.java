package com.tessoft.nearhere;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tessoft.common.Constants;
import com.tessoft.common.TaxiArrayAdapter;
import com.tessoft.common.TaxiPostReplyListAdapter;
import com.tessoft.common.UploadTask;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.Post;
import com.tessoft.domain.User;
import com.tessoft.domain.UserLocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MyInfoFragment extends BaseFragment implements OnClickListener {

	TaxiArrayAdapter adapter = null;
	View rootView = null;
	ListView listMain = null;
	View header = null;
	View footer = null;
	ObjectMapper mapper = new ObjectMapper();
	int UPDATE_USER_MOBILE_NO = 10;
	protected static final int UPDATE_USER_NAME = 20;
	User user = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			rootView = inflater.inflate(R.layout.activity_user_profile, container, false);

			header = getActivity().getLayoutInflater().inflate(R.layout.user_profile_list_header1, null);
			footer = getActivity().getLayoutInflater().inflate(R.layout.fragment_messagebox_footer, null);

			listMain = (ListView) rootView.findViewById(R.id.listMain);
			listMain.addHeaderView(header);
			listMain.addFooterView(footer, null, false );

			adapter = new TaxiArrayAdapter( getActivity(), this, 0 );
			listMain.setAdapter(adapter);

			initializeComponent();

			inquiryUserInfo();
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}

		return rootView;
	}

	private void inquiryUserInfo() throws IOException, JsonGenerationException,
	JsonMappingException {
		User user = getLoginUser();
		getActivity().setProgressBarIndeterminateVisibility(true);
		sendHttp("/taxi/getUserInfo.do", mapper.writeValueAsString( user ), 1);
	}

	private void initializeComponent() {

		TextView txtPickHomeLocation = (TextView) rootView.findViewById(R.id.txtPickHomeLocation);
		txtPickHomeLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					Intent intent = new Intent( getActivity(), SetDestinationActivity.class);
					intent.putExtra("title", "집 위치 선택");
					intent.putExtra("subTitle", "위치를 선택해 주십시오.");
					intent.putExtra("anim1", R.anim.stay );
					intent.putExtra("anim2", R.anim.slide_out_to_bottom );
					startActivityForResult(intent, 1 );
					getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
				}
				catch( Exception ex )
				{
					catchException(MyInfoFragment.this, ex);
				}
			}
		});

		TextView txtPickOfficeLocation = (TextView) rootView.findViewById(R.id.txtPickOfficeLocation);
		txtPickOfficeLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					Intent intent = new Intent( getActivity(), SetDestinationActivity.class);
					intent.putExtra("title", "직장 위치 선택");
					intent.putExtra("subTitle", "위치를 선택해 주십시오.");
					intent.putExtra("anim1", R.anim.stay );
					intent.putExtra("anim2", R.anim.slide_out_to_bottom );
					startActivityForResult(intent, 2 );
					getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
				}
				catch( Exception ex )
				{
					catchException(MyInfoFragment.this, ex);
				}
			}
		});

		TextView txtUpdateJobTitle = (TextView) rootView.findViewById(R.id.txtUpdateJobTitle);
		txtUpdateJobTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					EditText edtJobTitle = (EditText) header.findViewById(R.id.edtJobTitle);
					User user = getLoginUser();
					user.setJobTitle( edtJobTitle.getText().toString() );
					getActivity().setProgressBarIndeterminateVisibility(true);
					sendHttp("/taxi/updateUserJobTitle.do", mapper.writeValueAsString(user), 1);		
				}
				catch( Exception ex )
				{
					catchException(MyInfoFragment.this, ex);
				}
			}
		});

		TextView txtChangeBirthday = (TextView) header.findViewById(R.id.txtChangeBirthday);
		txtChangeBirthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					TextView txtView = (TextView) v;
					RelativeLayout layoutBirthday = (RelativeLayout) header.findViewById(R.id.layoutBirthday);

					if ("변경하기".equals(txtView.getText()))
					{
						txtView.setText("취소");
						layoutBirthday.setVisibility(ViewGroup.VISIBLE);
					}
					else
					{
						txtView.setText("변경하기");
						layoutBirthday.setVisibility(ViewGroup.GONE);
					}
				}
				catch( Exception ex )
				{
					catchException(MyInfoFragment.this, ex);
				}
			}
		});

		TextView txtSetDatePicker = (TextView) header.findViewById(R.id.txtSetDatePicker);
		txtSetDatePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					TextView txtChangeBirthday = (TextView) header.findViewById(R.id.txtChangeBirthday);
					RelativeLayout layoutBirthday = (RelativeLayout) header.findViewById(R.id.layoutBirthday);
					TextView txtBirthday = (TextView) header.findViewById(R.id.txtBirthday);
					layoutBirthday.setVisibility(ViewGroup.GONE);
					txtChangeBirthday.setText("변경하기");

					DatePicker dp = (DatePicker) header.findViewById(R.id.datepicker);
					Date date = new Date( dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth());

					User user = getLoginUser();
					user.setBirthday(Util.getDateStringFromDate(date, "yyyy-MM-dd"));
					txtBirthday.setText( user.getBirthday() );

					getActivity().setProgressBarIndeterminateVisibility(true);
					sendHttp("/taxi/updateUserBirthday.do", mapper.writeValueAsString( user ), 3 );
				}
				catch( Exception ex )
				{
					catchException(MyInfoFragment.this, ex);
				}
			}
		});

		listMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Post post = (Post) arg1.getTag();

				Intent intent = new Intent( getActivity(), TaxiPostDetailActivity.class);
				intent.putExtra("postID", post.getPostID());
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			}
		});

		ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
		imgProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					zoomImageFromThumb( v );
				}
				catch( Exception ex )
				{
					catchException(this, ex);
				}
			}
		});
		
		Button btnChangeProfileImage = (Button) header.findViewById(R.id.btnChangeProfileImage);
		btnChangeProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
				}
				catch( Exception ex )
				{
					catchException(this, ex);
				}
			}
		});
		
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		
		Button btnChangeMobileNo = (Button) header.findViewById(R.id.btnChangeMobileNo);
		btnChangeMobileNo.setOnClickListener(this);
		
		Button btnChangeName = (Button) header.findViewById(R.id.btnChangeName);
		btnChangeName.setOnClickListener( this );
	}

	private static final int PICK_IMAGE = 1;

	private static final int PROFILE_IMAGE_UPLOAD = 6;

	public boolean bDataFirstLoaded = false;
	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			if ( Constants.FAIL.equals(result) )
			{
				getActivity().setProgressBarIndeterminateVisibility(false);
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			getActivity().setProgressBarIndeterminateVisibility(false);

			super.doPostTransaction(requestCode, result);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == 1 )
				{
					bDataFirstLoaded = true;

					HashMap hash = (HashMap) response.getData();

					String userString = mapper.writeValueAsString( hash.get("user") );
					String locationListString = mapper.writeValueAsString( hash.get("locationList") );
					String userPostString = mapper.writeValueAsString( hash.get("userPost") );
					String postsUserRepliedString = mapper.writeValueAsString( hash.get("postsUserReplied") );

					user = mapper.readValue(userString, new TypeReference<User>(){});
					List<UserLocation> locationList = mapper.readValue(locationListString, new TypeReference<List<UserLocation>>(){});
					List<Post> userPosts = mapper.readValue(userPostString, new TypeReference<List<Post>>(){});
					List<Post> userPostsReplied = mapper.readValue(postsUserRepliedString, new TypeReference<List<Post>>(){});
					
					ArrayList postList = new ArrayList();
					HashMap postKeys = new HashMap();
					
					for ( int i = 0; i < userPosts.size(); i++ )
					{
						if ( !postKeys.containsKey( userPosts.get(i).getPostID() ) )
						{
							postList.add( userPosts.get(i) );
							postKeys.put( userPosts.get(i).getPostID(), "exist" );
						}
					}
					
					for ( int i = 0; i < userPostsReplied.size(); i++ )
					{
						if ( !postKeys.containsKey( userPostsReplied.get(i).getPostID() ) )
						{
							postList.add( userPostsReplied.get(i) );
							postKeys.put( userPostsReplied.get(i).getPostID(), "exist" );
						}
					}

					ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
					imgProfile.setImageResource(R.drawable.no_image);

					if ( user != null && user.getProfileImageURL() != null && user.getProfileImageURL().isEmpty() == false )
					{
						ImageLoader.getInstance().displayImage( Constants.imageServerURL + 
								user.getProfileImageURL() , imgProfile);
						imgProfile.setTag( user.getProfileImageURL() );
					}

					TextView txtUserName = (TextView) header.findViewById(R.id.txtUserName);

					if ( Util.isEmptyString( user.getUserName() ) )
						txtUserName.setText( user.getUserID() );
					else
						txtUserName.setText( user.getUserName() + " (" + user.getUserID() + ")" );

					TextView txtCreditValue = (TextView) header.findViewById(R.id.txtCreditValue);
					txtCreditValue.setText( user.getProfilePoint() + "%");

					if ( user.getBirthday() != null && !"".equals( user.getBirthday() ) )
					{
						String birthday = Util.getFormattedDateString(user.getBirthday(),"yyyy-MM-dd", "yyyy.MM.dd");
						TextView txtBirthday = (TextView) header.findViewById(R.id.txtBirthday );
						txtBirthday.setText( birthday );
					}

					for ( int i = 0; i < locationList.size(); i++ )
					{
						UserLocation loc = locationList.get(i);
						if ( "집".equals( loc.getLocationName() ) )
						{
							TextView txtHomeLocation = (TextView) header.findViewById(R.id.txtHomeLocation);
							txtHomeLocation.setText( loc.getAddress() );
						}
						else if ( "직장".equals( loc.getLocationName() ))
						{
							TextView txtOfficeLocation = (TextView) header.findViewById(R.id.txtOfficeLocation);
							txtOfficeLocation.setText( loc.getAddress() );
						}
					}

					ImageView imgSex = (ImageView) header.findViewById(R.id.imgSex);
					TextView txtSex = (TextView) header.findViewById(R.id.txtSex);

					if ( "M".equals( user.getSex() ))
					{
						imgSex.setImageResource(R.drawable.male);
						txtSex.setText("남자");
					}
					else if ( "F".equals( user.getSex() ))
					{
						imgSex.setImageResource(R.drawable.female);
						txtSex.setText("여자");
					}
					else
						imgSex.setVisibility(ViewGroup.GONE);

					if ( user.getJobTitle() != null && !"".equals( user.getJobTitle() ))
					{
						EditText edtJobTitle = (EditText) header.findViewById(R.id.edtJobTitle);
						edtJobTitle.setText( user.getJobTitle() );
					}

					TextView txtMobileNo = (TextView) header.findViewById(R.id.txtMobileNo);
					if ( !Util.isEmptyString( user.getMobileNo() ))
					{
						txtMobileNo.setText( user.getMobileNo() );
					}
					else
						txtMobileNo.setText("");
					
					adapter.clear();
					adapter.addAll(postList);
					adapter.notifyDataSetChanged();

					if ( postList.size() == 0 )
					{
						listMain.removeFooterView(footer);
						listMain.addFooterView(footer, null, false );
						TextView txtView = (TextView) footer.findViewById(R.id.txtGuide);
						txtView.setText("합승내역이 없습니다.");
					}
					else
						listMain.removeFooterView(footer);
				}
				else
				{
					inquiryUserInfo();
				}
			}
			else
			{
				showOKDialog("경고", response.getResMsg(), null);
				return;
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			super.onActivityResult(requestCode, resultCode, data);

			if ( requestCode == 1 || requestCode ==2 )
			{
				String selectedAddress = data.getExtras().get("address").toString();
				LatLng location = (LatLng) data.getExtras().get("location");

				if ( requestCode == 1 )
				{
					TextView txtHomeLocation = (TextView) header.findViewById(R.id.txtHomeLocation);
					txtHomeLocation.setText( selectedAddress );

					User user = getLoginUser();
					UserLocation userLocation = new UserLocation();
					userLocation.setUser( user );
					userLocation.setLocationName("집");
					userLocation.setLatitude( String.valueOf( location.latitude ));
					userLocation.setLongitude( String.valueOf( location.longitude ));
					userLocation.setAddress(selectedAddress);
					getActivity().setProgressBarIndeterminateVisibility(true);
					sendHttp("/taxi/updateUserLocation.do", mapper.writeValueAsString( userLocation ), 2);
				}
				else if ( requestCode == 2 )
				{
					TextView txtOfficeLocation = (TextView) header.findViewById(R.id.txtOfficeLocation);
					txtOfficeLocation.setText( selectedAddress );

					User user = getLoginUser();
					UserLocation userLocation = new UserLocation();
					userLocation.setUser( user );
					userLocation.setLocationName("직장");
					userLocation.setLatitude( String.valueOf( location.latitude ));
					userLocation.setLongitude( String.valueOf( location.longitude ));
					userLocation.setAddress(selectedAddress);
					getActivity().setProgressBarIndeterminateVisibility(true);
					sendHttp("/taxi/updateUserLocation.do", mapper.writeValueAsString( userLocation ), 2);
				}	
			}
			else if ( requestCode == 3 )
			{
				if( data != null && data.getData() != null) {
					Uri _uri = data.getData();

					//User had pick an image.
					Cursor cursor = getActivity().getContentResolver().query(_uri, 
							new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, 
							null, null, null);
					cursor.moveToFirst();

					//Link to the image
					final String imageFilePath = cursor.getString(0);
					cursor.close();

					Bitmap myBitmap = BitmapFactory.decodeFile( imageFilePath );
					myBitmap = resizeBitmapImageFn( myBitmap, 1024 );
					sendPhoto( myBitmap );
				}

			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	private void sendPhoto(Bitmap f) throws Exception {
		User user = getLoginUser();
		new UploadTask( getActivity(), this, user.getUserID() , PROFILE_IMAGE_UPLOAD ).execute(f);
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				inquiryUserInfo();
			}
			catch( Exception ex )
			{
				catchException(this, ex);
			}
		}
	};

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getActivity().registerReceiver(mMessageReceiver, new IntentFilter("refreshContents"));
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		getActivity().unregisterReceiver(mMessageReceiver);
	}
	
	/*
	 * 비트맵(Bitmap) 이미지의 가로, 세로 이미지를 리사이징
	 * @param bmpSource 원본 Bitmap 객체
	 * @param maxResolution 제한 해상도
	 * @return 리사이즈된 이미지 Bitmap 객체
	 */
	public Bitmap resizeBitmapImageFn(
	Bitmap bmpSource, int maxResolution){ 
	        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
	        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
	        int newWidth = iWidth ;
	        int newHeight = iHeight ;
	        float rate = 0.0f;
	        
	        //이미지의 가로 세로 비율에 맞게 조절
	        if(iWidth > iHeight ){
	                if(maxResolution < iWidth ){ 
	                        rate = maxResolution / (float) iWidth ; 
	                        newHeight = (int) (iHeight * rate); 
	                        newWidth = maxResolution; 
	                }
	        }else{
	                if(maxResolution < iHeight ){
	                        rate = maxResolution / (float) iHeight ; 
	                        newWidth = (int) (iWidth * rate);
	                        newHeight = maxResolution;
	                }
	        }
	 
	        return Bitmap.createScaledBitmap(
	bmpSource, newWidth, newHeight, true); 
	}
	
	/**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     *
     * <ol>
     *   <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     *   <li>Calculate the starting and ending bounds for the expanded view.</li>
     *   <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     *       simultaneously, from the starting bounds to the ending bounds.</li>
     *   <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     *
     * @param thumbView  The thumbnail view to zoom in.
     * @param imageResId The high-resolution version of the image represented by the thumbnail.
     */
	
	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;
	
    private void zoomImageFromThumb(final View thumbView ) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        
        String imageURL = "";
        if ( thumbView.getTag() == null || thumbView.getTag() instanceof String == false ) return;
        
        imageURL = thumbView.getTag().toString();
        if ( Util.isEmptyString( imageURL ) ) return;

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) rootView.findViewById(R.id.expanded_image);
        ImageLoader.getInstance().displayImage( Constants.imageServerURL + 
        		imageURL, expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        rootView.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnChangeMobileNo )
			{
				openMobileInputDialog();
			}
			else if ( v.getId() == R.id.btnChangeName )
			{
				openChangeNameDialog();
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	AlertDialog mobileInputDialog = null;
	
	public void openMobileInputDialog() throws Exception
	{
		LayoutInflater li = LayoutInflater.from( getActivity() );
		View promptsView = li.inflate(R.layout.fragment_user_mobile_input, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );
		alertDialogBuilder.setView(promptsView);
		
		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);
		
		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setTitle("휴대폰 번호 입력")
			.setPositiveButton("확인", null )
			.setNegativeButton("취소",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		mobileInputDialog = alertDialogBuilder.create();

		mobileInputDialog.setOnShowListener( new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
				Button b = mobileInputDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try
						{
							if ( TextUtils.isEmpty( userInput.getText() ) )
							{
								userInput.setError("입력한 번호가 올바르지 않습니다.");
								return;
							}
							
							getActivity().setProgressBarIndeterminateVisibility(true);
							user.setMobileNo(userInput.getText().toString());
							sendHttp("/taxi/updateUserInfo.do", mapper.writeValueAsString( user ), UPDATE_USER_MOBILE_NO );
						}
						catch( Exception ex )
						{
							catchException(this, ex);
						}
						finally{
							mobileInputDialog.dismiss();
						}
					}
				});
			}
		});
		// show it
		mobileInputDialog.show();
	}
	
	public void openChangeNameDialog()
	{
		showSimpleInputDialog("이름 입력", "이름을 입력해 주세요.", user.getUserName(), "변경하고자 하는 이름을 입력해 주십시오.", 
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog dialog = null;
				
				try
				{
					if ( v.getTag() instanceof AlertDialog )
					{
						dialog = (AlertDialog) v.getTag();
						
						EditText edtSimpleInput = (EditText) dialog.findViewById(R.id.edtSimpleInput);
						if ( TextUtils.isEmpty( edtSimpleInput.getText() ) )
						{
							edtSimpleInput.setError("입력값이 올바르지 않습니다.");
							return;
						}
						
						if ( user == null )
						{
							showOKDialog("경고", "사용자 정보가 올바르지 않습니다.", null);
							return;
						}
						
						getActivity().setProgressBarIndeterminateVisibility(true);
						user.setUserName(edtSimpleInput.getText().toString());
						sendHttp("/taxi/updateUserInfo.do", mapper.writeValueAsString( user ), UPDATE_USER_NAME );
					}
				}
				catch( Exception ex )
				{
					catchException(this, ex);
				}

				if (dialog != null )
					dialog.dismiss();
			}
		});
	}
}
