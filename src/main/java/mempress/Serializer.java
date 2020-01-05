package mempress;

public interface Serializer {

    ClassData ser(Object obj);

    Object des(ClassData cd);

    default ClassData fastForward(ClassData o) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
