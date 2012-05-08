package chaves.android;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailsModel implements Parcelable {

	private HashMap<String, String> _map;

	public DetailsModel(){
		_map = new HashMap<String, String>();
	}
	
	public DetailsModel(Parcel in){
		_map = new HashMap<String, String>();
		readFromParcel(in);
	}

	public int describeContents() {
		return 0;
	}

	private void readFromParcel(Parcel in) {
		 int count = in.readInt();
	        for (int i = 0; i < count; i++) {
	            _map.put(in.readString(), in.readString());
	        }
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_map.size());
        for (String s: _map.keySet()) {
            dest.writeString(s);
            dest.writeString(_map.get(s));
        }

	}

	public static final Parcelable.Creator CREATOR =
			new Parcelable.Creator() {
		public DetailsModel createFromParcel(Parcel in) {
			return new DetailsModel(in);
		}

		public DetailsModel[] newArray(int size) {
			return new DetailsModel[size];
		}
	};
	//##################################################################################################
	//#                                             GET/SET                                            #
	//##################################################################################################


	public String get(String key) {
        return _map.get(key);
    }
 
    public void put(String key, String value) {
        _map.put(key, value);
    }
    
    public void putMap(HashMap<String,String> map){
    	_map = map;
    }

}
