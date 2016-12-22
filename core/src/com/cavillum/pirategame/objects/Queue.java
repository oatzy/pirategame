package com.cavillum.pirategame.objects;

import java.util.ArrayList;
import java.util.Random;

public class Queue{
	private Integer[] _queue;
	private int _currentRound;
	private ArrayList<Integer> _upNext;
	
	public Queue(){
		_queue = new Integer[49];
		_currentRound = 0;
		_upNext = new ArrayList<Integer>(); // player choices
	}
	
	public void reset(){
		_currentRound = 0;
		_upNext.clear();
		generate();
	}
	
	public int getRound(){
		return _currentRound;
	}
	
	public Integer[] getQueue(){
		return _queue;
	}
	
	public int getCurrent(){
		return _queue[_currentRound];
	}
	
	public void setQueue(Integer[] queue){
		if (queue.length==49){
			_queue = queue;
		}
	}
	
	public void setRound(int round){
		if (round>=0 && round<49){ // check for OoB
			_currentRound = round;
		}
	}
	
	public boolean isEnd(){
		return _currentRound >= 48;
	}
	
	public void generate(){
		for (int i=0; i<49; i++){
			_queue[i] = i;
		}
		// shuffle
		int index, temp;
		Random random = new Random();
		for (int i = 48; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			temp = _queue[i];
			_queue[i] = _queue[index];
			_queue[index] = temp;
		}
	}
	
	public int iterate(){
		queueNext(); // if players got 'choose next'
		if (!isEnd()){ // prevent round going OoB
			_currentRound++;
			return _queue[_currentRound];
		} else return -1;
	}
	
	public void addNext(int index){
		// check index isn't already in list
		if (_upNext.indexOf(index)==-1){
			_upNext.add(index);
		}
	}
	
	public void queueNext(){
		// only queue up one upNext per round
		// prevents over-writing
		if (!_upNext.isEmpty()){
			for(int j=_currentRound+1; j<49; j++){
				if (_queue[j] == _upNext.get(0)){
					// swap old next index - since the queue is random 
					// it doesn't matter where the old next is pushed
					_queue[j] = _queue[_currentRound+1];
					_queue[_currentRound+1] = _upNext.get(0);
					_upNext.remove(0);
					return;
				}
			}
		}		
	}
}