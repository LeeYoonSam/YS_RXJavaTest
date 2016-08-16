package com.albert.study.rx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.albert.study.BaseLoadingActivity;
import com.albert.study.R;

public class RxSubjectTest extends BaseLoadingActivity {

	@Nullable @Bind(R.id.tvLogMessage)
	TextView tvLogMessage;


	BehaviorSubject<Integer> behaviorSubject;
	AsyncSubject<Integer> asyncSubject;
	PublishSubject<Integer> publishSubject;
	ReplaySubject<Integer> replaySubjectSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_rx_samples);

		ButterKnife.bind(this);

		//		rxFrom();

		//		behaviorSubjectExample();

		//		asyncSubjectExample();

		//		publishSubjectExample();

		replaySubjectSubjectExample();
	}

	public void rxFrom()
	{
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

		Observable.from(numbers)
		.flatMap(new Func1<Integer, Observable<? extends String>>() {

			@Override
			public Observable<? extends String> call(Integer number) {
				// TODO Auto-generated method stub
				return Observable.just("RX from TEST - numbers =  " + number).debounce(1, TimeUnit.SECONDS);
			}

		})
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Subscriber<String>() {

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				tvLogMessage.setText("From Test Complete!!");
			}

			@Override
			public void onError(Throwable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNext(String text) {

				tvLogMessage.setText(text);

				Log.d("RX Test : ", text);
			}
		});
	}

	/**
	 *  BehaviorSubject - Observable을 구독한 시점의 바로 이전 Item 부터 구독
	 *  
	 *  ex) (1구독) 1,2,3,4,5, (2구독) 6,7,8,9,10
	 *  	1구독 - 1,2,3,4,5,6,7,8,9,10
	 *  	2구독 - 5,6,7,8,9,10
	 *  
	 */
	public void behaviorSubjectExample()
	{
		behaviorSubject = BehaviorSubject.create();

		Observable<String> behaviorTest = getObservable();
		behaviorTest.subscribe(result1);

		Observable<String> behaviorTest2 = getObservable();
		behaviorTest2.subscribe(result2);

		for(int i = 0; i < 20; i ++) 
		{
			behaviorSubject.onNext(i);
		}

		behaviorSubject.onCompleted();
	}

	public Observable<String> getObservable() {
		Observable<String> observable = behaviorSubject
				.map(new Func1<Integer, String>() {
					@Override
					public String call(Integer num) {
						String nextTitle = "BehaviorSubject TEST : " + (num + 1); 						
						return nextTitle;
					}
				});

		return observable;
	}


	/**
	 * AsyncSubject  - Observable을 구독한 시점에서 동기화를 위해 'onComplete()' 호출 후 마지막 Item 부터 구독
	 * 
	 * onComplete()가 없으면 호출안됨.
	 */
	public void asyncSubjectExample()
	{
		asyncSubject = AsyncSubject.create();

		Observable<String> observable = asyncSubject
				.map(new Func1<Integer, String>() {
					@Override
					public String call(Integer num) {
						String nextTitle = "AsyncSubject TEST : " + (num + 1);					
						return nextTitle;
					}
				});

		observable.subscribe(result1);
		//		observable.subscribe(result2);

		for(int i = 0; i < 20; i ++) 
		{
			asyncSubject.onNext(i);

			//			if(i == 10)
			//				observable.subscribe(result2);
		}

		asyncSubject.onCompleted();
	}


	/**
	 * PublishSubject - Observable을 구독한 이후 Item 부터 구독
	 *  ex) (1구독) 1,2,3,4,5, (2구독) 6,7,8,9,10
	 *  	1구독 - 1,2,3,4,5,6,7,8,9,10
	 *  	2구독 - 6,7,8,9,10
	 */
	public void publishSubjectExample()
	{
		publishSubject = PublishSubject.create();

		Observable<String> observable = publishSubject
				.map(new Func1<Integer, String>() {
					@Override
					public String call(Integer num) {
						String nextTitle = "PublishSubject TEST : " + (num + 1);					
						return nextTitle;
					}
				});

		observable.subscribe(result1);

		for(int i = 0; i < 20; i ++) 
		{
			publishSubject.onNext(i);

			if(i == 10)
				observable.subscribe(result2);
		}

		publishSubject.onCompleted();
	}

	/**
	 * PublishSubject - Observable을 구독한 이후 Item 부터 구독
	 * 
	 * 2구독 시점에 1구독은 멈추고 2구독이 1구독이 전달받은 만큼 2구독도 전달받은 후 갯수가 같아지면 한단계씩 진행
	 * 
	 * ex)
	 * ReplaySubjectSubject TEST : 1
	 * ReplaySubjectSubject TEST : 2
	 * ReplaySubjectSubject TEST : 3
	 * ReplaySubjectSubject TEST : 4
	 * ReplaySubjectSubject TEST : 5
	 * ReplaySubjectSubject TEST : 6
	 * ReplaySubjectSubject TEST : 7
	 * ReplaySubjectSubject TEST : 8
	 * ReplaySubjectSubject TEST : 9
	 * ReplaySubjectSubject TEST : 10
	 * ReplaySubjectSubject TEST : 11
	 * ReplaySubjectSubject TEST : 1
	 * ReplaySubjectSubject TEST : 2
	 * ReplaySubjectSubject TEST : 3
	 * ReplaySubjectSubject TEST : 4
	 * ReplaySubjectSubject TEST : 5
	 * ReplaySubjectSubject TEST : 6
	 * ReplaySubjectSubject TEST : 7
	 * ReplaySubjectSubject TEST : 8
	 * ReplaySubjectSubject TEST : 9
	 * ReplaySubjectSubject TEST : 10
	 * ReplaySubjectSubject TEST : 11
	 * ReplaySubjectSubject TEST : 12
	 * ReplaySubjectSubject TEST : 12
	 * ReplaySubjectSubject TEST : 13
	 * ReplaySubjectSubject TEST : 13
	 * ReplaySubjectSubject TEST : 14
	 * ReplaySubjectSubject TEST : 14
	 * ReplaySubjectSubject TEST : 15
	 * ReplaySubjectSubject TEST : 15
	 * ReplaySubjectSubject TEST : 16
	 * ReplaySubjectSubject TEST : 16
	 * ReplaySubjectSubject TEST : 17
	 * ReplaySubjectSubject TEST : 17
	 * ReplaySubjectSubject TEST : 18
	 * ReplaySubjectSubject TEST : 18
	 * ReplaySubjectSubject TEST : 19
	 * ReplaySubjectSubject TEST : 19
	 * ReplaySubjectSubject TEST : 20
	 * ReplaySubjectSubject TEST : 20
	 * result 1 - onComplete
	 * result 2 - onComplete
	 */
	public void replaySubjectSubjectExample()
	{
		replaySubjectSubject = ReplaySubject.create();

		Observable<String> observable = replaySubjectSubject
				.map(new Func1<Integer, String>() {
					@Override
					public String call(Integer num) {
						String nextTitle = "ReplaySubjectSubject TEST : " + (num + 1);					
						return nextTitle;
					}
				});

		observable.subscribe(result1);

		for(int i = 0; i < 20; i ++) 
		{
			replaySubjectSubject.onNext(i);

			if(i == 10)
				observable.subscribe(result2);
		}

		replaySubjectSubject.onCompleted();
	}

	Subscriber<String> result1 = new Subscriber<String>() {

		@Override
		public void onNext(String text) {
			tvLogMessage.setText(text);
			Log.d("RX Test result1 : ", text);
		}

		@Override
		public void onError(Throwable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompleted() {
			Log.d("RX Test : ", "result 1 - onComplete");
		}
	};

	Subscriber<String> result2 = new Subscriber<String>() {

		@Override
		public void onNext(String text) {
			tvLogMessage.setText(text);
			Log.d("RX Test result2 : ", text);
		}

		@Override
		public void onError(Throwable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompleted() {
			Log.d("RX Test : ", "result 2 - onComplete");
		}
	};
}
