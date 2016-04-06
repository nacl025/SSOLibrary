package com.nationsky.oauthlibrary;

import android.os.Bundle;

public interface IOAuthListener {

	public void onComplete(Bundle values);

	public void onError(OAuthError error);

	public void onCancel();

}
