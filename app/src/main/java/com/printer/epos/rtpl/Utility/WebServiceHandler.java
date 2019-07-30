package com.printer.epos.rtpl.Utility;



public interface WebServiceHandler {

	void onSuccess(String response);
	void onError(String error);
	
}
