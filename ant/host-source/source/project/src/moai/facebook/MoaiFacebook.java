//----------------------------------------------------------------//
// Copyright (c) 2010-2011 Zipline Games, Inc. 
// All Rights Reserved. 
// http://getmoai.com
//----------------------------------------------------------------//

package com.ziplinegames.moai;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.Facebook.ServiceListener;
import com.facebook.android.FacebookError;

//================================================================//
// MoaiFacebook
//================================================================//
public class MoaiFacebook {

	public enum DialogResultCode {
		
		RESULT_SUCCESS,
	    RESULT_CANCEL,
        RESULT_ERROR;
						
        public static DialogResultCode valueOf ( int index ) {
	
            DialogResultCode [] values = DialogResultCode.values ();
            if (( index < 0 ) || ( index >= values.length )) {
	
                return RESULT_ERROR;
            }

            return values [ index ];
        }
    }

	private static Activity sActivity = null;
	private static Facebook	sFacebook = null;

	protected static native void	AKUNotifyFacebookLoginComplete	( int statusCode );
	protected static native void	AKUNotifyFacebookDialogComplete	( int statusCode );
	protected static native void	AKUNotifyFacebookSessionExtended	( String token, String tokenDate );

	//----------------------------------------------------------------//
	public static void onActivityResult ( int requestCode, int resultCode, Intent data ) {
	
		MoaiLog.i ( "MoaiFacebook onActivityResult: Calling authorize callback" );

		sFacebook.authorizeCallback ( requestCode, resultCode, data );
	}
	
	//----------------------------------------------------------------//
	public static void onCreate ( Activity activity ) {
		
		MoaiLog.i ( "MoaiFacebook onCreate: Initializing Facebook" );
		
		sActivity = activity;
	}
	
	//================================================================//
	// Facebook JNI callback methods
	//================================================================//
	
	//----------------------------------------------------------------//	
	public static void extendToken () {

		MoaiLog.i ( "MoaiFacebook extendToken" );

		sFacebook.extendAccessToken ( sActivity, new ServiceListener () {

			@Override

			public void onComplete(Bundle values) {
				synchronized ( Moai.sAkuLock ) {
					MoaiLog.i ( "MoaiFacebook extendToken onComplete" );
					AKUNotifyFacebookSessionExtended( sFacebook.getAccessToken(), String.valueOf( sFacebook.getAccessExpires() ) );
				}
			}

			@Override
	        public void onFacebookError(FacebookError e) {
				synchronized ( Moai.sAkuLock ) {
		        	MoaiLog.i ( "MoaiFacebook extendToken onFacebookError" );
		        }
	        }

	        @Override
        	public void onError(Error e) {
				synchronized ( Moai.sAkuLock ) {
					MoaiLog.i ( "MoaiFacebook extendToken onError" );
				}
        	}
		} ); 
	}

	//----------------------------------------------------------------//	
	public static String getToken () {

		return sFacebook.getAccessToken (); 
	}

	//----------------------------------------------------------------//	
	public static String getExpires () {
		return String.valueOf( sFacebook.getAccessExpires () );
	}

	//----------------------------------------------------------------//	
	public static void setExpires (String expires) {
		long value;
		try
		{
			value = Long.parseLong( expires );
		} catch ( NumberFormatException e ) {
			value = 0;
		}
		sFacebook.setAccessExpires ( value );
	}
		
	//----------------------------------------------------------------//	
	public static String graphRequest ( String path ) {

		String jsonResult;
		try {
			
			jsonResult = sFacebook.request ( path );
		} catch ( MalformedURLException urle ) {
			
			jsonResult = "Invalid URL";
		} catch ( IOException ioe ) {
			
			jsonResult = "Network Error";
		}
		
		return jsonResult;
	}
	
	//----------------------------------------------------------------//	
	public static void init ( String appId ) {
		
		sFacebook = new Facebook ( appId ); 
		MoaiFacebook.extendToken ();
	}

	//----------------------------------------------------------------//	
	public static boolean isSessionValid () {

		return sFacebook.isSessionValid ();
	}
	
	//----------------------------------------------------------------//	
	public static void login ( String [] permissions ) {
		
		sFacebook.authorize ( sActivity, permissions, new DialogListener () {
			
	        @Override
	        public void onComplete ( Bundle values ) {
						
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookLoginComplete ( DialogResultCode.RESULT_SUCCESS.ordinal() );
				}
	        }
	
	        @Override
	        public void onFacebookError ( FacebookError error ) {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookLoginComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onError ( DialogError e ) {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookLoginComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onCancel () {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookLoginComplete ( DialogResultCode.RESULT_CANCEL.ordinal() );
				}
			}
		});
	}

	//----------------------------------------------------------------//	
	public static void logout () {
		
		try {
			
			sFacebook.logout ( sActivity );
		}
		catch ( Throwable  e ) {
			
		}
	}
	
	//----------------------------------------------------------------//	
	public static void postToFeed ( String link, String picture, String name, String caption, String description, String message ) {

		Bundle parameters = new Bundle ();
		
		if ( link != null )	parameters.putString ( "link", link );
		if ( picture != null )	parameters.putString ( "picture", picture );
		if ( name != null )	parameters.putString ( "name", name );
		if ( caption != null )	parameters.putString ( "caption", caption );
		if ( description != null )	parameters.putString ( "description", description );
		if ( message != null )	parameters.putString ( "message", message );
		
		sFacebook.dialog ( sActivity, "feed", parameters, new DialogListener () {
			
	        @Override
	        public void onComplete ( Bundle values ) {
		
				if ( values.containsKey ( "post_id" )) {
					
					
					synchronized ( Moai.sAkuLock ) {
						AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_SUCCESS.ordinal() );
					}
				} else {
					
					synchronized ( Moai.sAkuLock ) {
						AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_CANCEL.ordinal() );
					}
				}
	        }
	
	        @Override
	        public void onFacebookError ( FacebookError error ) {
		
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onError ( DialogError e ) {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onCancel () {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_CANCEL.ordinal() );
				}
			}
		});
	}
	
	//----------------------------------------------------------------//	
	public static void sendRequest ( String message ) {

		Bundle parameters = new Bundle ();
		
		if ( message != null )	parameters.putString ( "message", message );
		
		sFacebook.dialog ( sActivity, "apprequests", parameters, new DialogListener () {
			
	        @Override
	        public void onComplete ( Bundle values ) {
		
				if ( values.containsKey ( "request" )) {
					
					
					synchronized ( Moai.sAkuLock ) {
						AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_SUCCESS.ordinal() );
					}
				} else {
					
					synchronized ( Moai.sAkuLock ) {
						AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_CANCEL.ordinal() );
					}
				}
	        }
	
	        @Override
	        public void onFacebookError ( FacebookError error ) {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onError ( DialogError e ) {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_ERROR.ordinal() );
				}
			}
	
	        @Override
	        public void onCancel () {
				
				synchronized ( Moai.sAkuLock ) {
					AKUNotifyFacebookDialogComplete ( DialogResultCode.RESULT_CANCEL.ordinal() );
				}
			}
		});
	}
	
	//----------------------------------------------------------------//	
	public static void setToken ( String token ) {

		sFacebook.setAccessToken ( token ); 
	}
}