package com.jpark.rxbasic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.Observable;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    TextView text1,text2,text3;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView)findViewById(R.id.textview);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView)findViewById(R.id.textView3);


        // 실제 Task를 처리하는 객체(발행자)
        Observable<String> simpleObservable =
                Observable.create(subscriber -> {
                    // 네트워크을 통해서 데이터를 긁어온다
                    // 반복문을 돌면서 호출
                    for(int i = 0;i < 3; i++) {
                        subscriber.onNext("Hello Rx"+i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //---------------------------------
                    subscriber.onCompleted();
                });

        // 옵저버(구독자)를 등록해주는 함수
        simpleObservable
                .subscribeOn(Schedulers.io()) //발행자를 별도의 io Thread에서 동작 시킨다.
                .observeOn(AndroidSchedulers.mainThread()) //
                .subscribe(new Subscriber<String>() {
                // observer(구독자)
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "[observer1]complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "[observer1]error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(String text) {
                        text1.setText(text);
                    }
                });

        simpleObservable.subscribeOn(Schedulers.io()) //발행자를 별도의 Thread에서 동작 시킨다.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(string -> text2.setText(string)
                , throwable -> Log.e(TAG, "[observer2]error: " + throwable.getMessage())
                , () -> Log.d(TAG, "[observer2]complete!"));

        simpleObservable.subscribeOn(Schedulers.io()) //발행자를 별도의 Thread에서 동작 시킨다.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(string -> text3.setText(string)
                , throwable -> Log.e(TAG, "[observer3]error: " + throwable.getMessage())
                , () -> Log.d(TAG, "[observer3]complete!"));
    }
}
