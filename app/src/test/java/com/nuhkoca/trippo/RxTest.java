package com.nuhkoca.trippo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;

public class RxTest {

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void someTests() {
        /*getNames()
                .subscribeOn(Schedulers.io())
                .flatMap(s -> getVars())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> System.out.print(s + "\n"));

        getNames()
                .subscribeOn(Schedulers.io())
                .concatMap(s -> getVars())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> System.out.print(s + "\n"));*/

        filter();

        assertTrue("s", true);
    }

    private Observable<String> getNames() {
        List<String> name = new ArrayList<>();

        name.add("a");
        name.add("bb");
        name.add("ccc");
        name.add("dddd");
        name.add("eeeee");
        name.add("ffffff");
        name.add("ggggggg");

        return Observable.fromIterable(name);
    }

    private Observable<String> getVars() {
        List<String> vars = new ArrayList<>();

        vars.add("a");
        vars.add("bb");
        vars.add("ccc");
        vars.add("dddd");
        vars.add("eeeee");
        vars.add("ffffff");
        vars.add("ggggggg");

        return Observable.fromIterable(vars);
    }

    private void filter() {
        Observable.fromArray("a", "b")
                .subscribeOn(Schedulers.io())
                .filter(s -> !s.equals(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(System.out::print);
    }
}