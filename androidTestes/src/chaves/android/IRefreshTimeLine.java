package chaves.android;

import java.util.List;
import winterwell.jtwitter.Twitter;

public interface IRefreshTimeLine {
	
	public void refresh(List<Twitter.Status> list);

}
