package jinhyuk_kim.chosun.polarble;

import android.util.Log;

import java.util.Date;
import java.util.LinkedList;

public class BioHarnessSessionData {
	long startingTS;
	long endingTS;
	int lastRRvalue;
	int totalNN;
	int totalpNNx;
	long sessionId;
	//Vector<BioHarnessRRData> nnX = new Vector <BioHarnessRRData>();
	LinkedList <Integer>beats = new LinkedList<Integer>();
	
	public BioHarnessSessionData(){	
		reset();
	}	

	public void reset(){
		Log.w(this.getClass().getName(), "## Start reset()");
		sessionId = (new Date()).getTime();
		startingTS = 0;
		endingTS = 0;
		lastRRvalue=0;
		totalNN=0;
		totalpNNx=0;
		beats.clear();
	}	

	public boolean updateBeat(int period, Integer beat){ //1 meets the pNN, 0 not NN
		if(period<=0)
			return false;
		
		boolean displayUpdate=false;
		beats.add(0, beat);	//0 is the beggining and beats.size() is the last
		
		if(beats.size()>period){
			Integer item = beats.remove(period);
			if(beat.intValue()!=item.intValue()){
				displayUpdate = true;
			}
		}	
		return displayUpdate;
	}

	public int getNN(){ 
		int nn=0;
		for(int i=0; i<beats.size(); i++){
			int beat = beats.get(i).intValue();
			if(beat==1)
				nn++;
		}
		return nn;
	}

	public int getPNN(int nn){ 		
		if(beats.size()==0)
			return 0;
		
		return (100*nn)/beats.size();
	}
}
