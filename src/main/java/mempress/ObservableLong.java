package mempress;

import com.google.common.base.Preconditions;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Bartek on 2014-12-05.
 */
public class ObservableLong {
    private List<Observer> observerList = new LinkedList<>();
    private long value;
    private boolean useThreads;
    private ExecutorService service;

    public ObservableLong() {
        this(0, true);
    }

    public boolean addListener(Observer o) {
        Preconditions.checkNotNull(o);
        return observerList.add(o);
    }

    public ObservableLong(long startValue, boolean notifyInAnotherThread) {
        value = startValue;
        if(notifyInAnotherThread) {
            useThreads = notifyInAnotherThread;
            service = Executors.newSingleThreadExecutor();
        }
    }

    private void notifyObservers() {
        if(useThreads) {
            service.submit(() -> {
                for(Observer o : observerList)
                    o.update(null, ObservableLong.this);
            });
        } else {
            for(Observer o : observerList)
                o.update(null, this);
        }
    }

    public long get() {
        return value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
        notifyObservers();
    }

    public void add(long v) {
        value += v;
        notifyObservers();
    }

    public void subtract(long v) {
        value -= v;
        notifyObservers();
    }

    public void subtractWithoutNotify(long v) {
        value -= v;
    }
}
