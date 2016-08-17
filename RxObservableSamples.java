package com.albert.study.rx;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
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

public class RxObservableSamples extends BaseLoadingActivity {

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

		//		observableCreate();
//		observableDefer();
		observableFrom();
	}

	/**
	 * Operators / Creating / From
	 * 
	 * Observables을 생성해 from에서 파라미터로 지정한 item을 하나씩 emit.
	 */
	public void observableFrom() {
		Integer[] items = {0, 1, 2, 3, 4, 5};

		Observable<Integer> fromObservable = Observable.from(items);

		fromObservable.subscribe(
				new Action1<Integer>() {
					@Override
					public void call(Integer item) {
						System.out.println("Next: " + item);
						tvLogMessage.setText("Number is " + item);
					}
				}, 
				new Action1<Throwable>() {
					@Override
					public void call(Throwable error) {
						// TODO Auto-generated method stub
						System.out.println("Error encountered: " + error.getMessage());
					}

				},
				new Action0() {
					@Override
					public void call() {
						// TODO Auto-generated method stub
						System.out.println("Sequence complete");
					}
				}
		);
	}

	/**
	 * Operators / Creating / Defer
	 * 
	 * 각 옵저버에 대한 새로운 Observable을 만들어서 사용.
	 */
	public void observableDefer() {
		Observable.defer(new Func0<Observable<Integer>>() {

			@Override
			public Observable<Integer> call() {
				return Observable.just(1, 2, 3);
			}
		})
		.subscribe(new Subscriber<Integer>() {
			@Override
			public void onNext(Integer item) {
				System.out.println("Next: " + item);

				tvLogMessage.setText("Number is " + item);
			}

			@Override
			public void onError(Throwable error) {
				System.err.println("Error: " + error.getMessage());
			}

			@Override
			public void onCompleted() {
				System.out.println("observableDefer complete.");
			}
		});
	}


	/**
	 * Operators / Creating / Create
	 * 
	 * Observables를 만들어서 어떤작업을 어떻게 할것인지 onNext, onCompleted 구현해서 Observable을 생성한다.
	 * 이후에 map 등으로 데이터를 변환 시킬수 있고, subscribe를 구현해서 create된 observable을 구독한다.
	 */
	public void observableCreate() {

		// create 기본에서 map 적용
		Observable.create(new OnSubscribe<Integer>() {
			@Override
			public void call(Subscriber<? super Integer> observer) {
				try {
					if (!observer.isUnsubscribed()) {
						for (int i = 0; i < 5; i++) {
							observer.onNext(i);
						}
						observer.onCompleted();
					}
				} catch (Exception e) {
					observer.onError(e);
				}
			}
		})
		.map(map)
		.subscribe(result1);

		// create 기본 
		//		Observable.create(new OnSubscribe<Integer>() {
		//			@Override
		//			public void call(Subscriber<? super Integer> observer) {
		//				try {
		//					if (!observer.isUnsubscribed()) {
		//						for (int i = 1; i < 5; i++) {
		//							observer.onNext(i);
		//						}
		//						observer.onCompleted();
		//					}
		//				} catch (Exception e) {
		//					observer.onError(e);
		//				}
		//			}
		//		})
		//		.subscribe(new Subscriber<Integer>() {
		//			@Override
		//			public void onNext(Integer item) {
		//				System.out.println("Next: " + item);
		//
		//				tvLogMessage.setText("Number is " + item);
		//			}
		//
		//			@Override
		//			public void onError(Throwable error) {
		//				System.err.println("Error: " + error.getMessage());
		//			}
		//
		//			@Override
		//			public void onCompleted() {
		//				System.out.println("observableCreate complete.");
		//			}
		//		});
	}

	Func1<Integer, String> map = new Func1<Integer, String>() {
		@Override
		public String call(Integer num) {
			String nextTitle = "Observables TEST : " + (num + 1);					
			return nextTitle;
		}
	};

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
}
