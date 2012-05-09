package chaves.android;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;

public class RefreshTimeLineImplementation implements IRefreshTimeLine {
	
	private TimelineActivity _activity;
	
	public RefreshTimeLineImplementation(TimelineActivity activity){
		_activity = activity;
	}
	
	public void refresh(List<Status> list) {
		_activity.refreshTimeline(list);
		
	}

}
