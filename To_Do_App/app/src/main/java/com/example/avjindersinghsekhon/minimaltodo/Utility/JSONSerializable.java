package com.example.avjindersinghsekhon.minimaltodo.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/*
* Tamara Charchoghlyan
* Interface that ToDOItem and BoardItem implmement
* Allows for StoreRetrieveData.toJSONArray() to accept genetic type
*/
public interface JSONSerializable {
    JSONObject toJSON() throws JSONException;
}
