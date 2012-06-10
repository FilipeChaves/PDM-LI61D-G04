package chaves.android.model;

import java.util.Date;

public class StatusDTO {
	private int _id;
	private String _user;
	private String message;
	private Date _date;
	private String _image;
	
	public String getImage(){
		return _image;
	}
	public void setImage(String image){
		_image=image;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String get_user() {
		return _user;
	}
	public void set_user(String _user) {
		this._user = _user;
	}
	public long get_date() {
		return _date.getTime();
	}
	public void set_date(long date) {
		this._date = new Date(date);
	}
}
