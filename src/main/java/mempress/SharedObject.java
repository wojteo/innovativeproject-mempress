package mempress;

import com.google.common.base.Preconditions;

/**
 * Created by Bartek on 2014-12-19.
 */
public class SharedObject<E> {
    private final MutableListElement<E> sharedObject;
    private volatile boolean blocked = false;

    public SharedObject(MutableListElement<E> sharedObject) {
        Preconditions.checkNotNull(sharedObject);
        this.sharedObject = sharedObject;
        sharedObject.incrementUseCount();
    }

    public E get() throws IllegalAccessException {
        if (!blocked)
            return sharedObject.getShared();
        else
            throw new IllegalAccessException();
    }

    public void release() {
        if (!blocked) {
            blocked = true;
            sharedObject.decrementUseCount();
        }
    }

    public boolean isReleased() {
        return blocked;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }
}
