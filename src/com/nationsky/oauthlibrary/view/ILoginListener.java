package com.nationsky.oauthlibrary.view;

import com.nationsky.oauthlibrary.OAuthParameters;

public interface ILoginListener {
	void OK(OAuthParameters parameters);
	void Cancel();
}
